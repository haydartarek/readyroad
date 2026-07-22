# ReadyRoad Hosting Options

Last price verification: 2026-07-22
Currency and tax treatment vary by billing country. No purchase is authorized
by this document.

## Decision Drivers

- The measured backend uses about 414-445 MiB at idle after initialization.
- A 512 MB container reached about 87 percent memory and took about 126 seconds
  to become healthy.
- A 1 GiB container with 1 CPU became healthy in about 52 seconds.
- The frontend already builds and operates successfully on Vercel.
- The database is small and fast, but production requires managed backups and
  a no-pause service level.
- SMTP must use HTTPS or an environment that permits outbound delivery.
- The operator needs a practical balance between cost and maintenance.

## Option A: Managed Hybrid

```text
Frontend: Vercel
Backend: Render Standard
Database: Supabase Pro
Email: Brevo HTTP API
```

| Attribute | Assessment |
|---|---|
| Backend plan | Render Standard, 1 CPU / 2 GB RAM |
| Backend price | USD 25/month |
| Database price | Supabase Pro, USD 25/month organization base price before overages |
| Frontend | Vercel current plan, subject to account/commercial-use eligibility |
| Email | Brevo Free initially, subject to quota and commercial terms |
| Expected base cost | About USD 50/month plus domain, taxes and overages |
| Setup complexity | Low |
| Maintenance | Low; provider manages host and runtime infrastructure |
| Security responsibility | Application, secrets, access and provider configuration |
| Backup responsibility | Supabase managed backups plus independent logical backup |
| Scalability | Straightforward vertical upgrades |
| Downtime risk | Low after warm service migration |
| SMTP support | Prefer HTTPS API; SMTP policy becomes irrelevant |
| Best for | Operator who prioritizes simplicity over minimum cost |

Why not Render Starter: Starter supplies 0.5 CPU and 512 MB. The measured
512 MB footprint leaves insufficient production headroom, so paying USD 7 for
the same memory ceiling does not resolve the core capacity risk.

## Option B: VPS Backend Only

```text
Frontend: Vercel
Backend: Hetzner CX23 with Docker and Caddy
Database: Supabase Pro
Email: Brevo HTTP API
```

| Attribute | Assessment |
|---|---|
| VPS plan | Hetzner CX23, 2 vCPU / 4 GB RAM / 40 GB disk / 20 TB EU traffic |
| Region | Nuremberg, Germany |
| VPS price | EUR 6.53/month including 19 percent German VAT after the 2026-06-15 adjustment; billing locale can differ |
| IPv4 | Approximately EUR 0.60/month from the last official price reviewed; must be reconfirmed at checkout |
| Provider backups | 20 percent of server price, about EUR 1.31/month |
| Database | Supabase Pro, USD 25/month at commercial launch |
| Expected base cost | About EUR 8.44/month for VPS, IPv4 and provider backup, plus USD 25/month Supabase Pro, domain, taxes and overages |
| Setup complexity | Medium |
| Maintenance | OS updates, Docker, Caddy, firewall, logs and recovery are operator-owned |
| Security responsibility | High for the VPS; database remains managed |
| Backup responsibility | VPS backup plus independent database logical backup |
| Scalability | Vertical upgrade or replacement host; adequate year-one headroom |
| Downtime risk | Low with parallel deployment and tested rollback |
| SMTP support | Available, but HTTPS email API remains preferred |
| Best for | Cost-conscious operator comfortable maintaining one Linux server |

This is the recommended architecture because it removes Render cold starts,
provides substantial memory/CPU headroom, keeps the frontend and database
managed, and avoids placing all production responsibilities on one machine.

## Option C: Full VPS

```text
Frontend: Docker on VPS
Backend: Docker on VPS
Database: self-hosted PostgreSQL or managed Supabase
Email: external HTTP provider
Monitoring: operator-managed
```

| Attribute | Assessment |
|---|---|
| VPS example | Hetzner CX33, 4 vCPU / 8 GB RAM / 80 GB disk / 20 TB EU traffic |
| VPS price | EUR 10.10/month including 19 percent German VAT after the 2026-06-15 adjustment, excluding IPv4 and backups |
| Expected base cost with provider backup/IPv4 | Roughly EUR 12.72/month before domain, taxes and external storage; add Supabase if database remains managed |
| Setup complexity | High |
| Maintenance | Frontend, backend, proxy, host, observability and potentially PostgreSQL are operator-owned |
| Security responsibility | Highest |
| Backup responsibility | Highest; database point-in-time recovery must be designed and tested |
| Scalability | Manual; one host becomes a fault domain |
| Downtime risk | Higher without a second node/load balancer |
| SMTP support | Available, but reputation and deliverability remain external concerns |
| Best for | Experienced operator accepting ongoing infrastructure work |

Self-hosting PostgreSQL on the same VPS is rejected for the first commercial
release. It saves a managed database fee but creates a single failure domain and
adds patching, replication, backup, restore and security duties.

## Provider Cross-Check

