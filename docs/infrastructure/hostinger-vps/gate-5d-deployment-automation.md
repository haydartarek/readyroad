# Gate 5D: Deployment Automation and Rollback

## Scope

This runbook automates immutable application releases on the existing
Hostinger VPS. It preserves the current Docker Compose architecture, Caddy
routing, loopback-bound application ports, Supabase database, monitoring, and
backup system.

It does not change application behavior, API contracts, database schema,
content, SEO, or the Docker topology.

## Audited manual flow

The previous production procedure required an operator to:

1. create a release directory;
2. clone the Backend and Frontend repositories;
3. apply an uncommitted Frontend Dockerfile build argument;
4. copy the environment, Compose file, and Caddyfile;
5. build and tag both images;
6. inspect image IDs and create a release manifest;
7. run Compose manually;
8. wait for health and run endpoint checks;
9. switch `/opt/readyroad/current`;
10. invoke the rollback script manually if validation failed.

The production Frontend Dockerfile change was committed before this gate, so
both application images are now reproducible from immutable Git commits.

Manual failure points included dirty source trees, mistyped image tags,
incomplete manifests, missing health checks, a non-atomic release decision,
forgotten rollback, and accidental deletion of a required release.

## Installed components

```text
/opt/readyroad/bin/readyroad-deploy
/opt/readyroad/bin/readyroad-rollback
/opt/readyroad/bin/readyroad-smoke
/opt/readyroad/bin/readyroad-deploy-lib
/opt/readyroad/bin/templates/docker-compose.yml
/opt/readyroad/bin/templates/Caddyfile.production
/var/log/readyroad-deploy/
```

All scripts and templates are included by the Gate 5C encrypted configuration
backup because they live below `/opt/readyroad/bin`.

## Deployment workflow

`readyroad-deploy`:

- uses Bash strict mode and a non-blocking global `flock`;
- requires explicit 40-character Backend and Frontend commit SHAs;
- validates refs, release IDs, dependencies, templates, and current production;
- checks out clean, detached Backend and Frontend commits;
- copies the existing root-only production environment without printing it;
- writes release-specific image tags into the copied environment;
- validates Compose before building;
- builds immutable Backend and Frontend images;
- records and verifies Docker content-addressed image IDs;
- checks the Backend JAR/runtime and starts an isolated Frontend candidate;
- creates a manifest before activation;
- creates an encrypted pre-deployment configuration backup;
- recreates Backend and Frontend using the candidate images;
- waits for Docker health and executes the production smoke suite;
- switches the `current` symlink atomically;
- recreates Caddy from the new release and validates production again;
- marks the manifest immutable after the final result;
- performs release-retention dry-run before execution.

Application containers cannot run as a complete blue/green pair because the
approved Compose architecture uses fixed container names and loopback ports.
Changing that would be a Docker architecture and routing change. Candidate
images are therefore built and checked before a guarded rolling activation;
any activation failure invokes automatic rollback.

## Deploy

Run with explicit immutable commits:

```bash
sudo /opt/readyroad/bin/readyroad-deploy \
  --backend-ref <40-character-backend-commit> \
  --frontend-ref <40-character-frontend-commit> \
  --release-id <release-id>
```

Branch names, tags, shortened SHAs, and omitted refs are rejected. Validate
without creating a release or container by using the exact commits intended
for production:

```bash
sudo /opt/readyroad/bin/readyroad-deploy \
  --backend-ref <40-character-backend-commit> \
  --frontend-ref <40-character-frontend-commit> \
  --dry-run
```

## Manifest

Every new `release-manifest.env` records:

```text
Release and version identifier
Backend and Frontend Git commits and requested refs
Backend, Frontend, and Caddy image names and IDs
Compose and Caddy SHA-256
Deployment start and completion timestamps
Rollback target
Operator
Duration
Health result
Rollback result
Final deployment status
```

No credentials, JWTs, cookies, database URLs, or OAuth values are written to
the manifest or deployment logs.

## Health gate

The reusable smoke suite validates:

