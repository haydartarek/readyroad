# ReadyRoad Backup and Restore Policy

Status: Gate A proposal
Owner: ReadyRoad project owner; a named backup operator is required before Gate B
Initial recovery objectives: RPO 24 hours, RTO 2 hours

## Policy Statement

A provider backup, snapshot, copied file, or successful backup command is not a
verified backup until ReadyRoad has been restored into an isolated environment
and its integrity checks pass.

No backup may contain plaintext credentials in Git, logs, issue trackers or
shared documentation.

## Backup Inventory

| Asset | Primary protection | Independent copy | Frequency | Retention | Encryption | Responsible role |
|---|---|---|---|---|---|---|
| Supabase PostgreSQL | Supabase Pro managed daily backups | Encrypted logical `pg_dump` in EU off-site object storage | Nightly | 7 daily, 8 weekly, 12 monthly | TLS in transit; object-store encryption plus client-side encryption where supported | Backup operator |
| VPS | Hetzner provider backup | Rebuild from immutable image and configuration repository | Provider schedule; before risky host changes | Provider 7-slot policy plus selected snapshots | Provider encryption; no secrets in exported config | Infrastructure operator |
| Environment variables | Provider/VPS secret stores | Encrypted password-manager/offline recovery record | On every change; quarterly inventory | Current plus previous recovery version while valid | Strong encrypted vault; restricted MFA access | Project owner |
| Uploaded files | Future EU object storage; not container disk | Versioned/replicated bucket or encrypted export | Daily or provider-managed continuous protection | 30 daily, 12 monthly as content needs dictate | In transit and at rest | Content/infrastructure owner |
| Caddy/Compose/config | Git for non-secret config; Caddy volume provider backup | Encrypted configuration archive | On change and weekly | 8 weekly, 12 monthly | No plaintext secrets; encrypted archive | Infrastructure operator |
| Docker images | GHCR immutable version and SHA tags | Rebuild from signed/tagged source commit | Every release | Retain all supported and last 3 known-good releases | Registry controls and digest verification | Release operator |
| Git repositories | GitHub | Encrypted local/off-site bare mirror | Daily/weekly | At least 12 monthly points | Encrypted destination; MFA access | Project owner |
| DNS zone | DNS provider | Encrypted zone export and record inventory | On every DNS change | Current plus monthly history | Encrypted vault | Domain owner |

## Database Backup Design

### Managed layer

Use Supabase Pro for commercial production to obtain documented managed daily
backups and retention. Provider backups are the first recovery layer, not the
only layer.

### Independent logical layer

Nightly process:

1. Run from a dedicated backup runner or tightly controlled host, not a laptop
   that must remain online.
2. Obtain the database credential from secret storage at runtime.
3. Create a custom-format logical dump without printing the connection string:

   ```bash
   pg_dump --format=custom --no-owner --no-acl --file readyroad-YYYYMMDDTHHMMSS.dump "$DATABASE_URL"
   ```

4. Generate SHA-256 checksum.
5. Encrypt client-side if the destination does not provide an independently
   controlled encryption layer.
6. Upload to a separate EU-region object-storage account/bucket with versioning,
   retention and deletion protection where affordable.
7. Delete local temporary plaintext immediately after verified upload.
8. Emit a backup heartbeat containing only timestamp, size, checksum identifier
   and success/failure. Never include credentials or user data.
9. Alert when the backup is late, unexpectedly small/large, or fails.

Do not manually edit a dump. Do not back up only the `readyroad` content counts;
the complete required schema, Flyway history, constraints, indexes and data must
be recoverable.

## Retention

```text
Daily:   7 copies
Weekly:  8 copies
Monthly: 12 copies
```

Retention must comply with the privacy policy and data minimization rules.
Deleting a user in production may require a documented policy for when that data
ages out of encrypted backups. Backup access is exceptional and audited.

## Restore Test Procedure

Frequency: quarterly and before the first commercial launch.

1. Select a backup without using the most recent copy every time; periodically
   test weekly/monthly retention points.
2. Verify object metadata and SHA-256 checksum.
3. Provision an isolated PostgreSQL instance with no production network path.
4. Create an empty restore database owned by a dedicated test role.
5. Restore:

   ```bash
   pg_restore --clean --if-exists --no-owner --no-acl --dbname "$RESTORE_DATABASE_URL" readyroad-backup.dump
   ```

6. Point a non-production backend at the restored database using isolated
   secrets and disabled outbound email/OAuth callbacks.
7. Verify:
   - Flyway history is internally valid and latest expected version is present.
   - 184 traffic signs.
   - 1,472 sign questions.
   - 184 sign exams.
   - 50 general quiz questions and 131 options, unless an approved release
     intentionally changes these counts.
   - Canonical duplicate checks return 0.
   - Foreign keys and representative indexes exist.
   - Representative read paths, authentication with a dedicated restore-test
     account, lessons and random quiz work.
   - No production email, notification or OAuth callback is sent.
8. Record start/end time to validate RTO.
9. Destroy the isolated restored environment and all temporary credentials.
10. Store a redacted report with backup ID, checksum, duration, tests and result.

Never use real user passwords during a restore test. Authentication validation
must use a dedicated test identity or an approved reset in the isolated copy.

## Restore Acceptance Gate

```text
Checksum: PASSED
Restore command: PASSED
Flyway validation: PASSED
Canonical counts: PASSED
Duplicate checks: PASSED
Representative API smoke: PASSED
Outbound integrations disabled: PASSED
Measured restore time: <= 2 hours
Production modified: NO
```

Until this gate passes, `Restore test` must be reported as NOT VERIFIED.

## Environment and Secret Recovery

- Maintain a redacted inventory of variable names and owners in documentation.
- Store actual values only in approved encrypted secret stores.
- Export/recover access through at least two owner-controlled MFA recovery
  methods kept separately.
- On restore, rotate high-risk credentials rather than copying a possibly
  compromised historical value.
- Do not back up expired temporary tokens or application logs as a substitute
  for database backup.

## Upload Recovery

The current container filesystem is not a durable upload system. Before uploads
are considered production-ready:

- Select EU-region object storage.
- Store only object references in PostgreSQL.
- Enable object versioning or provider recovery.
- Validate authorization and content-type/size controls.
- Include objects in a restore drill and verify broken-reference count is 0.

## Failure Handling

| Failure | Response |
|---|---|
| Nightly dump failed | Alert immediately; retry once after diagnosing capacity/connectivity; never silently skip |
| Upload failed | Retain encrypted local temporary copy only within a strict expiry and alert |
| Checksum mismatch | Quarantine copy and create a fresh backup; investigate storage/transfer |
| Backup unexpectedly small | Treat as failure until table/count checks explain it |
| Restore test failed | Block production migration and repair backup procedure |
| Credential suspected compromised | Revoke/rotate, audit access, create new backup with clean credentials |

## Official References

- [Supabase database backups](https://supabase.com/docs/guides/platform/backups)
- [Hetzner backup billing and slots](https://docs.hetzner.com/cloud/billing/faq/)
