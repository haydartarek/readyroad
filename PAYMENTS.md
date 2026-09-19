# Rijvia one-time Checkout V1

Production and local database: **PostgreSQL 17.6**, schema **readyroad** (confirmed by the owner).
The production path is `db/migration-postgresql`; V66 creates `purchases`,
`stripe_webhook_events`, and `user_entitlement`. No MySQL migration is added.
Existing demo and exam logic is unchanged.

Use only the owner's verified Stripe account: **Layan Garage BV**,
**layanahmed.be@gmail.com**, account **acct_1UDqUv3UludJRB2s**.
Never select a different account when preparing CLI credentials or prices.

## Required runtime configuration

Inject all six variables through the environment or deployment secret store:

| Variable | Meaning |
| --- | --- |
| `STRIPE_SECRET_KEY` | Server-only Stripe key for the verified account. Prefer a restricted key with Checkout Sessions write access and the required catalog permissions. |
| `STRIPE_WEBHOOK_SECRET` | Signing secret for this environment's webhook destination or local listener. |
| `STRIPE_PRICE_ID_3_DAYS` | Existing one-time Price granting 3 days. |
| `STRIPE_PRICE_ID_1_WEEK` | Existing one-time Price granting 7 days. |
| `STRIPE_PRICE_ID_4_WEEKS` | Existing one-time Price granting 28 days. |
| `APP_BASE_URL` | Absolute frontend HTTP(S) origin, without credentials, path, query, or fragment. |

Startup fails with the missing variable's **name**, never its value. Do not put keys
in source, `NEXT_PUBLIC_*`, command logs, or committed environment files. Prices
and keys must belong to the same environment. Existing live prices must not be
used in test mode. This implementation does not create products, change prices,
enable tax, or configure subscriptions.

Keep `POSTGRES_SCHEMA=readyroad`; the PostgreSQL profile sets both JDBC
`currentSchema` and Hibernate/Flyway default schema from it. Native conflict-safe
inserts use that connection search path. Instants use `TIMESTAMPTZ` and Hibernate
`hibernate.jdbc.time_zone=UTC`; the API presents expiry in `Europe/Brussels`.

The local Compose file forwards payment variables from its environment. The
production Compose file already reads `.env.production`; configure deployment
secrets separately through the existing owner-controlled release process.

## Local setup with Stripe CLI

1. Install the official Stripe CLI if absent. Authenticate only to the verified
   account. Run `stripe whoami --format json` and confirm the account ID above
   before any account operation. Do not print CLI configuration or keys.
2. Select **test mode on the same account**, using test prices and a test key.
   This does not authorize a live payment. Set `APP_BASE_URL` to your local
   frontend origin, for example `http://localhost:3000`.
3. Start the listener:

   ```powershell
   stripe listen --forward-to http://localhost:3000/api/stripe/webhook --events checkout.session.completed,checkout.session.async_payment_succeeded,checkout.session.async_payment_failed,checkout.session.expired
   ```

   Supply the listener's signing secret as `STRIPE_WEBHOOK_SECRET` through a secure
   local environment. Do not paste it into chat or version control. The listener
   must stay running. A Dashboard destination secret is not interchangeable with
   this listener secret.
4. After all six variables are present, rebuild the changed local application:

   ```powershell
   cd "C:\Users\haydar\Desktop\end_project\readyroad"
   docker compose -f docker-compose.yml -f "C:\Users\haydar\Desktop\end_project\docker-compose.snapshot.local.yml" up -d --build
   docker compose -f docker-compose.yml -f "C:\Users\haydar\Desktop\end_project\docker-compose.snapshot.local.yml" ps
   ```

5. Sign into Rijvia and open `/ar/plans`, `/nl/plans`, `/fr/plans`, or `/plans`
   (English uses the existing canonical unprefixed routing). The account sidebar
   also links to plan selection. The price is displayed by Stripe before payment;
   the application does not invent or hardcode a price.
6. Complete a Stripe test payment. The return page polls every 1.5 seconds, for at
   most 15 seconds, and shows “still confirming” if necessary. It never activates
   access from a query string. Test the asynchronous and duplicate-delivery cases
   below before any separately approved production release.

Generic `stripe trigger` sessions have no Rijvia purchase and are deliberately
rejected with 503 so an early delivery can be retried. Exercise Checkout through
the application, then resend its corresponding events when testing retries.

## Contracts and concurrency

- `POST /api/checkout`: JWT principal owns `{plan, clientRequestId}`. The existing
  frontend language context supplies an allowlisted `Accept-Language` value.
  Another owner receives 404; another plan on the same request receives 409.
- The purchase commits before the Stripe call. Session creation/retries lock the
  purchase. The original locale and UUID survive ambiguous network failures;
  the Stripe Idempotency-Key is the original client request ID. Stored Checkout
  URL/session is reused. Ambiguous requests older than 23 hours require a new
  request, before Stripe's minimum 24-hour idempotency retention can elapse.
- `POST /api/stripe/webhook`: anonymous **only with a valid Stripe signature**.
  The frontend forwards raw bytes and the signature to the backend. Maintenance
  mode does not block this endpoint. Verification happens before JSON parsing.
- Events use atomic PostgreSQL `ON CONFLICT DO NOTHING`. Event registration,
  purchase status and entitlement changes commit or roll back together. Each
  purchase is locked; activation locks the existing User row **before** loading
  or creating the entitlement. Two different purchases cannot lose extensions.
- `GET /api/purchases/{id}/status`: authenticated owner only. PENDING/FAILED return
  null expiry. PAID returns the user's current entitlement expiry with Brussels
  offset. `session_id` is never used for authorization or activation.
