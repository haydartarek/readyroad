# ReadyRoad Current Production Architecture

Last reviewed: 2026-07-23
Status: Active free-infrastructure development baseline

## Scope

This document records the current production topology and measured operating
characteristics. It contains no credentials and authorizes no production
change.

## Topology

```text
Browser / Mobile
      |
      +--> Vercel: Next.js frontend
              |
              +--> Render: Spring Boot backend
                        |
                        +--> Supabase: PostgreSQL, schema readyroad
                        +--> SMTP: deferred on Render Free

GitHub Actions --> build and test only
GitHub repositories --> source of release artifacts
```

## Service Inventory

| Service | Current provider/plan | Region | Purpose | Runtime and limits | Persistence/backups | Known limitations | Cost |
|---|---|---|---|---|---|---|---|
| Frontend | Vercel Free | Provider-managed | Next.js web application | Next.js 16.2.10, server-rendered routes and middleware | Git-backed deployments; provider logs/retention depend on plan | Free-plan quotas and provider deployment delays | USD 0/month |
| Backend | Render Free | Frankfurt | Spring Boot API | 0.1 CPU, 512 MB RAM; sleeps after inactivity | Ephemeral filesystem; no production-grade persistent application storage | Cold starts, SMTP ports blocked, 512 MB memory pressure, free tier is not intended for production | USD 0/month |
| Database | Supabase Free PostgreSQL | eu-west-1 configuration | PostgreSQL schema `readyroad` | PostgreSQL 17.6; direct limit observed as 60 connections; application pool max 5 | Free tier has no managed automatic backup guarantee suitable for production | Free projects may pause; backup and log retention are limited | USD 0/month |
| CI/CD | GitHub Actions | Provider-managed | Backend, web, mobile and release checks | Maven, Node, Playwright, Flutter and Docker builds | Workflow logs/artifacts according to GitHub plan | No protected production deployment job or immutable production image flow | Current account cost not measured |
| Source | GitHub, two public repositories | Provider-managed | Backend and frontend/mobile source | Default branch is `main`; current work is on `feature/postgresql-supabase` | Git history retained by GitHub | Release baseline differs from current local work; historical `.env` commits exist | Current account cost not measured |
| Domain | None owned or connected | N/A | Future public identity | `readyroad.be` returned AVAILABLE from DNS Belgium on 2026-07-22 | Not reserved | Availability can change until purchased | Not purchased |
| Email | Deferred | N/A | Password reset and transactional email | Application flow exists; delivery unavailable from current Render Free environment | No active delivery provider | Render Free blocks outbound SMTP ports 25, 465 and 587 | USD 0/month |

## Production URLs

```text
Frontend: https://readyroad-frontend-haydar.vercel.app
Backend:  https://readyroad-backend-haydar.onrender.com
Health:   https://readyroad-backend-haydar.onrender.com/actuator/health
```

## Measured Backend Characteristics

Measurements were taken on 2026-07-22. They are observations, not service
level guarantees.

| Measurement | Result |
|---|---|
| Backend Docker image content size | 224,691,752 bytes, about 214 MiB |
| Local image display size | About 618 MB unpacked |
| Frontend Docker image content size | 92,919,392 bytes, about 88.6 MiB |
| 0.5 CPU / 512 MB healthy time | About 126 seconds |
| 0.5 CPU / 512 MB Spring startup | 82.09 seconds |
| 0.5 CPU / 512 MB steady memory | 445.3 MiB, 86.97 percent |
| 1 CPU / 1 GiB healthy time | About 52 seconds |
| 1 CPU / 1 GiB Spring startup | 30.988 seconds |
| 1 CPU / 1 GiB steady memory | 414.4 MiB, 40.47 percent |
| Render cold request | Timed out after 120 seconds during one wake-up observation |
| Render Free deployment on 2026-07-23 | Spring startup 245.901 seconds; canonical quiz reconciliation about 244 seconds; deployment live about 10 minutes after build start |
| Render warm health | 143-255 ms |
| Render warm traffic signs | 5.6-6.2 seconds, 511,381 bytes |
| Render warm random quiz | 366-723 ms, about 14.4 KB |
| Render warm lessons | 1.1-1.2 seconds, 26,168 bytes |
| Local warm traffic signs at 0.5 CPU | 789 ms |
| Local warm random quiz at 0.5 CPU | 576 ms |
| Local warm lessons at 0.5 CPU | 303 ms |

Startup includes distinct stages: JVM/Spring initialization, Flyway validation,
database connection establishment, lesson reconciliation, and canonical sign
quiz reconciliation. Render wake-up time adds another external delay. Gate A
does not change this logic.

## Measured Frontend Characteristics

