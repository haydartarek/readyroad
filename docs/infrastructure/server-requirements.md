# ReadyRoad Server Requirements

Status: Gate A design, not provisioned
Last reviewed: 2026-07-22

## Workload Boundary

The proposed VPS hosts only the Spring Boot backend, Caddy, and lightweight
monitoring/log forwarding. Vercel continues to host Next.js and Supabase
continues to host PostgreSQL. The production database must not run on this host.

## Measurement Basis

- Backend memory: about 414 MiB at 1 CPU / 1 GiB and 445 MiB at 512 MB.
- 512 MB environment: about 126 seconds to healthy and 87 percent memory.
- 1 CPU / 1 GiB environment: about 52 seconds to healthy and 40 percent memory.
- Backend image content: about 214 MiB.
- Startup also performs Flyway validation and canonical content reconciliation.
- Initial usage envelope: 25-50 peak concurrent users and up to 1.5 million API
  requests/month. This is estimated and requires production analytics.

## Capacity Levels

| Resource | Absolute minimum for a short test | Recommended production baseline | Scale review threshold |
|---|---:|---:|---:|
| vCPU | 1 | 2 | Sustained CPU above 60 percent for 15 minutes |
| RAM | 2 GB | 4 GB | Sustained host RAM above 70 percent or container above 1.5 GB |
| SSD | 25 GB | 40 GB | Disk above 70 percent after cleanup/rotation |
| Network | 1 TB/month | 5+ TB/month | 75 GB/month application egress is the initial observation trigger |
| Public IPv4 | 1 | 1 | N/A |

The 2 GB minimum is not the recommended purchase. Four GB provides headroom for
the JVM, Docker, Caddy, operating-system cache, temporary image pulls, rolling
replacement and monitoring.

## Recommended Plan

```text
Provider: Hetzner Cloud
Plan: CX23
Region: Nuremberg, Germany
Operating system: Ubuntu 24.04 LTS
CPU: 2 vCPU
RAM: 4 GB
Storage: 40 GB SSD
Traffic: 20 TB in EU location according to current plan specification
IPv4: One public IPv4
Provider backup: Enabled
Snapshot: Before high-risk host changes; not a substitute for backup
```

Nuremberg is selected for proximity to Belgian users and Supabase's configured
EU region. Actual latency must be measured before traffic cutover.

## Host Software

- Ubuntu 24.04 LTS with unattended security updates.
- Docker Engine from an official repository.
- Docker Compose plugin.
- Caddy as the only reverse proxy and TLS terminator.
- Firewall using the provider firewall and host firewall.
- Better Stack collector/agent only if selected in Gate B.
- No database server, control panel, mail server or FTP server.

## Public Network Surface

| Port | Exposure | Purpose |
|---|---|---|
| 22/tcp | Restricted to approved administrator IPs; temporary fallback procedure documented | SSH administration |
| 80/tcp | Public | ACME challenge and redirect to HTTPS |
| 443/tcp | Public | `api.readyroad.be` HTTPS API |
| Backend application port | Docker private network only | Caddy-to-Spring communication |

No database, Docker daemon, actuator management port, or application port is
publicly exposed. The public health endpoint should expose only the existing
sanitized health response.

## Docker Production Design

Future Gate B design:

```text
Internet
  -> Caddy container :80/:443
       -> backend container :8890 on private Docker network

Volumes:
  caddy_data        TLS state
  caddy_config      Caddy runtime state
  backend_logs      optional short local buffer only
```

Requirements:

- Use an immutable image tag: `vX.Y.Z` and/or Git SHA.
- Never deploy using `latest` alone.
- Backend keeps its existing non-root Java process.
- Caddy and backend have health checks.
- Restart policy is `unless-stopped` or equivalent.
- Add explicit memory and CPU limits after a staging measurement, initially
  reserving enough capacity for startup rather than matching idle usage.
- Configure Docker log rotation even when logs are forwarded.
- Environment file remains outside Git, owned by root, mode 0600.
- No Docker socket is mounted into application containers.
- Automatic image updaters such as Watchtower are not used in production.

## JVM and Pool Baseline

No JVM value is changed in Gate A. In Gate B, validate a bounded memory policy
against the 4 GB host, for example container-aware JVM sizing rather than a
fixed value copied from development. The existing database pool max of 5 is
appropriate for initial traffic and Supabase connection limits. Increase only
after observing wait time and database capacity.

## Storage Requirements

- Canonical bundled sign images are about 5.2 MB and remain in the image.
- Application images/content bundled into the JAR/image are immutable artifacts.
- User-uploaded images must not depend on container or Render ephemeral storage.
- Before enabling durable public uploads, select managed object storage with EU
  region, signed/admin upload controls, lifecycle policy and backup expectations.
- Local logs are a bounded buffer, not the system of record.
- Maintain at least 30 percent free disk for image pulls, temporary layers and
  rollback artifacts.

## Availability and Recovery Objectives

```text
Initial availability target: 99.5 percent
RPO: 24 hours
RTO: 2 hours
Deployment downtime target: 0-5 minutes
Rollback target: 5-15 minutes
```

This is a single-host backend. It is not high availability. A provider/host
outage requires reprovisioning or temporary rollback to Render.

## Environment Separation

| Environment | Database | Secrets | OAuth callbacks | Frontend origin | Email |
|---|---|---|---|---|---|
| Local | Local PostgreSQL/MySQL | Local ignored file | Localhost | Localhost | Disabled/test sink |
| Test | Isolated CI database | CI secrets | None/test-only | Test origin | Disabled/test sink |
| Preview | Dedicated non-production database/project | Preview secrets | Preview callback only | Vercel preview | Sandbox/test recipient policy |
| Production | Supabase production | VPS/provider secrets only | Production domain only | Canonical domain and controlled Vercel URL during transition | Verified production domain |

Automated tests and Vercel previews must never use the production database.

## Acceptance Measurements on Provisioned Host

Before cutover:

- Healthy in less than 90 seconds from a cold container start.
- Steady host RAM below 70 percent.
- No OOM kill or container restart loop.
- Warm health p95 below 500 ms from the monitoring region.
- Public traffic-sign endpoint p95 recorded and compared with Render baseline.
- Random quiz and lessons return correct counts and no duplicates.
- Database connection pool shows no sustained exhaustion.
- TLS grade and expiry monitoring are active.
- Disk/log growth observed during at least a 24-hour soak.

## Upgrade Path

Move to CX33 or a managed backend when resource thresholds persist after
application-level diagnosis. Do not scale solely from one transient spike.
For availability above 99.5 percent, design a second backend instance and a
managed load balancer; a larger single VPS does not provide high availability.