- REFUNDED is reserved and is never entered or exited. A failure cannot downgrade
  PAID; `payment_intent.payment_failed` and all unhandled events are IGNORED.
- `/checkout/expired` and invalid local success links show a generic expiry
  message. An old application checkout attempt returns 410 and starts fresh on
  the next click. A link opened directly on Stripe's domain displays **Stripe's**
  expiry page; Rijvia cannot replace text on an externally hosted Stripe page.

## Targeted automated validation

Tests never contact a real Stripe account. Test-only configuration in
`src/test/resources/application.properties` supplies unmistakable offline fixture
values for Spring contexts, keeping existing PostgreSQL CI contexts bootable.
Production artifacts do not include that file. These fixtures do not establish
a live or test-mode Stripe integration.

```powershell
# Backend: only payment unit/security/HTTP tests
mvn '-Dtest=PaymentConfigurationTest,PaymentControllerTest,StripeWebhookControllerTest,PaymentSecurityTest,StripeCheckoutGatewayTest' test
# Real PostgreSQL 17.6 + complete migration path, isolated Testcontainers DB
mvn '-Dit.test=PaywallPostgreSqlIntegrationTest' test-compile failsafe:integration-test failsafe:verify
# Frontend, from readyroad_front_end/web_app
npm test -- --runInBand --runTestsByPath src/components/payment/payment-flow.test.tsx src/services/paymentService.test.ts src/app/api/stripe/webhook/route.test.ts
npx playwright test tests/e2e/payment-flow.spec.ts --project=chromium --workers=1
```

The Playwright suite runs the actual Next.js routes with controlled Stripe/API
responses, including Arabic RTL at mobile and desktop widths and all four
locales. It complements the real-database tests; it does not replace a real Stripe
test-mode checkout with the owner's configured keys.

Initial local validation on 2026-09-18: **24 backend unit/security/HTTP tests, 18 real
PostgreSQL integration cases, 16 frontend tests and 11 Playwright cases passed**.
Targeted TypeScript validation and ESLint passed. Subsequent owner-review fixes
passed 30 backend unit/security/HTTP tests and 32 PostgreSQL integration cases;
the canonical-route Playwright run passed all 11 cases.

The real Sandbox end-to-end payment passed on 2026-09-18, using the authorized
`acct_1UDqUv3UludJRB2s` account and the owner's securely configured runtime values.
Checkout explicitly sends `managed_payments[enabled]=false`: the account default
otherwise rejected the product because it has no Managed Payments tax code.
All 4 gateway cases passed with this parameter, and only the local backend was
rebuilt. A fresh checkout attempt was needed because Stripe rejected reuse of the
earlier idempotency key after the request parameters changed.

- Plan: `RIJVIA_3_DAYS`, EUR 2.99, `livemode=false`, test card ending 4242.
- Purchase: `e606fd9c-da61-4948-9033-2a8fdf2493a0`, status `PAID`.
- Event: `evt_1UGqor3UludJRB2sjGbfMld8`, `checkout.session.completed`, `PROCESSED`.
- The original Stripe CLI listener reported HTTP 200 for this exact event.
- Rijvia returned to the Arabic success route and displayed payment confirmation.
- Entitlement: `ACTIVE`, expires `2026-09-21T01:42:29.386081Z`
  (`2026-09-21 03:42` Europe/Brussels), exactly 3 days after activation.

No credentials or account defaults were changed. No live payment, commit, push,
or production deployment was performed.

```gherkin
Feature: Rijvia asynchronous Checkout safety

Scenario: Checkout completed but payment is still pending
  Given a PENDING Purchase
  When checkout.session.completed arrives with payment_status unpaid
  Then Purchase remains PENDING
  And entitlement is not activated

Scenario: Delayed payment succeeds
  Given checkout.session.completed previously arrived unpaid
  When checkout.session.async_payment_succeeded arrives
  Then Purchase becomes PAID
  And entitlement becomes ACTIVE exactly once

Scenario: Delayed payment fails
  Given a PENDING Purchase
  When checkout.session.async_payment_failed arrives
  Then Purchase becomes FAILED
  And entitlement remains unchanged

Scenario: Expired Checkout
  Given a PENDING Purchase
  When checkout.session.expired arrives
  Then Purchase becomes FAILED
  And entitlement remains unchanged

Scenario: Failure event cannot downgrade success
  Given Purchase is already PAID
  When checkout.session.async_payment_failed arrives later
  Then Purchase remains PAID
  And entitlement expiry remains unchanged

Scenario: Duplicate webhook delivery
  Given a stripe_event_id already processed
  When the same event is redelivered
  Then return 200 without re-applying business logic

Scenario: Client request ownership
  Given clientRequestId belongs to another user
  When the requesting user retries the same clientRequestId
  Then the request is rejected and no purchase is exposed or reused

Scenario: Same client, different plan
  Given a PENDING purchase with clientRequestId X and plan A
  When the same user calls checkout again with clientRequestId X and plan B
  Then the response is 409 Conflict

Scenario: Concurrent first-time entitlement creation
  Given a user with no existing user_entitlement row
  When two webhook events for two different purchases of that user arrive concurrently
  Then exactly one entitlement row is created
  And both extensions apply correctly in sequence
```

## Rollback and release boundary

V66 is additive. Roll back application binaries if required, keeping financial
and webhook records intact; do not drop tables or rewrite applied migrations.
No commit, push, CI run, production migration, live charge, or deployment is
part of local implementation approval.
