# Gate 5B: Production Monitoring, Alerting, and Logging

## Scope

Gate 5B adds operational visibility without changing application behavior,
API contracts, the database schema, content, or SEO. Supabase remains the only
database. Internal application ports remain bound to loopback.

Production endpoints:

- `https://readyroad.be`
- `https://api.readyroad.be/actuator/health`
- `https://readyroad.be/robots.txt`
- `https://readyroad.be/sitemap.xml`

Production containers:

- `readyroad-caddy`
- `readyroad-frontend`
- `readyroad-backend`

## Monitoring Inventory

### Baseline on 2026-07-24

| Signal | Baseline |
| --- | --- |
| VPS load average | `0.08, 0.08, 0.07` |
| VPS memory | 1,053 MiB used, 6,887 MiB available |
| Swap | Not configured |
| Root filesystem | 7% used |
| Inodes | 2% used |
| Hostinger hPanel CPU | 2% |
| Hostinger hPanel memory | 13% |
| Hostinger hPanel disk | 6 GB / 100 GB |
| Backend memory | 445 MiB |
| Frontend memory | 148 MiB |
| Caddy memory | 53 MiB |
| Container restart counts | 0 for all three containers |
| Unexpected public ports | 0 |
| Docker log storage before Caddy recreation | 5.9 MiB |
| Docker log storage after Caddy recreation | About 55 KiB |
| Certificate minimum lifetime | 89 days |

### Existing capabilities

- Hostinger hPanel provides historical CPU, RAM, process, disk, inode, and
  network graphs.
- Docker health checks exist for all three production containers.
- Spring Boot exposes only `health` and `info` under the active secure profile.
- Caddy writes JSON access logs to stdout.
- Docker uses the `local` log driver with compression, 10 MiB per file, and
  five files per container.
- UFW permits only TCP 22, 80, and 443.
- Fail2Ban protects SSH.
- `sysstat` collects host resource history.
- Caddy certificate data is persistent in `readyroad-caddy-data`.

### Missing capabilities before Gate 5B

- No five-minute external checks for all four public endpoints.
- No automatic local checks for resource thresholds, container restart
  changes, 5xx bursts, certificate lifetime, or security-state drift.
- No explicit journald retention limit.
- Sensitive OAuth and reset values in URL query parameters were not filtered
  from Caddy access logs.

## Selected Stack

| Component | Purpose | Decision |
| --- | --- | --- |
| GitHub Actions | External checks and GitHub Issue incidents | Selected |
| Hostinger hPanel | Historical VPS resource graphs | Selected |
| `systemd` timer and shell monitor | Internal resources, Docker, TLS, 5xx, security | Selected |
| Docker health checks | Per-container liveness | Retained |
| Caddy JSON logs | Request status and duration | Retained and redacted |
| UptimeRobot | External uptime and email alerts | Deferred; requires a separately approved external account/contact |
| Better Stack | Uptime, telemetry, and alerting | Rejected for this gate; unnecessary external account and ingestion |
| HetrixTools | External monitoring | Rejected; duplicate coverage |
| Grafana Cloud | Metrics and logs | Rejected; excessive scope and vendor ingestion for three containers |
| Sentry | Application exception telemetry | Rejected; SDK/application changes are outside Gate 5B |
| Uptime Kuma | Self-hosted uptime | Rejected; adds a public service and persistent container |
| Prometheus/Grafana | Host and application metrics | Rejected; unnecessary VPS pressure |

This stack adds no continuously running monitoring container and does not mount
the Docker socket into any container.

## Installed Components

| Component | Production path |
| --- | --- |
| Monitor script | `/usr/local/sbin/readyroad-monitor` |
| Monitor service | `/etc/systemd/system/readyroad-monitor.service` |
| Monitor timer | `/etc/systemd/system/readyroad-monitor.timer` |
| Monitor state | `/var/lib/readyroad-monitor` |
| Journald limits | `/etc/systemd/journald.conf.d/readyroad-limits.conf` |
| External workflow | `.github/workflows/production-uptime.yml` on the default branch |

The timer runs five minutes after the previous execution, with a small
randomized delay to avoid synchronized load.

## External Monitoring

The GitHub Actions workflow checks every five minutes:

| Check | Expected result |
| --- | --- |
| Frontend | HTTP 200 |
| Backend health | HTTP 200 and JSON `status=UP` |
| Robots | HTTP 200 |
| Sitemap | HTTP 200 |

The workflow never records response bodies in an incident. A failed check opens
or updates one GitHub Issue named `[monitoring] ReadyRoad production outage`.
A successful recovery check comments on and closes the open incident. The
workflow failure is also visible through GitHub Actions notifications.

The workflow has only `contents: read` and `issues: write` permissions. It uses
the ephemeral repository token and requires no application, VPS, Supabase, or
deployment secrets.

## Local Monitoring

The local monitor checks:

