# ReadyRoad Monitoring and Logging Plan

Status: Gate A proposal
Recommended primary tool: Better Stack, initial free tier if current limits and
terms remain suitable at Gate B

## Objectives

- Detect user-visible outages before support reports.
- Distinguish frontend, backend, database and external-provider failures.
- Alert on resource exhaustion, TLS expiry and failed backups.
- Preserve enough structured evidence to diagnose incidents without logging
  secrets or unnecessary personal data.
- Keep the initial toolset small and maintainable.

## Tool Decision

### Recommended

Better Stack can initially provide uptime monitors, status page, log ingestion,
error tracking and infrastructure telemetry in one system. The free offering
reviewed on 2026-07-22 listed 10 monitors, one status page, 100,000 exceptions,
3 GB logs with 3-day retention, and 30 GB metrics; current terms must be
reconfirmed before Gate B. Better Stack documents EU data storage by default.

### Alternatives reviewed

- UptimeRobot Free: up to 50 monitors at 5-minute intervals; useful as a simple
  uptime alternative but does not replace logs/resource metrics.
- Provider metrics: retain Hetzner, Vercel and Supabase metrics as supporting
  telemetry, not the only cross-service alert path.
- Grafana Cloud/Sentry: valid future options if telemetry volume or application
  error analysis outgrows the initial setup. Do not add them all at launch.

## Monitor Inventory

| Monitor | Target | Expected result | Interval | Failure rule |
|---|---|---|---|---|
| Frontend home | Canonical `/` | HTTP 200 and expected marker | 1-5 minutes according to approved plan | 2 consecutive failures |
| Backend health | `https://api.readyroad.be/actuator/health` | HTTP 200, status UP | 1 minute preferred | 2 consecutive failures |
| Traffic-sign canary | Public traffic-sign API | HTTP 200, non-empty response | 5 minutes | 2 consecutive failures or invalid marker |
| Lessons canary | Public lessons API | HTTP 200, expected shape | 5 minutes | 2 consecutive failures |
| Random quiz canary | Public random quiz API | HTTP 200, no duplicate IDs in sample | 5 minutes | 2 consecutive failures |
| TLS expiry | Frontend and API domains | Valid chain/hostname | Daily | Alert at 30, 14 and 7 days |
| Backup heartbeat | Nightly backup job | Success within window | Daily | Missing/failed after expected completion |
| VPS host | Agent/provider metrics | CPU, RAM, disk and reachability | 1 minute | Thresholds below |
| Status page | Monitor system | Public incident communication available | Provider managed | Manual/provider alert |

Authenticated canaries must use a dedicated least-privilege synthetic account,
never an administrator or real user. Do not store a password in source or expose
it in monitor output.

## Alert Thresholds

| Signal | Warning | Critical | Evaluation |
|---|---:|---:|---|
| Host CPU | > 70 percent | > 85 percent | 10 minutes |
| Host RAM | > 75 percent | > 85 percent | 10 minutes |
| Disk usage | > 70 percent | > 85 percent | Immediate after two samples |
| Backend restarts | 1 unexpected | 2+ in 15 minutes | Immediate |
| HTTP 5xx rate | > 1 percent | > 5 percent | 5 minutes with minimum request count |
| Health latency | p95 > 500 ms | p95 > 2 seconds | 10 minutes while warm |
| Traffic-sign latency | p95 > 3 seconds | p95 > 6 seconds | 15 minutes after baseline stabilization |
| DB pool utilization | > 70 percent | > 90 percent / timeout | 10 minutes |
| Database availability | Degraded | Unavailable | Immediate |
| Backup age | > 26 hours | > 36 hours | Immediate |
| TLS remaining | 30 days | 14/7 days | Daily |

Thresholds are initial values. Adjust from production baselines without hiding
real incidents.

## Notification Routing

```text
Critical availability/security/backup:
  Immediate owner alert plus status-page assessment

Warning capacity/latency:
  Owner alert during waking hours; incident if sustained

Release-specific errors:
  Release operator and project owner
```

At least two independent recovery contacts should exist before commercial
launch. Alert delivery must not depend only on ReadyRoad's own email provider.

## Logging Standard

Preferred format: structured JSON to stdout.

Required fields:

```text
timestamp
level
service
environment
release_version
request_id / correlation_id
event_name
http_method
route_template
status_code
duration_ms
```

Do not log full query strings or bodies by default. Use route templates rather
than user-controlled raw paths where practical.

Never log:

```text
Passwords
JWTs or refresh tokens
Authorization/Cookie headers
Reset tokens
OAuth codes or provider token bodies
JDBC URLs containing credentials
SMTP/API credentials
Full personal-data payloads
```

Email addresses and other personal identifiers must be removed, masked or
pseudonymized unless a documented incident/legal requirement justifies limited
collection. Current backend logging includes user email addresses in several
paths, and an OAuth failure path may log the provider response body. These are
Gate B security-hardening tasks.

## Retention and Rotation

- Docker local log driver: rotate, for example 10 MB per file and 3-5 files.
- Central application logs: begin with the provider's short free retention; set
  a documented 7-30 day production retention only when needed and privacy-safe.
- Security/audit records: separate policy based on legal need and access control.
- Do not keep debug logs enabled in production.
- Log volume and personal-data access are reviewed quarterly.
- Incident evidence is exported to restricted encrypted storage only when needed.

The current `application-prod.properties` file rotation does not necessarily
apply to Render because active profiles are `secure,postgresql`. Production
logging behavior must be validated from the actual active profile/environment.

## Dashboard

One operational dashboard should show:

- Frontend and backend uptime.
- Backend request rate, p50/p95/p99 latency and 4xx/5xx.
- Host CPU, RAM, disk and container restarts.
- JVM heap/non-heap and garbage collection when available.
- Database pool active/idle/pending connections.
- Supabase availability/connection count.
- Release version and deployment timestamp.
- Last successful backup and restore-test date.
- Email delivery, bounce and complaint counts after provider activation.

## Incident Process

1. Acknowledge and assign severity.
2. Confirm impact with a second signal.
3. Freeze unrelated deployments.
4. Restore service using the rollback plan when impact is active.
5. Post status communication without secrets or unsupported claims.
6. Preserve relevant redacted logs/metrics.
7. Close only after functional canaries and recovery checks pass.
8. Write a short root-cause and prevention record.

## Verification Before Launch

- [ ] Each monitor has been forced to fail and produced the expected alert.
- [ ] Backup heartbeat failure was simulated.
- [ ] CPU/RAM/disk telemetry is visible.
- [ ] Container restart is detected.
- [ ] SSL expiry monitor covers both frontend and API hostnames.
- [ ] Logs contain release and correlation identifiers.
- [ ] High-confidence secret scan of representative logs returns 0.
- [ ] PII review is complete.
- [ ] Status-page owner and communication procedure are assigned.

## Official Sources

- [Better Stack pricing](https://betterstack.com/pricing)
- [UptimeRobot pricing](https://uptimerobot.com/pricing/)