```text
Frontend
Actuator status UP
Traffic signs
Lessons
Random quiz
Login
auth/me
Admin authorization
user-progress
robots.txt
sitemap.xml
```

Authentication material is held only in a mode `0700` temporary runtime
directory and is removed on exit. Passwords and JWTs are never logged.

## Automatic rollback

After container activation begins, any failed command triggers:

1. candidate cleanup;
2. verification of the previous manifest and image IDs;
3. recreation of the previous Backend and Frontend;
4. Docker health verification;
5. atomic restoration of the previous `current` link;
6. recreation and health verification of Caddy;
7. the complete production smoke suite;
8. a mode `0600` rollback state record.

Rollback never runs `docker compose down`, never uses `down -v`, and never
deletes volumes.

Manual rollback remains available:

```bash
sudo /opt/readyroad/bin/readyroad-rollback \
  /opt/readyroad/releases/<release-id>
```

Dry-run:

```bash
sudo /opt/readyroad/bin/readyroad-rollback \
  /opt/readyroad/releases/<release-id> --dry-run
```

## Release retention

The default keeps five valid releases. Cleanup always:

- previews actions first;
- protects the current and previous releases;
- protects the newest valid releases;
- skips failed releases for automatic deletion;
- does not remove Docker images or volumes.

## Controlled failure validation

The test-only option fails after candidate Backend and Frontend activation:

```bash
sudo /opt/readyroad/bin/readyroad-deploy \
  --backend-ref <40-character-backend-commit> \
  --frontend-ref <40-character-frontend-commit> \
  --release-id <unique-test-release> \
  --simulate-health-failure
```

The command must exit non-zero, report automatic rollback `PASSED`, restore
the prior symlink and images, and leave all production smoke checks green.

## Reboot validation

After a successful same-commit deployment:

```bash
sudo reboot
```

Verify Docker, Caddy, Frontend, Backend, monitoring timer, backup timer,
current release, HTTPS, private application ports, and the complete smoke
suite.

## Security and operational rules

- Run deployment and rollback as root through `sudo`.
- Always pass full 40-character Backend and Frontend commit SHAs to deployment.
- Keep `.env.production` mode `0600`; never copy it into Git.
- Never enable shell tracing.
- Never print Compose interpolation or environment data.
- Keep ports 3000 and 8890 bound only to `127.0.0.1`.
- Never restore or modify Supabase during deployment.
- Never remove volumes during release cleanup or rollback.
- Preserve deployment logs with the configured journald and filesystem
  rotation controls.

## Recovery

If automated rollback itself fails, stop. Do not retry deployment. Use the
Gate 5C configuration backup, recorded image IDs, rollback state file, and the
last healthy release directory to diagnose and recover without modifying
Supabase.

## Validation record

Validation completed on 2026-07-24 against these immutable inputs:

```text
Backend commit: c86255dae7700b79c6eb75fcc1d13079c0592c77
Frontend commit: 1ae36cc7525514d430609a9a30901f071242abab
Production release: 20260724-g5d-c86255d-1ae36cc
```

- Bash syntax, ShellCheck, Compose validation, and retention tests: passed.
- Deployment and rollback dry-runs: passed without production changes.
- Controlled post-activation failure: automatic rollback passed in 197 seconds.
- Successful immutable deployment: passed in 210 seconds.
- Production smoke suite: 11 of 11 checks passed.
- Deployment and rollback manifests: complete, read-only, and secret-free.
- Docker image IDs: verified before activation and preserved after reboot.
- VPS reboot recovery: Docker, Caddy, Frontend, Backend, monitoring, backup,
  certificates, release symlink, and production health passed.
- Supabase: unchanged. Gate 5C backup and manual restore verification remain
  active; deployment does not restore or migrate the database.
- Container restart counts after deployment and reboot: zero.
- Unexpected public ports: zero; application ports remain loopback-only.
- Sensitive values found in deployment logs and manifests: zero.
- Backend `mvn clean test`, `mvn verify`, and `mvn package -DskipTests`: passed.
- Production regression detected by the smoke suite: zero.