- CPU use and load average.
- RAM use.
- Root and Docker filesystem use.
- Root and Docker inode use.
- Network byte counters.
- Uptime and boot ID changes.
- Docker daemon state.
- Expected container presence, running state, health, and restart count.
- Unexpected running containers.
- Frontend and Backend loopback health endpoints.
- Caddy 5xx count over the previous five minutes.
- Production log patterns that may expose credentials or tokens.
- Total Docker container log storage.
- TLS expiry for apex, `www`, and API hosts.
- UFW, Fail2Ban SSH jail, and unexpected public listeners.

The one-shot service has a 10% CPU quota, 128 MiB memory limit, low CPU
priority, idle I/O priority, and systemd hardening. A normal run consumes about
0.8 CPU seconds and does not remain resident.

## Thresholds

| Signal | Warning | Critical |
| --- | --- | --- |
| CPU | At least 80% for three checks | Operational review |
| Load / vCPU | At least 100% for three checks | Operational review |
| RAM | 80% | 90% |
| Disk | 75% | 85% |
| Inodes | 75% | Operational review |
| Caddy 5xx | At least 3 in 5 minutes | Failed health endpoint |
| Certificate lifetime | 30 days | 14 days |
| Docker container logs | 500 MiB total | Disk threshold |
| Container restarts | Any increase | At least 3 since the previous check |
| Sensitive log patterns | Not applicable | Any unredacted match |
| Unexpected public port | Not applicable | Any port other than 22, 80, or 443 |

Short CPU and deployment spikes do not alert because CPU and load thresholds
require three consecutive monitor cycles.

## Logging Policy

### Caddy

- Format: JSON.
- Location: container stdout through Docker `local` log driver.
- Sensitive headers: Caddy's default redaction covers `Authorization`,
  `Proxy-Authorization`, `Cookie`, and `Set-Cookie`.
- Sensitive query values explicitly replaced with `REDACTED`:
  `token`, `code`, `state`, `access_token`, `refresh_token`, `id_token`,
  `api_key`, `apikey`, `secret`, and `password`.
- Retention bound: 10 MiB x five files, compressed.

The old Caddy container was recreated after redaction was enabled. TLS state
remained in the named Caddy volume. A controlled probe confirmed that the
literal test value appeared zero times while `REDACTED` appeared in the
corresponding access records.

### Backend and Frontend

- Location: container stdout/stderr through Docker `local` log driver.
- Retention bound: 10 MiB x five files per container, compressed.
- Global request/response body logging is disabled.
- Production secret-pattern scans output counts only and never print matches.

Spring Boot logs include timestamp, level, and logger. Caddy provides method,
host, URI, status, and duration at the request boundary. Correlation IDs remain
deferred because adding them requires application code and regression coverage;
they are not necessary for the current single-backend topology.

### Host security and services

- SSH, Fail2Ban, UFW, Docker daemon, and monitor events use journald.
- Journald maximum persistent use: 512 MiB.
- Journald minimum free disk reserve: 2 GiB.
- Runtime journal maximum: 128 MiB.
- Maximum retention: 30 days.
- Maximum individual journal period: one day.
- Compression: enabled.

## Alert Channels

1. GitHub Issue incident plus GitHub Actions workflow failure for external
   endpoint outages.
2. Structured `readyroad-monitor` entries in journald for local resource,
   container, TLS, 5xx, log, and security alerts.
3. Hostinger hPanel for historical resource review and provider-level events.

No SMTP credential, webhook secret, personal application token, or monitoring
agent credential is stored on the VPS.

## Safe Alert Tests

The following simulations do not stop containers or alter thresholds:

```bash
sudo /usr/local/sbin/readyroad-monitor container
sudo /usr/local/sbin/readyroad-monitor disk
sudo /usr/local/sbin/readyroad-monitor ssl
sudo /usr/local/sbin/readyroad-monitor 5xx
sudo /usr/local/sbin/readyroad-monitor security
```

Each test must produce one `event=simulated_alert` entry. The external workflow
is tested using `workflow_dispatch` first with `failure`, then with `recovery`.
The failure opens or updates the incident and the recovery closes it.

No rollback action is required for simulations. The production state is not
changed.

### Validation results

| Test | Result |
| --- | --- |
| Normal external run | Passed; four endpoints healthy |
| Simulated external failure | Passed; incident #1 created |
| Simulated external recovery | Passed; incident #1 closed |
| End-to-end failure alert time | About 85 seconds in the observed GitHub run |
| Recovery workflow execution | 6 seconds |
| Container simulation | Delivered immediately to journald |
| Disk simulation | Delivered immediately to journald |
| SSL simulation | Delivered immediately to journald |
| 5xx simulation | Delivered immediately to journald |
| Security simulation | Delivered immediately to journald |
| Local simulation entries | Five of five present |
| Caddy literal sensitive probe | Zero occurrences |
| Caddy redaction marker | Three corresponding occurrences |
| Local monitor healthy run | `alerts=0`, service result success |
| Local scheduled run | Passed automatically with `alerts=0` |
| Gate 5A Backend CI | Tests, verify, package, and Docker build passed |
| Authenticated user progress | Five HTTP 200 responses; 277 ms average |
| User progress response consistency | One unique SHA-256 response hash |
| Admin authorization | Authenticated 200; anonymous 401 |
| Production browser console | Zero console or page errors on four public pages |
| Production public smoke | Home, login, signs, lessons, robots, sitemap, health, and quiz passed |

