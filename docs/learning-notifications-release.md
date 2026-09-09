# Learning Notifications: Release Gate

## Scope

- Same backend/frontend repositories and existing main branches.
- PostgreSQL V65 adds notification intents, per-channel delivery records,
  email preferences and browser subscriptions. No historical backfill.
- The source event and its notification intent share a transaction.
  A failed intent write must not leave a committed source event without an intent.
- Worker materialization and delivery fan-out are atomic and use row locks.
  Failed attempts remain pending with bounded exponential retries.
- Email/Push delivery is at-least-once, not a claim of guaranteed receipt:
  SMTP/provider acceptance can succeed immediately before a process crash.
  Push uses a stable notification tag to limit visible duplicates.
- Reminders use the latest persisted learning activity (registration is the
  fallback), 24 hours inactivity and a 24-hour reminder cooldown. The scheduler
  checks every minute in UTC. This does not claim to track every browser visit.
- Weak-area notifications use cumulative category answers, minimum five answers
  and strictly less than 60% accuracy. Learning history is not backfilled.

## Configuration

Apply V65 through the existing Flyway PostgreSQL release process only.
The local production-mirror profile disables Flyway: do not point a schema
update command at its shared database. Validate with the targeted Testcontainer.

- LEARNING_NOTIFICATION_OUTBOX_ENABLED: defaults true under postgresql.
- LEARNING_NOTIFICATION_EMAIL_ENABLED: defaults false; enable only after
  validating the existing SMTP configuration and sender.
- LEARNING_WEB_PUSH_PUBLIC_KEY / LEARNING_WEB_PUSH_PRIVATE_KEY:
  an existing matching VAPID P-256 key pair, or a newly provisioned pair if none
  exists. Keep the private key in the existing secret configuration, never Git.
- LEARNING_WEB_PUSH_SUBJECT: defaults mailto:info@rijvia.be.
- Reuses app.mail.from and app.frontend.url.

The release script optionally merges `/opt/readyroad/secrets/learning-notifications.env`
into the new candidate environment. The file must be root-owned mode 0600;
only the five notification settings above are accepted. The current release
and rollback environment are not edited. Keep this file out of Git and logs.

Learners must explicitly opt in. Email requires a verified address. Push also
requires browser permission and HTTPS (localhost is supported for testing).
Disabling a subscription stops this browser's delivery even during API failure.
No real messages were sent by the unit tests. Crypto tests generate ephemeral
test keys only. Configuring keys is not evidence of successful delivery.

## Required Verification

1. Targeted backend tests, including LearningNotificationTransportTest and
   LearningNotificationOutboxPostgreSqlIntegrationTest.
2. Verify concurrent reminder deduplication, transaction rollback, persistence
   retry and independent email/Push retries in PostgreSQL.
3. Targeted quiz forms, notification context/panel, channel settings and worker tests.
4. TypeScript, scoped ESLint and git diff --check.
5. On localhost:3000, verify opted-out state, opt-in, browser permission denial,
   successful email/Push receipt, opt-out, logout and all four locales.
6. Release/CI/deployment is authorized by the Owner; execute only after validation.

## Rollback

Keep V65 tables and pending records. Do not drop queues or edit Flyway history.
The previous application ignores these additive tables. Pending deliveries
remain preserved until a compatible worker is restored. Set the outbox flag
false to pause the new worker when required; that also restores the previous
direct in-app persistence path and does not provide external delivery.