| Provider/plan | CPU | RAM | Storage | Published monthly price | Backup | Assessment |
|---|---:|---:|---:|---:|---|---|
| Hetzner CX23 | 2 vCPU | 4 GB | 40 GB | EUR 6.53 incl. 19 percent VAT after 2026-06-15; IPv4 extra | 20 percent of server price | Recommended backend VPS |
| Hetzner CX33 | 4 vCPU | 8 GB | 80 GB | EUR 10.10 incl. 19 percent VAT after 2026-06-15; IPv4 extra | 20 percent of server price | Full-stack or additional growth headroom |
| DigitalOcean Basic | 1 vCPU | 2 GB | 50 GB | USD 12 | Weekly 20 percent or daily 30 percent | Valid managed-console alternative, less capacity per unit cost |
| DigitalOcean Basic | 2 vCPU | 4 GB | 80 GB | USD 24 | Weekly 20 percent or daily 30 percent | Similar capacity to recommendation at higher price |
| Render Standard | 1 CPU | 2 GB | Provider-managed | USD 25 | Application filesystem remains unsuitable for durable uploads | Lowest operational burden |
| Render Starter | 0.5 CPU | 512 MB | Provider-managed | USD 7 | Same memory ceiling as current free service | Rejected by measured memory headroom |

OVHcloud was reviewed as a plausible EU provider, but an exact comparable plan
price was not reliably captured from the official page during this audit. It is
not used as the price baseline and should be rechecked before purchase.

## Database Models Compared

| Model | Backups | Maintenance | Security | Availability | Cost | Decision |
|---|---|---|---|---|---|---|
| Supabase Pro | Managed daily backups with documented retention; add independent logical backup | Low | Shared responsibility | Managed service | USD 25/month base | Recommended for commercial launch |
| Other managed PostgreSQL | Provider-specific | Low | Shared responsibility | Usually managed | Commonly higher than current DB need | Re-evaluate only if Supabase becomes unsuitable |
| PostgreSQL on application VPS | Entirely operator-owned | High | Entirely operator-owned | Single-host risk without replication | Lowest direct fee | Rejected for initial launch |

Supabase Free can remain during a non-commercial test period only if its pause,
backup and quota limits are explicitly accepted. It is not the recommended
commercial production database plan.

## Email Provider Comparison

| Provider | Free allowance reviewed | HTTPS API | Domain controls | Logs/webhooks | Decision |
|---|---|---|---|---|---|
| Brevo | 300 emails/day | Yes | SPF, DKIM and DMARC configuration | Transactional delivery events and webhooks | Recommended initial provider |
| Resend | 3,000/month and 100/day | Yes | One custom domain on free plan; SPF/DKIM and optional DMARC | 30-day logs on free plan | Recommended fallback; review US account metadata handling |
| Mailgun | 100/day free | Yes | Custom domain | One-day free log retention | Viable fallback |
| Amazon SES | Usage-priced; current pricing model must be confirmed for the account/region | Yes | Domain verification and production-access process | Provider telemetry | Economical at scale but excessive setup for initial volume |

Brevo is preferred because the initial volume estimate fits the free daily quota,
it supports an HTTPS transactional API, and it avoids Render/VPS SMTP-port
dependencies. Gate B must include a DPA review and domain SPF, DKIM and DMARC.

## Reverse Proxy Comparison

| Proxy | Automatic TLS | Docker fit | Complexity | Decision |
|---|---|---|---|---|
| Caddy | Native automatic HTTPS | Strong | Low | Recommended |
| Nginx | External certificate automation required | Strong | Medium | Valid but unnecessary complexity here |
| Traefik | Native ACME and service discovery | Strong | Medium/high | Better for many dynamic services than this two-service topology |

Only Caddy should be deployed for the selected VPS architecture.

## Recommendation

Select Option B for Gate B: Vercel frontend, Hetzner CX23 backend in Nuremberg,
Supabase Pro database, Brevo HTTP API, Caddy TLS, and Better Stack monitoring.
Keep Render active through cutover and rollback validation.

For an operator who does not want Linux maintenance, select Option A instead.
The technical minimum is Render Standard; Render Starter is not sufficient.

## Official Sources

- [Render pricing](https://render.com/pricing)
- [Render free services](https://render.com/docs/free)
- [Supabase pricing](https://supabase.com/pricing)
- [Hetzner 2026 price adjustment](https://docs.hetzner.com/general/infrastructure-and-availability/price-adjustment/)
- [Hetzner European Cloud specifications](https://www.hetzner.com/european-cloud/)
- [Hetzner backup billing](https://docs.hetzner.com/cloud/billing/faq/)
- [Hetzner locations](https://docs.hetzner.com/cloud/general/locations/)
- [DigitalOcean Droplet pricing](https://www.digitalocean.com/pricing/droplets)
- [Brevo plans](https://help.brevo.com/hc/en-us/articles/208589409-About-Brevo-s-pricing-plans)
- [Brevo webhooks](https://developers.brevo.com/docs/how-to-use-webhooks)
- [Resend pricing](https://resend.com/pricing)
- [Resend regions](https://resend.com/docs/dashboard/domains/regions)
- [Mailgun pricing](https://www.mailgun.com/pricing/)
- [Amazon SES pricing](https://aws.amazon.com/ses/pricing/)
