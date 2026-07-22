# ADR: Production Hosting Architecture

- ADR ID: ADR-INFRA-001
- Status: Proposed, requires explicit Gate A approval
- Date: 2026-07-22
- Decision owner: ReadyRoad project owner

## Context

ReadyRoad currently runs the frontend on Vercel, the Spring Boot backend on
Render Free, and PostgreSQL on Supabase Free. This configuration is useful for
testing but has production risks:

- Render Free sleeps and produced a cold request exceeding 120 seconds.
- Backend memory reached about 445 MiB in a 512 MB container.
- Startup takes about 52 seconds even at 1 CPU / 1 GiB because application
  initialization also reconciles canonical content.
- SMTP delivery is unavailable from the current free backend environment.
- The backend filesystem is ephemeral and must not be the durable store for
  user uploads.
- Supabase Free backup/pause guarantees are insufficient for a commercial
  production database.
- No approved immutable-image production deployment or tested restore exists.
- Historical backend Git commits contained an `.env` file.

The Next.js frontend and PostgreSQL queries are operating acceptably. Moving
them without a measured benefit would increase risk.

## Requirements

1. No backend cold starts during normal production operation.
2. At least 2 vCPU and 4 GB RAM for practical year-one headroom.
3. EU-region hosting close to Belgium.
4. Managed PostgreSQL with production backups.
5. HTTPS transactional email, independent of SMTP port policy.
6. Versioned container releases and manual production approval.
7. RPO 24 hours and RTO 2 hours initially.
8. Low-downtime migration with Render retained for rollback.
9. No production database use by automated test/preview environments.
10. A cost appropriate for an early-stage product.

## Options Evaluated

### A. Managed Hybrid

Vercel + Render Standard + Supabase Pro + Brevo.

- Strengths: lowest operational burden, simple rollback, provider-managed host.
- Weaknesses: about USD 50/month before domain and overages.
- Security: strong provider boundary; application secrets/access still owned by
  the project.
- Operations: easiest model.

### B. VPS Backend Only

Vercel + Hetzner CX23 + Supabase Pro + Brevo, with Caddy and Docker.

- Strengths: strong CPU/RAM headroom, no cold starts, low VPS cost, database and
  frontend remain managed.
- Weaknesses: requires Linux patching, firewall, Docker, monitoring and recovery.
- Security: VPS hardening becomes project responsibility.
- Operations: moderate and manageable for one backend host.

### C. Full VPS

Frontend, backend and optionally PostgreSQL on Hetzner CX33.

- Strengths: low direct infrastructure cost and full control.
- Weaknesses: single fault domain, highest maintenance, complex PostgreSQL
  backup/recovery, removes working managed frontend benefits.
- Security: maximum operator responsibility.
- Operations: not proportionate to current traffic or team size.

## Decision

Adopt Option B after explicit Gate A approval:

```text
Frontend: Vercel, retained
Backend: Hetzner CX23, Nuremberg, Ubuntu 24.04 LTS, Docker
Reverse proxy/TLS: Caddy
Database: Supabase retained; upgrade to Pro for commercial production
Email: Brevo transactional HTTP API after domain verification
Monitoring: Better Stack plus provider metrics
Container delivery: GHCR immutable version and SHA tags, manual approved deploy
```

This decision is PROPOSED, not executed.

## Cost Baseline

```text
Hetzner CX23:             about EUR 6.53/month incl. 19% VAT baseline
IPv4:                    about EUR 0.60/month, reconfirm at checkout
Hetzner backup:           about EUR 1.31/month
Supabase Pro:             USD 25/month base
Brevo initial tier:       USD/EUR 0 if quota and terms remain suitable
Better Stack initial:     USD/EUR 0 if quota remains suitable
Domain:                   registrar price, not yet purchased
```

The expected base is about EUR 8.44/month for the VPS side plus USD 25/month
for Supabase Pro, domain registration, taxes and any usage overages.

## Rejected Decisions

- Keep Render Free for commercial launch: rejected due cold start, memory and
  free-tier production limitations.
- Upgrade only to Render Starter: rejected because 512 MB remains the measured
  memory ceiling.
- Move the frontend to VPS: rejected because Vercel is working and provides
  managed edge/TLS/deployment benefits.
- Self-host PostgreSQL on the backend VPS: rejected because it creates a single
  failure domain and disproportionate recovery/security work.
- Deploy every push automatically: rejected because production must require an
  approval and immutable image selection.
- Use Watchtower for unattended production updates: rejected because it weakens
  release control and rollback predictability.
- Use raw SMTP as the primary email strategy: rejected because an HTTPS API is
  more portable and provides better delivery event handling.

## Migration Impact

- No API contract or business logic change is required for the hosting move.
- A future Gate B may add deployment files, provider secrets, health monitoring
  and email-provider integration under a separate approved scope.
- Vercel `BACKEND_URL` and backend CORS/origin settings will change at cutover.
- The same Supabase production database remains in place, avoiding data copy.
- Canonical URLs, Search Console and analytics change only after the domain is
  purchased and connected.

## Rollback Summary

Keep Render deployed and healthy. If the VPS fails acceptance or post-cutover
monitoring, restore the Vercel backend environment variable to the Render URL,
redeploy the last known-good frontend configuration, and revert `api` DNS if it
was changed. The shared Supabase database does not move. Target rollback time is
5-15 minutes, excluding DNS cache outliers.

## Conditions Before Gate B

- User approves provider, plan, cost, domain and email provider.
- All historical credentials are confirmed rotated.
- Git history cleanup/replacement strategy is approved for the public repos.
- A clean release baseline is created and CI passes for that exact commit.
- Supabase plan choice and independent backup destination are approved.
- Domain ownership is confirmed.
- Deployment and rollback rehearsals are scheduled.

## Consequences

Positive:

- Removes free-backend sleep behavior.
- Provides memory/CPU headroom at low cost.
- Preserves managed frontend and database strengths.
- Supports versioned, reversible production releases.

Negative:

- The project owner must patch and monitor a Linux VPS.
- Provider and off-site backup configuration become operational duties.
- Supabase Pro remains the largest recurring base cost.
- The architecture is still single-region and does not promise high availability.

## Review Trigger

Review this ADR when any of these occur:

- Peak concurrency exceeds 50 users.
- API egress exceeds 75 GB/month.
- Backend RAM remains above 70 percent or CPU above 60 percent over 15 minutes.
- Availability target rises above 99.5 percent.
- Supabase costs or requirements justify a different managed database.
- A second operator/service owner joins and managed hosting becomes preferable.