| Measurement | Result |
|---|---|
| Local production build | Passed in about 23.5 seconds |
| Static generation work items | 50 |
| Standalone output | 25.32 MiB |
| Static assets | 3.53 MiB |
| Server output | 27.65 MiB |
| Public homepage response | 200, 112,871 bytes, 459 ms in one observation |
| Homepage cache behavior | `x-vercel-cache: MISS`; private/no-store response |
| Route profile | Most application routes are dynamic/server-rendered; robots, sitemap and Open Graph output include static generation |

The ignored local `.next` directory was much larger because it contained
accumulated development output. It is not a deployment-size measurement.

## Measured Database Characteristics

| Measurement | Result |
|---|---|
| PostgreSQL version | 17.6 |
| Database size | About 23 MB |
| `readyroad` schema relations | About 11 MB |
| Base tables | 42 |
| Indexes | 158 |
| Application pool | Max 5, min 1 |
| Observed JDBC idle connections | 4 |
| Latest Flyway version | 10, successful |
| Traffic signs | 184 |
| Sign questions | 1,472 |
| Sign exams | 184 |
| General quiz questions | 50 |
| General quiz options | 131 |
| Duplicate canonical identifiers | 0 in the audited key sets |
| Direct all-sign query execution | About 21 ms |
| Direct random-quiz query execution | About 2.5 ms |

The production database contained 2 user rows and no quiz attempts, exam
simulations, sign exam results, or user question history at audit time. There
is not enough production activity to derive a real capacity trend.

## Expected Usage Envelope

The following numbers are conservative planning estimates, not measured usage.
They require production analytics after launch.

| Requirement | 12-month planning estimate |
|---|---|
| Monthly active users | Up to 5,000 |
| Peak concurrent users | 25-50 |
| API requests | 0.5-1.5 million/month |
| API and asset egress | 25-75 GB/month |
| Database growth | 20-50 MB/month under active learning history |
| Transactional email | 1,000-3,000/month |
| Deployment frequency | Weekly to biweekly, with manual production approval |
| Availability target | 99.5 percent initial target |
| Cold-start tolerance | None for authenticated production workflows |
| Initial RPO/RTO | RPO 24 hours; RTO 2 hours |

The 511 KB traffic-sign response materially affects the egress estimate and
should be observed after launch. Optimizing the API is outside this milestone.

## Current CI and Release State

- Backend CI covers Maven test, verify, package and Docker build.
- Web CI covers Jest, TypeScript, lint, Next.js build, Playwright and Docker.
- Mobile CI covers formatting, analysis, tests and debug APK.
- Release workflow exists, but there is no protected, approved production
  deployment using an immutable image tag.
- Recommended future release tags are `vX.Y.Z` and the Git commit SHA. Production
  must not rely on `latest` alone.
- Backend commit `e778d013c70ff8944d431a66a94d59e6e0f8e2bd` and
  frontend/mobile commit `8bf419a110681ab5fb34b31955c1e07ce85de109`
  are pushed on `feature/postgresql-supabase`, with clean synchronized
  worktrees and passing GitHub Actions as observed on 2026-07-23.
- Render is live on backend commit `e778d01`. Its deployment completed with
  successful canonical reconciliation and zero importer errors.
- An initial Vercel deployment of frontend commit `8bf419a` failed during
  static generation because its sleeping Render dependency did not respond
  within the build timeout. After Render was warm, the same deployment was
  rebuilt without a source or environment change and became Ready at
  `readyroad-frontend-haydar-4q4e25ee6.vercel.app`; the public Vercel alias was
  updated successfully. The initial failure is classified as an infrastructure
  limitation.
- Free-infrastructure operation, validation and rollback are defined in
  `free-infrastructure-runbook.md`.

## Current Security Posture

- High-confidence secrets in the current tracked trees: 0.
- Public database credentials in current tracked trees: 0.
- Default production passwords in current tracked trees: 0.
- Unrestricted CORS: 0; the production Vercel origin is explicitly allowed.
- Historical secret debt: confirmed. `.env` existed in older backend commits.
- Repositories are public, so every historical credential must be treated as
  compromised even if it has already been removed from the current tree.
- Backend logs do not print bearer tokens, but several paths log user email
  addresses, and one OAuth failure path may log an external response body.
- Frontend security headers are present, but no Content-Security-Policy header
  was observed in the public response.

## References

- [Render pricing](https://render.com/pricing)
- [Render free services](https://render.com/docs/free)
- [Supabase pricing](https://supabase.com/pricing)
- [Supabase backups](https://supabase.com/docs/guides/platform/backups)
- [Vercel limits](https://vercel.com/docs/limits)
- [DNS Belgium domain registration](https://www.dnsbelgium.be/en/register-domain-name)