## Operational Commands

### Current resources

```bash
uptime
free -m
df -h /
df -h /var/lib/docker
df -i /
sar -u 1 5
sar -r 1 5
```

### Docker

```bash
docker ps
docker stats --no-stream
docker system df
docker inspect --format '{{.Name}} {{.State.Status}} {{if .State.Health}}{{.State.Health.Status}}{{end}} {{.RestartCount}}' \
  readyroad-caddy readyroad-frontend readyroad-backend
```

Never use `docker compose down -v` during incident response.

### Monitor

```bash
systemctl status readyroad-monitor.timer --no-pager
systemctl list-timers readyroad-monitor.timer --no-pager
sudo systemctl start readyroad-monitor.service
sudo journalctl -t readyroad-monitor --since -1h --no-pager
```

### Logs

```bash
docker logs --since 15m readyroad-caddy
docker logs --since 15m readyroad-backend
docker logs --since 15m readyroad-frontend
sudo journalctl -u docker --since -1h --no-pager
sudo journalctl -u ssh --since -1h --no-pager
sudo fail2ban-client status sshd
sudo journalctl --disk-usage
```

Do not paste production log output into tickets without reviewing it. Use
counts and sanitized path/status summaries for credential investigations.

### Certificates

```bash
echo | openssl s_client -servername readyroad.be -connect readyroad.be:443 2>/dev/null \
  | openssl x509 -noout -issuer -dates
echo | openssl s_client -servername www.readyroad.be -connect www.readyroad.be:443 2>/dev/null \
  | openssl x509 -noout -issuer -dates
echo | openssl s_client -servername api.readyroad.be -connect api.readyroad.be:443 2>/dev/null \
  | openssl x509 -noout -issuer -dates
```

## Review Schedule

### Daily

- Review open monitoring incidents.
- Confirm the external workflow's latest scheduled run.
- Review new `WARNING` and `CRITICAL` local monitor entries.

### Weekly

- Review Hostinger CPU, RAM, disk, inode, process, and network graphs.
- Review restart counters and Docker disk use.
- Review Caddy 5xx summaries.
- Confirm UFW and Fail2Ban remain active.

### Monthly

- Confirm certificate lifetime is above 30 days.
- Review journald and Docker retention use.
- Run one non-destructive local simulation.
- Review monitoring workflow permissions and the incident history.
- Confirm documented expected containers and public ports remain current.

## Incident Response

1. Confirm whether the alert is external, local, or both.
2. Check `readyroad.be` and the public health endpoint from a separate network.
3. Check Docker state, health, restart counts, and recent logs.
4. Check CPU, RAM, disk, inodes, and Hostinger graphs.
5. Check Caddy 5xx records and upstream errors without exposing query values.
6. If the current immutable release is defective, use the verified release
   rollback procedure. Do not change Supabase.
7. Verify frontend HTTP 200, Backend `UP`, and all container health checks.
8. Record cause, start time, recovery time, and corrective action.

Escalate immediately for a failed health endpoint, repeated restarts, disk over
85%, an expiring certificate under 14 days, unexpected public ports, or any
unredacted credential pattern.

## Known Limitations and Deferred Items

- GitHub scheduled workflows may begin later than the exact cron minute. They
  run from the default branch and are not a hard real-time paging system.
- GitHub notification delivery depends on the repository owner's GitHub
  notification settings.
- Hostinger hPanel provides resource history, but Gate 5B does not add a
  third-party push agent for local metrics.
- Swap is not configured. Current available RAM is ample; adding swap is a
  separate infrastructure decision.
- Backup failure monitoring starts after Gate 5C defines a backup job.
- Centralized log ingestion, tracing, and application correlation IDs remain
  deferred because they require external ingestion or application changes.
- Render Free remains unchanged but is not required for production rollback.
  Vercel remains an emergency frontend fallback.

## Rollback

Monitoring rollback does not touch application images, releases, or volumes:

```bash
sudo systemctl disable --now readyroad-monitor.timer
sudo rm /etc/systemd/system/readyroad-monitor.service
sudo rm /etc/systemd/system/readyroad-monitor.timer
sudo rm /usr/local/sbin/readyroad-monitor
sudo rm /etc/systemd/journald.conf.d/readyroad-limits.conf
sudo systemctl daemon-reload
sudo systemctl restart systemd-journald
```

For Caddy logging rollback, restore
`/opt/readyroad/current/Caddyfile.pre-gate5b`, validate it, and recreate only the
Caddy container. Do not remove `readyroad-caddy-data` or
`readyroad-caddy-config`.
