# Governance Engine

## Purpose

`ContentGovernanceFrameworkTest` is the single executable governance engine. It discovers categories from the central index and evaluates their review data against canonical JSON, official source declarations, terminology, reports, images, lessons, questions, and exams.

## Add a Category

1. Change the category entry in `review-index.json` from `NOT_REVIEWED` to its actual workflow status.
2. Create `category-reviews/<slug>.json` using `category-review.schema.json` and the existing approved category as structural reference.
3. Create the category report from `category-report-template.md` and its machine-readable evidence files.
4. Put category-specific legal regression guards in the review file's `content_assertions` array.
5. Run `mvn -Dtest=ContentGovernanceFrameworkTest test`.

Do not create a category-specific Java test. New reusable assertion behavior belongs in the engine; category facts belong in category review data.

## Workflow States

- `NOT_REVIEWED`: catalog counts are indexed, but no review data or report is active.
- `OPEN`: review data exists and work is in progress.
- `REQUIRES_HUMAN_REVIEW`: unresolved decisions are explicitly recorded.
- `APPROVED`: all governance gates and evidence reports pass.

`risk_level` describes the consequence of incorrect content, not the number of unresolved defects. An approved category can remain high risk while having zero open findings.
