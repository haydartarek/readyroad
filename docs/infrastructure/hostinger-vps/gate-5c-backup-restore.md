# Gate 5C: Backup, Restore, and Disaster Recovery

## Scope

This runbook covers the ReadyRoad production deployment on the Hostinger VPS:

- Next.js frontend container
- Spring Boot backend container
- Caddy configuration and certificate volumes
- Supabase PostgreSQL schema `readyroad`
- deployment metadata, system services, and security configuration
- the currently empty uploads volume

It does not install a PostgreSQL service, restore into Supabase, or modify
application behavior.

## Backup architecture

```text
Supabase PostgreSQL 17
        |
        | pg_dump -Fc, schema readyroad, read-only snapshot
        v
VPS protected staging -> age encryption -> readyroad-backups volume
                                              |
                                              | encrypted artifacts only
                                              v
                                  operator off-site copy
```

The VPS stores only the age X25519 recipient. The age identity used to decrypt
backups is held by the operator outside the VPS and outside Git.

Supabase recommends regular logical exports and off-site copies for Free plan
projects:

<https://supabase.com/docs/guides/platform/backups>

## Asset inventory

| Asset | Classification | Backup handling |
| --- | --- | --- |
| `readyroad` schema and Flyway history | Critical, secret | Daily encrypted custom-format dump |
| Current environment | Critical, secret | Encrypted environment archive |
| Caddy data and config volumes | Critical, secret | Encrypted Caddy archive |
| Current Compose and Caddy files | Critical | Encrypted configuration archive |
| Release manifests and image identities | Critical | Encrypted configuration archive |
| Rollback scripts and state | Critical | Encrypted configuration archive |
| Monitoring and backup units/scripts | Critical | Encrypted configuration archive |
| UFW, Fail2Ban, SSH, Docker, journald config | Critical | Encrypted configuration archive |
| `readyroad-uploads` | Critical when non-empty | Encrypted archive only when files exist |
| Application source in release directories | Reconstructable | Git repositories, not duplicated |
| Docker image layers | Reconstructable | Image tags/digests are inventoried |
| Container/application logs | Generated | Rotated, not backed up |
| Build caches, `.next`, `target`, `node_modules` | Generated | Excluded |
| Temporary restore or staging data | Not required | Removed after each operation |

The following requested legacy paths do not exist on this VPS:
`/opt/readyroad/env`, `/opt/readyroad/compose`,
`/opt/readyroad/scripts`, and `/opt/readyroad/release-manifests`.
Their active equivalents are stored in release directories and
`/opt/readyroad/bin`.

## Backup types and policy

| Type | Frequency | Retention | Integrity | Owner | Failure signal |
| --- | --- | --- | --- | --- | --- |
| Database logical dump | Daily | 7 daily, 4 weekly, 3 monthly | encrypted and plaintext SHA-256, `pg_restore -l` | root/systemd | journal + Gate 5B monitor |
| VPS configuration | Weekly | 4 | encrypted and plaintext SHA-256, archive listing | root/systemd | journal + Gate 5B monitor |
| Deployment configuration | Before deployment | 5 | encrypted and plaintext SHA-256 | deployment operator | command exit status |
| Caddy data/config | Weekly | 4 | checksum, decryption, archive listing | root/systemd | journal + Gate 5B monitor |
| Environment | Weekly | 4 | checksum, decryption, file presence | root/systemd | journal + Gate 5B monitor |
| Uploads | Weekly when non-empty | 4 | checksum, decryption, archive listing | root/systemd | journal + Gate 5B monitor |
| Restore verification | Monthly | last-success timestamp | isolated PostgreSQL restore | operator | `restore_overdue` |

At the measured database size, the full database policy consumes approximately
21 MB before filesystem overhead. Configuration archives are below 100 KB per
generation. The backup script refuses to start when less than 2 GB is free.

Retention always keeps at least one artifact. It sorts timestamped encrypted
files newest-first and deletes sidecars only with the corresponding old
artifact.

## Locations

### VPS

```text
/var/lib/docker/volumes/readyroad-backups/_data/
  db/daily/
  db/weekly/
  db/monthly/
  config/weekly/
  config/deployment/

/etc/readyroad-backup/
  backup.conf
  database.env
  age-recipient.txt

/var/lib/readyroad-backup/state/
  latest-backup
  last-success
  last-failure
  last-db-success
  last-config-success
  last-restore-success
```

`database.env` is mode `0600`, root-owned, and contains only PostgreSQL
connection variables. The private age identity must never be placed on the
VPS.

### Off-site

The verified copy is under:

```text
%USERPROFILE%\ReadyRoadBackups\<timestamp>\
```

Only encrypted artifacts and non-secret checksum/metadata sidecars are copied.
The directory ACL allows only the operator and `SYSTEM`.

Hostinger platform backups are a secondary infrastructure-level recovery
source. GitHub artifacts are limited to non-secret metadata and are not used
for database or environment backups. S3-compatible storage is deferred until
an external storage service is approved.

