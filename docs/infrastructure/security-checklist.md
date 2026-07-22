# ReadyRoad Production Security Checklist

Status: Gate A review
Last reviewed: 2026-07-22

Legend:

```text
[x] Verified in the current state
[ ] Required before or during Gate B
[!] Known risk requiring owner action
[N/A] Not applicable to the selected architecture
```
## Current Findings

- [x] High-confidence secrets in current tracked Git trees: 0.
- [x] Public database credentials in current tracked Git trees: 0.
- [x] Default production passwords in current tracked Git trees: 0.
- [x] Unrestricted production CORS: 0.
- [x] Hostile-origin production preflight was denied.
- [x] Backend response includes HSTS, no-sniff, frame denial and no-store headers.
- [x] Frontend response includes HSTS, no-sniff, frame and referrer controls.
- [!] Backend Git history previously tracked `.env`; the repositories are public.
- [!] Credential rotation status cannot be proven from source inspection alone.
- [!] Frontend public response did not expose a Content-Security-Policy header.
- [!] Backend logs include user email addresses in several paths.
- [!] An OAuth failure path may log an external token response body.
- [!] Current release worktrees/remote CI/deployed versions do not form one clean,
  proven release baseline.
- [!] Production, preview and automated-test data separation is not fully proven.

## Historical Secret Debt

Older backend commits include an `.env` file. Removing it from the current tree
does not make historical values safe. Before commercial launch or further public
distribution:

- [ ] Inventory every credential that ever appeared in Git history.
- [ ] Rotate database passwords/roles.
- [ ] Rotate JWT secrets and assess whether active tokens must be invalidated.
- [ ] Rotate Google OAuth client secrets and verify callback restrictions.
- [ ] Revoke/rotate Gmail app passwords and other SMTP credentials.
- [ ] Rotate administrator bootstrap/default credentials.
- [ ] Rotate any Vercel, Render, Supabase or GitHub token that appeared.
- [ ] Review provider access logs for misuse where available.
- [ ] Choose and approve a Git history rewrite or clean-repository replacement.
- [ ] Coordinate force-push/history cleanup with all clones and CI integrations.
- [ ] Run a history-wide secret scanner after cleanup.
- [ ] Document rotation dates and owners without recording secret values.

History cleanup is not a substitute for rotation. Every committed credential is
treated as compromised.

## Identity and Access

- [ ] MFA enabled for GitHub, Vercel, Render, Supabase, Hetzner, DNS, email and
  monitoring accounts.
- [ ] At least two recovery methods stored separately.
- [ ] Individual accounts used; no shared administrator login.
- [ ] Least-privilege roles assigned and reviewed quarterly.
- [ ] Production SSH uses named non-root administrator accounts and keys only.
- [ ] Root/password SSH disabled after tested recovery access.
- [ ] Provider API tokens are scoped, expiring where supported, and individually
  revocable.
- [ ] GitHub production environment requires manual approval.
- [ ] Branch protection and required CI checks enabled for the release branch.
- [ ] Dormant users/keys/tokens removed.

## Secrets and Environment Variables

- [ ] Local, Test, Preview and Production use separate credentials.
- [ ] Automated tests never use the production database.
- [ ] Vercel previews do not call production unless explicitly read-only and
  approved; dedicated preview backend/database is preferred.
- [ ] Production env file is outside Git, root-owned and mode 0600.
- [ ] No secret appears in Docker image layers, Compose files, build args or
  GitHub artifacts.
- [ ] Secret names/owners are documented; values remain in encrypted stores.
- [ ] Rotation and emergency revocation runbooks are tested.
- [ ] Backup credentials are separate from application credentials.
- [ ] Email, OAuth and reset-token values never appear in logs.

## Network and Host

- [ ] Hetzner provider firewall allows only restricted 22 and public 80/443.
- [ ] Host firewall mirrors the intended rules.
- [ ] Backend application port is private to the Docker network.
- [ ] PostgreSQL/Supabase port is not exposed by the VPS.
- [ ] Docker daemon/socket is not public or mounted into the application.
- [ ] SSH source IP allowlist used where operationally possible.
- [ ] Ubuntu security updates and unattended upgrades enabled.
- [ ] Host clock synchronization active.
- [ ] No FTP, database, control panel or mail server installed.
- [ ] External port scan confirms unnecessary public ports: 0.
- [ ] Server rebuild and provider-console recovery tested.

## Containers and Supply Chain

- [x] Backend Java process runs as non-root after entrypoint setup.
- [x] Frontend image runs as a non-root user.
- [ ] Production image uses immutable SemVer and commit-SHA tags/digest.
- [ ] CI creates an SBOM and vulnerability scan result for the release image.
- [ ] Base images and dependencies are patched through controlled releases.
- [ ] No unattended Watchtower production update.
- [ ] Read-only filesystem and dropped Linux capabilities evaluated in staging.
- [ ] CPU/memory/PID limits set from staging measurements.
- [ ] Docker log rotation configured.
- [ ] Registry pull token is read-only and scoped to the backend image.
- [ ] Prior known-good image remains pullable for rollback.

