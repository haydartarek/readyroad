# ReadyRoad Free Infrastructure Runbook

Last reviewed: 2026-07-23
Status: Active development and production-preview procedure

## Scope

This runbook governs ReadyRoad while it remains entirely on the current free
infrastructure:

```text
Frontend: Vercel Free
Backend: Render Free
Database: Supabase Free
Domain: provider URLs only
SMTP: deferred
```

It authorizes no purchase, plan upgrade, provider migration, custom domain, DNS
change, or SMTP activation. Those actions require a separate milestone and
explicit owner approval.

## Current Release Baseline

```text
Backend runtime:
  Branch: feature/postgresql-supabase
  Commit: e778d013c70ff8944d431a66a94d59e6e0f8e2bd
  Note: later documentation-only commits do not require a Render deployment

Frontend/mobile repository:
  Branch: feature/postgresql-supabase
  Commit: 8764dc696ea520cf4b37f7a59f51112a21f78360

Frontend URL:
  https://readyroad-frontend-haydar.vercel.app

Backend URL:
  https://readyroad-backend-haydar.onrender.com

Backend health:
  https://readyroad-backend-haydar.onrender.com/actuator/health
```

The two worktrees were clean and synchronized with their remote branches on
2026-07-23. Backend verification passed for the deployed runtime commit. Web
and Docker GitHub Actions checks passed for the frontend commit above; the
latest mobile check remained green because these web-only changes did not
trigger or modify the mobile application.

## Limitation Classification

Record the following as `Infrastructure Limitation`, not `Application Bug`,
unless logs or a reproducible warm-runtime test prove a software defect:

- Render sleep and cold-start latency.
- A Render request timing out while the service is waking.
- A Vercel deployment timing out because static generation waits for a sleeping
  Render backend.
- Render Free outbound SMTP restrictions.
- Free-plan deployment queue or build delay.
- Supabase Free pause, quota, retention, or backup limits.
- Vercel Free quota or provider-side deployment delay.

Do not change application behavior merely to hide one of these limitations.

## Daily Validation

Run from PowerShell:

```powershell
$frontend = "https://readyroad-frontend-haydar.vercel.app"
$backend = "https://readyroad-backend-haydar.onrender.com"

Invoke-WebRequest -UseBasicParsing "$frontend/" -TimeoutSec 30
Invoke-WebRequest -UseBasicParsing "$frontend/api/health" -TimeoutSec 30
Invoke-WebRequest -UseBasicParsing "$frontend/robots.txt" -TimeoutSec 30
Invoke-WebRequest -UseBasicParsing "$frontend/sitemap.xml" -TimeoutSec 30

# A cold Render wake may exceed this window. Record that separately.
Invoke-WebRequest -UseBasicParsing "$backend/actuator/health" -TimeoutSec 180
```

After the backend reports `UP`, validate only public, read-only canaries:

```powershell
Invoke-WebRequest -UseBasicParsing "$backend/api/traffic-signs" -TimeoutSec 60
Invoke-WebRequest -UseBasicParsing "$backend/api/lessons" -TimeoutSec 60
Invoke-WebRequest -UseBasicParsing "$backend/api/quiz/random" -TimeoutSec 60
```

Do not use production administrator credentials in automated or public
monitoring. Protected routes should return `401` or `403` when tested without
credentials.

## Render Wake Procedure

1. Request `/actuator/health` with a maximum wait of 180 seconds.
2. If it times out, inspect the Render service status and latest deployment
   logs.
3. If the service is starting, wait for startup to finish; do not start another
   deployment.
4. If the service is suspended or a free-plan limit is active, record an
   infrastructure limitation and stop.
5. If startup logs show an application exception, Flyway failure, or database
   error, treat it as a deployment blocker and diagnose before retrying Vercel.
6. Once health is `UP`, run the three public canaries above.

Repeated restart or redeploy attempts can extend downtime and obscure the
original logs. Perform at most one controlled retry after identifying the
reason for failure.

## Vercel Deployment Procedure

Before deploying:

1. Confirm the exact frontend commit has passing GitHub Actions.
2. Confirm the Render backend is warm and its public canaries pass.
3. Confirm Vercel environment variable names exist without printing values:
   `BACKEND_URL`, `NEXT_PUBLIC_API_BASE_URL`, and `NEXT_PUBLIC_APP_URL`.
4. Confirm the project root is `web_app`.

List production deployments:

```powershell
cd C:\Users\haydar\Desktop\end_project\readyroad_front_end\web_app
npx --yes vercel@latest ls readyroad-frontend-haydar --prod
```

If a deployment failed only because Render was asleep, rebuild the same
deployment after Render is healthy:

```powershell
npx --yes vercel@latest redeploy <deployment-url>
```

Do not modify source, SEO behavior, route structure, or API contracts solely to
avoid a free-tier cold start.

## Vercel Rollback

The production alias should continue serving the previous Ready deployment when
a new deployment fails before promotion. Verify the alias before taking action.

To roll back after a faulty deployment has become current:

```powershell
npx --yes vercel@latest rollback <known-good-deployment-url> --yes
```

Then verify:

```text
/
/api/health
/robots.txt
/sitemap.xml
/login
/traffic-signs
/lessons
```

Rollback changes only the active Vercel deployment. It must not change Supabase
data, Flyway history, Render environment variables, or repository history.

## Supabase Validation

Use the provider dashboard or an approved read-only connection to verify:

- Project status is healthy.
- Connection use remains within the free-plan limit.
- Flyway history contains only successful rows.
- Canonical counts remain 184 signs, 1,472 sign questions, and 184 sign exams.
- General quiz counts remain 50 questions and 131 options.
- Duplicate canonical identifiers remain zero.

Do not run manual data repair against the active database. Canonical data
changes must continue through the tracked importer and migrations.

## Release Evidence

For every validation or deployment, record:

```text
Timestamp
Backend commit
Frontend/mobile commit
GitHub Actions run URLs
Vercel deployment URL and state
Render deployment state
Backend health result
Public canary results
Supabase health/count result
Regression result
Infrastructure limitations
Remaining blockers
Rollback target
```

Never record environment-variable values, credentials, tokens, cookies, reset
links, or database connection strings.

## Deferred Items

The following remain intentionally deferred:

```text
Hosting or domain purchase
Hetzner or VPS migration
Custom domain, DNS, and custom SSL migration
SMTP/Brevo activation
Supabase Pro
Paid monitoring
Production backup purchase
Advanced production security hardening
Secret rotation and Git history cleanup
```

Their deferral does not authorize bypasses or temporary secret storage in Git.