## Encryption

- Tool: `age`
- VPS version validated: `1.1.1`
- Operator version validated: `1.3.1`
- Recipient type: X25519
- Public recipient: `/etc/readyroad-backup/age-recipient.txt`
- Private identity: operator-controlled storage outside the project and VPS
- Backup permissions: `0600`

The restore validation proved:

- correct-key decryption succeeds;
- wrong-key decryption fails;
- a one-byte corruption is detected by SHA-256 and age authentication;
- a truncated artifact is detected and rejected;
- the plaintext checksum matches after decryption.

## Tools and services

```text
/usr/local/lib/readyroad-backup/backup-lib.sh
/usr/local/sbin/readyroad-backup-supabase
/usr/local/sbin/readyroad-backup-config
/usr/local/sbin/readyroad-backup-run
/etc/systemd/system/readyroad-backup.service
/etc/systemd/system/readyroad-backup.timer
```

The timer runs daily at 02:15 UTC with up to 30 minutes of randomized delay.
`Persistent=true` runs a missed backup after boot. A global lock and
component-specific locks prevent overlap.

The service is a constrained root one-shot because it must read root-only
configuration and Docker volume data. It uses low CPU and I/O weights, a
45-minute timeout, a private `/tmp`, and a read-only system except for the
backup volume and its state directory.

## Manual operations

Create the regular daily backup:

```bash
sudo systemctl start readyroad-backup.service
sudo systemctl show readyroad-backup.service \
  -p Result -p ExecMainStatus
```

Create a database and weekly configuration backup immediately:

```bash
sudo /usr/local/sbin/readyroad-backup-run --force
```

Create a pre-deployment configuration backup:

```bash
sudo /usr/local/sbin/readyroad-backup-config --deployment
```

Preview retention without deletion:

```bash
sudo /usr/local/sbin/readyroad-backup-supabase --retention-dry-run
sudo /usr/local/sbin/readyroad-backup-config --retention-dry-run
```

Inspect status without exposing backup data:

```bash
systemctl status readyroad-backup.timer --no-pager
sudo journalctl -u readyroad-backup.service --since today --no-pager
sudo /usr/local/sbin/readyroad-monitor
```

## Integrity verification

For the latest encrypted artifact:

```bash
latest="$(sudo cat /var/lib/readyroad-backup/state/latest-backup)"
sudo sh -c 'cd "$(dirname "$1")" &&
  sha256sum --check "$(basename "${1}.sha256")"' sh "$latest"
```

After copying it off-site, decrypt locally and compare the resulting dump with
the `.dump.sha256` sidecar. Validate its directory without printing data:

```bash
pg_restore --list readyroad-db-<timestamp>.dump >/dev/null
```

## Isolated database restore

1. Copy the encrypted artifact and sidecars off-site.
2. Decrypt locally with the operator identity.
3. Create a Docker network with `--internal`.
4. Start a temporary PostgreSQL 17 container with no published ports, a
   temporary data filesystem, and no production volume.
5. Run:

```bash
pg_restore --exit-on-error --no-owner --no-privileges \
  --username postgres --dbname readyroad_restore \
  /restore/readyroad-db-<timestamp>.dump
```

6. Verify schema objects, Flyway history, constraints, and canonical counts.
7. Remove the temporary container, network, and decrypted files.

The application containers are not started against the restored copy. The copy
contains production user records, and the backend startup importer and admin
initializer could write to that copy. Database-level restoration plus
production API smoke tests provide validation without creating temporary
sessions, sending email, or changing copied user records.

## Configuration restore

1. Decrypt the configuration, environment, and Caddy archives off-site.
2. Extract each into a new temporary directory.
3. Put `.env.production` from the environment archive beside the restored
   Compose file.
4. Run `docker compose ... config --quiet`.
5. Run `caddy validate` against the restored Caddyfile.
6. Run `systemd-analyze verify` against restored service and timer files.
7. Confirm certificate files, manifests, scripts, and `current-release.txt`.
8. Delete the temporary plaintext directory.

Never extract directly over `/opt`, `/etc`, `/usr/local`, or Docker volumes.

## VPS replacement procedure

1. Provision and harden Ubuntu using the Phase 1 runbook.
2. Install Docker using the Phase 2 runbook; do not install PostgreSQL Server.
3. Create the ReadyRoad network and named volumes.
4. Install `age`, Docker client tooling, and the backup scripts.
5. Transfer encrypted configuration and environment archives.
6. Decrypt them only on the controlled replacement host during recovery.
7. Restore Compose, Caddy, systemd, firewall, SSH, and monitoring files.
8. Recover immutable images from their registry or rebuild their recorded
   source commits and verify their digests.
9. Restore Caddy volumes before starting Caddy.
10. Verify Supabase connectivity and start the application.
11. Run health, authentication, signs, lessons, quiz, and progress smoke tests.
12. Change DNS only after the replacement passes all validation.

