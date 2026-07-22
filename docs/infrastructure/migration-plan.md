# Backend Migration Plan: Render to VPS

Status: Gate A plan only
Target: Low-downtime backend-only migration
Execution authorization: Explicit Gate B approval required

## Scope and Guardrails

- Keep Vercel and Supabase in place.
- Keep Render active until the new backend passes acceptance and rollback is
  verified.
- Do not change API contracts, business logic or database schema for the move.
- Do not purchase, provision, change DNS, or change production variables during
  Gate A.
- Never print or commit credentials.

## Preconditions

1. User approves Hetzner CX23, expected cost, domain and Brevo.
2. A clean release commit is selected; all backend/web CI checks pass for that
   exact commit.
3. Historical credentials are rotated and Git-history remediation is approved.
4. Supabase production backup and an independent logical backup are completed.
5. An isolated restore test passes before cutover.
6. `readyroad.be` is owned and DNS access is confirmed.
7. DNS TTL is reduced to 300 seconds at least one normal TTL period in advance.
8. Render's current URL, environment inventory and deployment are preserved.
9. A maintenance/cutover window and responsible operator are assigned.

## Release Artifact Strategy

1. Tag the approved source using Semantic Versioning, initially an RC such as
   `v1.0.0-rc.1`.
2. GitHub Actions builds the backend Docker image from that commit.
3. Push to GitHub Container Registry with both version and full commit SHA tags.
4. Record image digest in the release notes.
5. Scan the image and dependencies according to the existing security policy.
6. Production deployment selects the digest/tag manually after approval.
7. Never use `latest` as the only production reference.

## Sequential Runbook

| Step | Precondition | Action | Expected result | Verification | Rollback action |
|---|---|---|---|---|---|
| 1. Provision | Gate B approved | Create CX23 in Nuremberg with Ubuntu 24.04 LTS, IPv4 and provider backup | Host is available | Provider console and SSH host fingerprint recorded | Delete only after confirming no production traffic/data |
| 2. Secure access | Host exists | Create non-root admin, install SSH key, disable password/root login after testing, restrict SSH source | Key-only administration works | Second SSH session succeeds before closing first | Use provider console/recovery mode |
| 3. Patch host | Secure access works | Apply OS updates and enable unattended security updates | Current patched host | Reboot and reconnect; audit pending updates | Rebuild host if hardening is inconsistent |
| 4. Firewall | Required ports known | Allow restricted 22 and public 80/443 only | Minimal public surface | External port scan; application port and Docker daemon closed | Restore last known firewall rules through provider console |
| 5. Install runtime | Patched host | Install Docker Engine and Compose plugin from official source | Docker runtime operational | Run a signed test image and inspect versions | Remove packages or rebuild host |
| 6. Configure secrets | Credentials rotated | Create root-owned production env file outside Git, mode 0600 | Container can read only required values | Permission check and redacted configuration inventory | Remove file and revoke newly issued values |
| 7. Configure network/proxy | Domain owned or temporary hostname selected | Create private Docker network and Caddy configuration | Proxy can reach backend privately | Local/temporary HTTPS health request | Stop Caddy and restore previous config |
| 8. Pull immutable image | Approved digest exists | Authenticate read-only to GHCR and pull exact digest | Correct artifact present | Compare local digest to release record | Pull previous known-good digest |
| 9. Start backend | Supabase reachable from VPS | Start backend with production profiles and resource/log limits | Container becomes healthy | Logs show Flyway validation, startup and importer success; no secret values | Stop container; database remains unchanged except expected idempotent startup behavior |
| 10. Validate database | Backend healthy | Confirm Flyway version and canonical counts through safe endpoints/read-only query | Version 10+ expected and canonical data intact | 184 signs, 1472 sign questions, 184 sign exams, 50 general quiz questions; duplicates 0 | Stop new backend and investigate; do not repair production ad hoc |
| 11. Soak test | New backend healthy | Run at least 24 hours on temporary URL with synthetic low-rate checks | Stable resource and connection behavior | CPU/RAM/disk, pool, 5xx, restarts, latency | Keep Render as production; fix before retry |
| 12. Domain/TLS | DNS ownership and Caddy ready | Point `api.readyroad.be` to VPS and allow Caddy ACME | Valid HTTPS API hostname | Certificate chain, hostname, HSTS, expiry and health | Revert DNS to prior target or use Render URL directly |
| 13. CORS | Final frontend origins known | Add canonical frontend origin while retaining temporary Vercel origin during transition | Browser API requests accepted only from intended origins | Allowed-origin preflight 200; hostile origin denied | Restore previous origin list |
| 14. Smoke test | API domain ready | Test health, traffic signs, lessons, random quiz, auth, profile and admin authorization | Functional parity | Expected 200/401/403 statuses, counts, no duplicates or console errors | Stop cutover and keep current Vercel backend target |
| 15. Switch frontend | All acceptance tests pass | Change Vercel production `BACKEND_URL` to new API and deploy exact approved web commit | Production web uses VPS | Network trace and server logs identify new backend; critical paths pass | Restore Render URL and redeploy previous environment/configuration |
| 16. Observe | Frontend switched | Monitor for at least 24-72 hours | Stable service | Uptime, 5xx, auth, latency, DB pool, CPU/RAM/disk and user reports | Execute rollback if thresholds are breached |
| 17. Decommission decision | Observation and rollback test pass | Keep Render for an agreed safety period, then pause/remove only after approval | Duplicate hosting cost/risk controlled | Final backup, configuration record and owner approval | Re-enable/redeploy Render if still retained |

## Acceptance Checks Before Traffic Switch

```text
New backend health: UP
TLS: valid
Flyway: validated
Traffic signs: 184
Sign questions: 1472
Sign exams: 184
General quiz questions: 50
Duplicates: 0
Authentication: passed
Random quiz: passed
Lessons: passed
Admin authorization: passed
CORS allowed origin: passed
CORS hostile origin: denied
Secrets in logs: 0
Container restarts: 0
```

## Email Migration Subplan

Email is a separate approved change even if executed in the same Gate B window.

1. Own the domain.
2. Approve Brevo's plan, terms and DPA.
3. Verify the sending domain.
4. Publish SPF and DKIM; publish a monitored DMARC policy and tighten it after
   validating legitimate senders.
5. Store the API key only in production secret storage.
6. Implement/use the HTTPS transactional API under an explicitly approved code
   scope.
7. Process delivery, bounce and complaint webhooks idempotently.
8. Validate a real password-reset delivery, expiration and reuse protection.
9. Rotate the validation credential before formal production handoff if it was
   exposed during testing.

SMTP remains formally deferred until this plan is approved and executed.

## Expected Downtime

- Target: 0-5 minutes of user-visible disruption.
- The backend is deployed in parallel, so the main switch is the Vercel backend
  environment variable/redeployment and optional DNS transition.
- Existing sessions should remain valid only if the same JWT secret and issuer
  are securely transferred; this must be tested without exposing the value.
- DNS caches can extend transition observations beyond the nominal TTL.

## Post-Cutover Checklist

- Capture deployment version, image digest, host ID and timestamp.
- Confirm Better Stack monitors and alerts are receiving events.
- Confirm backup heartbeat and next scheduled logical backup.
- Review logs for PII, secrets, 4xx/5xx and OAuth/email failures.
- Restore normal DNS TTL after the stability window.
- Remove temporary origins and temporary hostnames after all clients migrate.
- Record actual traffic/resource measurements to replace estimates.
- Do not delete Render until the owner accepts the rollback window result.
