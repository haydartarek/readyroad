# ReadyRoad Content Governance

## Framework

Content Governance is data-driven. One reusable test engine reads `review-index.json`, then validates every active category through its file under `category-reviews`. Adding a category never requires a new Java test class or category-specific branch in the test engine.

```text
src/main/resources/data/content_governance/
  governance-engine/category-review.schema.json
  category-reviews/<category>.json
  review-index.json
  legal-sources.json
  terminology-glossary.json

docs/content-governance/
  governance-engine/
  category-reviews/<category>/
  content-change-log.md
```

The machine-readable central dashboard is `src/main/resources/data/content_governance/review-index.json`.

## Canonical Ownership

- `src/main/resources/data/signs_import/<signCode>/sign.json` is the only runtime source for a sign's name, summary, description, driver guidance, exceptions, category, and image path.
- `questions.json` and `exams.json` contain assessment content only. They reference their owning sign through the `signCode` prefix in every question ID.
- `data/lessons_content.json` contains lesson content. It may teach sign families and reference sign codes, but it must not become a second catalog of sign descriptions.
- Governance files contain source IDs, review metadata, validation rules, quality metrics, and terminology. They must never duplicate runtime sign descriptions.
- Production database rows are synchronized from canonical files. Direct production content edits are prohibited.

## Required Change Workflow

1. Identify the canonical item and the legal or educational claim being changed.
2. Verify the claim against an official Belgian source already registered in `legal-sources.json`, or add a new official source first.
3. Record the reason, source IDs, original review language, and review date in the category review data.
4. Update all four languages without broadening or narrowing the legal meaning.
5. Run `ContentGovernanceFrameworkTest`, importer regression tests, and full backend verification.
6. Add the accepted content change to `content-change-log.md`.

## Language Policy

Dutch and French legal terminology follows the published legal texts. English and Arabic are faithful editorial translations. A translation may improve clarity but may not add an obligation, priority rule, penalty, exception, or deadline absent from the verified source.

## Review Policy

- Re-review affected content whenever an official Belgian rule or implementation date changes.
- Perform a full content review at least once every twelve months.
- A category remains open while it has an unresolved legal source, ambiguous question, missing translation, terminology conflict, or correction awaiting approval.
- `review-index.json` is the authoritative project-level status view; category data and reports provide the evidence.

## Central Review Index

| Category | Status | Last review | Signs | Questions | Lessons | Risk | Approval |
| --- | --- | --- | ---: | ---: | ---: | --- | --- |
| DANGER | APPROVED | 2026-07-18 | 34 | 272 | 1 | HIGH | APPROVED |
| INFORMATION | APPROVED | 2026-07-19 | 37 | 296 | 0 | HIGH | APPROVED |
| MANDATORY | APPROVED | 2026-07-19 | 18 | 144 | 13 | HIGH | APPROVED |
| PARKING | APPROVED | 2026-07-19 | 15 | 120 | 4 | HIGH | APPROVED |
| PRIORITY | APPROVED | 2026-07-20 | 16 | 128 | 28 | HIGH | APPROVED |
| PROHIBITION | APPROVED | 2026-07-20 | 33 | 264 | 19 | HIGH | APPROVED |
| ROAD_MANAGEMENT | APPROVED | 2026-07-20 | 9 | 72 | 0 | HIGH | APPROVED |
| ZONE | APPROVED | 2026-07-21 | 22 | 176 | 1 | HIGH | APPROVED |

The Markdown table is a readable summary. Automated validation always reads the JSON index.