Registry-backed image recovery is part of Gate 5D. Until then, the existing VPS
images and source commits remain required for the fastest full-host recovery.

## Disaster recovery scenarios

| Scenario | Detection and immediate response | Restore source and validation | Estimated recovery |
| --- | --- | --- | --- |
| Backend container failure | Docker health/restart alert; restart the immutable image | current release manifest; health, auth, APIs | 5-10 min |
| Frontend container failure | HTTP and Docker health alert | current frontend image; homepage and proxy checks | 5-10 min |
| Caddy failure | HTTPS/SSL alert | Caddy image plus encrypted Caddy archive; TLS and redirect checks | 10-20 min |
| VPS filesystem loss | all external checks fail | Hostinger backup plus off-site encrypted configuration | 60-120 min |
| VPS replacement | host unreachable or unrecoverable | off-site archives, source commits, image records | 90-180 min |
| Supabase data loss | missing data/count drift | latest encrypted logical dump restored only after explicit approval | RPO <= 24 h; RTO 30-60 min |
| Configuration deletion | service/config validation fails | latest encrypted configuration/environment archive | 15-30 min |
| Deployment failure | health gate fails | immutable previous release and rollback script | 5-10 min |
| Certificate storage loss | Caddy certificate reissue or TLS failure | encrypted Caddy archive or safe ACME reissue | 15-30 min |
| Credential compromise | security alert or confirmed disclosure | isolate service, rotate affected credentials, rebuild encrypted environment backup | depends on provider; escalate immediately |

Escalate any database restore, credential compromise, DNS change, or full VPS
replacement to the project owner before destructive production action.

## Alert integration

Gate 5B now checks:

- backup service failure;
- last failure newer than last success;
- missing latest backup;
- invalid encrypted checksum;
- last successful backup older than 26 hours;
- restore verification older than 35 days.

The controlled failure test exits with code `97`, leaves the latest valid
backup unchanged, produces a `backup_failure` alert, and is cleared only after
a subsequent successful backup.

## Validation record

Date: 2026-07-24 UTC

```text
Production PostgreSQL: 17.6
Restore PostgreSQL: 17.10 (same major version)
Database backup format: PostgreSQL custom
Database encrypted size: 1,465,924 bytes
Database plaintext size: 1,465,372 bytes
Initial backup duration: 8 seconds
Database restore duration: 628 ms
Tables: 42
Views: 2
Functions: 2
Triggers: 21
Indexes: 158
Constraints: 521
Foreign keys: 46 validated, 0 unvalidated
Road signs: 184
Sign questions: 1,472
Sign choices: 3,997
Sign exams: 184
Lessons: 30
Quiz questions: 50
Quiz options: 131
Flyway failed migrations: 0
Duplicate signs: 0
Duplicate question references: 0
Orphan choices: 0
Production health failures during backup: 0
Container restarts during backup: 0
```

## Final closure verification

The closure verification was performed without another restore and without
writing to the production database:

```text
Encrypted artifacts on VPS: 12
Encrypted checksum sidecars: 12
Encrypted checksum failures: 0
Zero-byte encrypted artifacts: 0
Plaintext backup artifacts: 0
Private age identities on VPS: 0
Off-site encrypted artifacts: 12
Off-site checksum failures: 0
Unexpected off-site ACL entries: 0
Backup timer: enabled and active
Latest backup service result: success
Latest backup service exit: 0
Normal monitoring alerts: 0
Production container restarts: 0
Unexpected public application listeners: 0
```

The configuration retention dry-run was corrected to evaluate configuration,
environment, Caddy, and uploads as independent artifact series. A synthetic
test created six generations per type, pruned only the selected type to four,
preserved its newest generation, left the other types unchanged, and removed
all test files. The production dry-run selected no current configuration,
environment, or Caddy artifact for deletion.

```text
mvn clean test: PASSED (115 tests)
mvn verify: PASSED (115 unit tests; 186 integration tests, 21 skipped)
mvn package -DskipTests: PASSED
ShellCheck: PASSED
bash -n: PASSED
systemd-analyze verify: PASSED
docker compose config: PASSED
Caddy validation: PASSED
git diff --check: PASSED
Production smoke: PASSED
```

The production smoke covered the frontend, actuator health, traffic signs,
lessons, random quiz, login, authenticated user lookup, user progress,
`robots.txt`, and `sitemap.xml`. All returned HTTP 200; the actuator reported
`UP`.

## Known limitations

- The operator off-site copy is verified but currently manual. A failed manual
  transfer does not invalidate the encrypted VPS copy, but it reduces
  protection against complete VPS loss.
- Supabase Free does not provide the same retained daily backup guarantees as
  paid plans; logical backup RPO is therefore the daily timer interval.
- Full application startup against restored production user data is
  intentionally excluded.
- Registry-backed image restoration and automated deployment rollback belong
  to Gate 5D.
- The server has no swap; the backup job is small and measured, but disk and
  memory monitoring remain required.
