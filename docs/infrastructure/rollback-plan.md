# ReadyRoad Production Rollback and Disaster Recovery Plan

Status: Gate A design; must be rehearsed before production cutover
Target rollback time: 5-15 minutes for backend traffic
Initial RPO/RTO: 24 hours / 2 hours

## Rollback Principles

1. Stop the impact first; investigate after stable service is restored.
2. Keep Render deployed and healthy during migration and the stability window.
3. Use immutable prior images and recorded environment versions.
4. Do not modify or repair production data ad hoc during an incident.
5. Never restore a database over production without preserving the failed state
   and obtaining explicit owner approval.
6. Record every action and timestamp in the incident log.

## Required Rollback Assets

- Current Render backend URL and deployment identifier.
- Last known-good backend and frontend Git SHAs.
- Last known-good backend image digest.
- Redacted environment-variable inventory for Render, VPS and Vercel.
- Access to Vercel production environment settings and redeploy controls.
- DNS provider access with MFA and current zone export.
- Supabase project access with MFA and recovery contacts.
- Latest verified database backup and its checksum.
- Caddy, Compose and host configuration backups without plaintext secrets.
- Contact list and user/status communication template.

## Fast Backend Cutover Rollback

Use when the VPS is unhealthy, slow, misconfigured, or returns unexpected errors
after the frontend switch.

1. Declare rollback and freeze production changes.
2. Confirm Render `/actuator/health` is UP.
3. Restore Vercel production `BACKEND_URL` to the recorded Render URL.
4. Redeploy the last known-good frontend commit/configuration.
5. If browser traffic uses `api.readyroad.be` directly, restore its DNS record to
   the previous approved target or temporarily use the Render hostname.
6. Verify homepage, login, traffic signs, lessons and random quiz.
7. Verify expected unauthenticated 401 responses on protected endpoints.
8. Confirm requests appear in Render logs and new requests have stopped reaching
   the VPS.
9. Communicate restoration, preserve VPS logs, and start root-cause analysis.

Expected duration: 5-15 minutes, excluding long DNS cache behavior.

## Application Deployment Rollback on VPS

Use when the host is healthy but the new backend image is faulty.

1. Select the prior recorded image digest, never an unverified `latest` tag.
2. Update the deployment manifest to that digest.
3. Start the prior container without changing database credentials.
4. Verify health, Flyway validation and smoke tests.
5. Keep the failed container stopped but preserve logs until the incident is
   documented.
6. If the prior image cannot start because a forward-only database migration is
   incompatible, do not force it. Switch traffic to Render if compatible, or
   execute the approved database recovery procedure.

## DNS Rollback

1. Use the saved pre-migration zone record.
2. Restore the prior A/CNAME target.
3. Do not change unrelated DNS records such as MX, SPF, DKIM or DMARC.
4. Verify authoritative DNS and at least two public resolvers.
5. Verify TLS on the restored hostname.
6. Monitor both old and new targets until the previous TTL has elapsed.

## Supabase Connection Rollback

The proposed migration keeps Supabase, so normal rollback does not move or
replace the database. If the new host has a bad connection configuration:

1. Stop the VPS backend to prevent repeated failures.
2. Confirm Render still has the last known-good production connection settings.
3. Switch frontend traffic to Render.
4. Revoke the faulty/new database credential if compromise is suspected.
5. Issue a new least-privilege credential and update only approved secret stores.
6. Validate from a non-production client before redeploying.

## Database Restore Escalation

Restore is required only for verified data loss/corruption, not an application
deployment failure.

1. Freeze writes and preserve the current database/export.
2. Determine the incident time and desired recovery point.
3. Restore the selected backup into an isolated PostgreSQL instance/schema.
4. Verify checksum, Flyway history, table counts, constraints, canonical counts,
   representative authentication and application queries.
5. Obtain explicit owner approval for production replacement.
6. Rotate credentials if compromise caused the incident.
7. Switch the application to the restored database using the controlled
   migration process.
8. Keep the original failed state until legal/operational retention allows
   deletion.

Target RTO is 2 hours, but this target is not considered achieved until a timed
restore rehearsal passes.

## Disaster Scenarios

| Scenario | Detection | Immediate action | Recovery | Communication | Prevention |
|---|---|---|---|---|---|
| VPS unavailable | Uptime and provider alert | Switch Vercel backend target to Render | Repair/rebuild from image and configuration backup | Status page and owner alert | Provider backup, immutable image, documented rebuild |
| Database unavailable | Health failure, pool errors, Supabase alert | Stop retry storm; keep public status clear | Provider recovery or approved restored database | Owner/status update; no sensitive details | Managed plan, pool bounds, independent backups |
| Faulty deployment | 5xx/restart/functional canary | Roll back image or traffic | Deploy prior digest | Release incident note | Manual approval, staging smoke, immutable tags |
| Expired/invalid TLS | SSL monitor | Use provider hostname/Render fallback if needed | Renew/fix Caddy/DNS/ACME | Status update if user impact | Automated TLS and expiry alerts at 30/14/7 days |
| Compromised secret | Provider alert or suspicious access | Revoke credential and restrict traffic | Issue new credential, update approved stores, audit access | Security incident process and required notifications | MFA, least privilege, no secrets in Git/logs |
| Deleted/corrupt data | Integrity checks/user report | Freeze writes, preserve evidence | Isolated restore, verify, controlled switch | Owner and affected-user/legal assessment | Backups, restore tests, limited admin access |
| Email provider outage | Delivery webhook/monitor | Disable retry storm; show accurate user message | Provider recovery or approved fallback | Status note for password reset | Webhook monitoring and documented provider fallback |

## Rollback Verification Checklist

Before migration:

- [ ] Render health confirmed immediately before cutover.
- [ ] Vercel environment rollback rehearsed in a non-production project.
- [ ] Prior frontend deployment can be promoted/redeployed.
- [ ] Prior backend digest can be pulled and started.
- [ ] DNS zone export saved and restore permission confirmed.
- [ ] Independent database restore completed in isolation.
- [ ] Secrets can be rotated without source-code changes.
- [ ] Contact/communication channel tested.

After rollback:

- [ ] Health and public smoke tests pass.
- [ ] Authentication and authorization pass.
- [ ] Traffic signs, lessons and quiz pass.
- [ ] No duplicate writes or canonical drift detected.
- [ ] Database connections returned to expected levels.
- [ ] Monitoring targets the restored service.
- [ ] Incident timeline and root-cause owner assigned.

## When Render May Be Removed

Render may be decommissioned only after:

- 72-hour minimum VPS stability window passes.
- Rollback to the prior image has been rehearsed.
- Host rebuild from documentation has been rehearsed or verified.
- Independent database restore passes.
- Monitoring and backup alerts are operational.
- Owner explicitly accepts that future host failure will use reprovisioning rather
  than immediate Render fallback.