## TLS, Domain and Browser Controls

- [x] Current provider URLs use HTTPS.
- [ ] `readyroad.be` ownership confirmed before configuration.
- [ ] Apex is canonical; `www` redirects permanently to apex.
- [ ] `api.readyroad.be` uses valid automatic Caddy TLS.
- [ ] DNSSEC enabled when registrar/DNS provider supports a tested path.
- [ ] TLS expiry alerts at 30, 14 and 7 days.
- [ ] HTTP redirects to HTTPS.
- [ ] HSTS validated after both domains are stable.
- [ ] Content-Security-Policy designed and tested without breaking Next.js/OAuth.
- [ ] Frame, MIME-sniffing, referrer and permissions policies reviewed.
- [ ] Mixed content and insecure asset requests: 0.
- [ ] Canonical URLs, OAuth callbacks and password-reset links use final domain.

## CORS and API

- [x] Current production Vercel origin is explicitly allowed.
- [x] Credentials are not combined with wildcard origin.
- [x] Untrusted test origin was denied.
- [ ] Final apex origin added during controlled cutover.
- [ ] Temporary Vercel/preview origins removed after transition where not needed.
- [ ] Admin endpoints return 401/403 correctly and are not exposed by monitor
  credentials.
- [ ] Rate limiting/abuse controls reviewed for auth, password reset, contact and
  public expensive endpoints.
- [ ] Actuator exposes only intended sanitized health information publicly.
- [ ] Request size, upload type and upload size limits validated.

## Database

- [x] Production database is not embedded in an application container.
- [x] Application pool is bounded at max 5 in the current configuration.
- [ ] Dedicated least-privilege application role confirmed.
- [ ] Migration role separated from runtime role where practical.
- [ ] TLS required for database connections.
- [ ] Supabase network restrictions evaluated against stable VPS egress IP.
- [ ] Supabase Pro enabled or Free-tier risks explicitly accepted before launch.
- [ ] Independent encrypted logical backup active.
- [ ] Quarterly isolated restore test passes.
- [ ] Database admin access protected by MFA and audited.
- [ ] No manual direct production content edits.

## Logging, Monitoring and Privacy

- [x] Bearer token value is intentionally hidden by the request filter.
- [ ] Remove/mask user email addresses from routine application logs.
- [ ] Do not log OAuth token endpoint response bodies.
- [ ] Structured logs include release and correlation IDs.
- [ ] Passwords, tokens, cookies, authorization headers, JDBC credentials and
  reset links absent from representative logs.
- [ ] Retention and access follow GDPR data minimization.
- [ ] Uptime, 5xx, CPU, RAM, disk, restart, database and backup alerts tested.
- [ ] Security incident contacts and notification assessment documented.
- [ ] Monitoring account uses MFA and least privilege.

## Email

- [x] SMTP remains formally deferred in Gate A.
- [ ] User explicitly approves Brevo or another provider.
- [ ] DPA, region/data handling and commercial terms reviewed.
- [ ] Sending domain verified.
- [ ] SPF and DKIM pass.
- [ ] DMARC begins with a monitored policy and is tightened after validation.
- [ ] API key stored only in approved production secret storage.
- [ ] Delivery, bounce and complaint webhooks authenticate and process
  idempotently.
- [ ] Password reset, expiration, invalid token and reuse protections verified
  end to end with actual delivery.
- [ ] Email content contains no sensitive information beyond the one-time link.

## Backup and Incident Readiness

- [ ] Provider backup enabled on VPS.
- [ ] Supabase managed backup level approved.
- [ ] Nightly encrypted off-site logical backup succeeds.
- [ ] Backup heartbeat alert tested.
- [ ] Isolated restore meets RTO and integrity gates.
- [ ] DNS zone and non-secret configuration exports current.
- [ ] Render remains available through migration stability window.
- [ ] Vercel backend-target rollback rehearsed.
- [ ] Compromised-secret and deleted-data incident drills completed.

## Gate B Security Blockers

Gate B must not switch production traffic while any of these remain unresolved:

1. Historical credential rotation is unconfirmed.
2. No clean release commit with passing CI for the exact deployed artifacts.
3. Production and test/preview database separation is unproven.
4. No verified independent database restore.
5. Domain/DNS/TLS ownership and recovery access are unconfirmed.
6. VPS SSH/firewall hardening has not passed an external port check.
7. Logs still expose secrets or high-risk OAuth response bodies.
8. Monitoring and backup alerts have not been tested.

## Current Gate A Result

```text
Secrets in current Git tree: 0
Public database credentials in current Git tree: 0
Default passwords in current Git tree: 0
Unrestricted CORS: 0
Known historical secret exposure: YES
Production security approval: NOT YET GRANTED
```
