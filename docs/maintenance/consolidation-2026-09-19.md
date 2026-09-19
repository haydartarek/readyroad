# Backend consolidation — 2026-09-19

`main` at `8047339a55d754230b7d0b62cc891b6546439a0b` is the application baseline, including Stripe. No application behavior or dependency versions change in this consolidation. The cooldown integration fixture is corrected to use Hibernate-compatible UTC timestamp binding.

## Decisions

- Removed three archived old worktrees and twelve non-main local branches. Main is the only active local branch/worktree.
- Removed three stashes only after preserving every stash commit, index and untracked parent in a separate verified bundle.
- Editorial worktree changes and dynamic-category work are already integrated or superseded by newer main. The six category commits match main's squash patch exactly.
- The old permanent image deletion proposal is archived, not adopted: it changes retention/audit behavior. Current workflow/metadata fixes remain intact.
- The July rewrite of all 30 lessons is archived, not silently treated as merged. New lesson wording requires a separate content decision. The August lesson-title stash already matches main.
- The historical Java/Flutter monorepo branch has unrelated history; preserve it in the bundle, not as an alternative development branch.
- The ignored V238 SQL backup matches the tracked migration and was archived outside the source tree. No migrations were edited.
- Removed 9 remote branches proven to be ancestors of origin/main and unrelated to open PRs. Other remote branches remain pending explicit approval because some back open PRs or are not literal ancestors. Backend PR #203's DevExam removal is already present in newer main. Exact branch SHA guards and classifications are in the private archive.

## Recovery archive

The private archive is `worktree-rescue-20260919/verified-snapshot`, next to the two repositories, not inside either checkout. Its README describes recovery commands; `*-refs.txt`, `inventory.json`, `stash-inventory.json` and `remote-branch-decisions.json` preserve the exact original state and decisions. Keep this archive: not every old proposal was adopted into main. Do not publish its patches or diagnostic logs.

The Git bundles were verified before removing local worktrees, branches and stashes. Changed/untracked worktree files and old ignored source backups were copied and SHA-256 checked. Active `.env` files were left in place and unchanged.

## Release boundary

This consolidation prepares main for future work. It does not authorize a production deployment. Its paired commit messages use `Release-Pair: consolidation-20260919`; the existing Deploy Production workflow skips automatic deployment for this marker. No deployment workflow is dispatched.

## Bundle checksums (SHA-256)

- `readyroad.bundle`: `e8963f00bb59461a5d2ee286bdd3e7167a2b29a0a8739eb6c8ce6f4cd199689c`
- `readyroad-stashes.bundle`: `ab88df74fb32271b6a75f2617a26e2ccf70a199f1630cfa9acea206aab06954f`

## Test maintenance

The cooldown test inserted `LocalDateTime` directly through JDBC, while Hibernate binds the query cutoff with `hibernate.jdbc.time_zone=UTC`. On a non-UTC JVM this makes the fixture and cutoff disagree. The fixture now binds both timestamps with the same UTC JDBC calendar; the eight-hour rule, query, and assertions remain unchanged.

## Validation

Full `mvn --batch-mode --no-transfer-progress clean verify` executed 446 unit cases and selected 417 integration cases. The only three failures were the mixed-timezone cooldown fixture; 19 cases are conditionally skipped or explicitly disabled in the existing suite. After the fixture correction, targeted Maven verify passed all eight cooldown cases under both Europe/Paris and UTC, with the reported JVM timezones checked in XML. This yields 446 passing unit cases and 398 passing executed integration cases; no executed failures remain.

The initial full-run failure report is preserved alongside both successful targeted rechecks; this is not represented as a second full-suite run. The 19 exclusions are 10 opt-in external-database portability cases, three Flyway checks disabled under H2, five old Phase 6 API cases disabled in source, and one duplicate compliance case disabled in source.

Final logs, XML reports and `final-test-summary.json` are retained outside the repository under `worktree-rescue-20260919/final-validation`. Generated build directories and old diagnostic logs were removed from active source trees after preserving the evidence.
