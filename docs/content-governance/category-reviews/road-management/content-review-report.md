# ROAD_MANAGEMENT Category Content Review

## Status

`APPROVED` on `2026-07-20` after all automated validation gates passed.

The review applies the current 1975 Road Code through 31 May 2027. The successor code starting on 1 June 2027 remains a future-law item and is not mixed into current learner answers.

## Scope

- 9 canonical `sign.json` files.
- 72 questions, eight per sign.
- 9 exams with 3 EASY, 3 MEDIUM and 2 HARD questions and a passing score of 6/8.
- No lesson page or active UI legal copy references these nine sign codes; historical Flyway migrations were audited but left immutable.
- Four languages: NL, EN, FR and AR.

## Quality Metrics

| Metric | Value |
| --- | --- |
| Signs reviewed | 9 |
| Questions reviewed | 72 |
| Exams reviewed | 9 |
| Lessons reviewed | 0 |
| Languages reviewed | 4 |
| Legal sources linked | 2 |
| Terminology standardized | 10 |
| Cross-language consistency | PASSED |
| Future-law items | 1 |
| Human review required | 0 |
| Critical issues | 0 remaining |
| Major issues | 0 remaining |
| Minor issues | 0 remaining |
| Critical correction groups | 4 completed / 0 remaining |
| Major correction groups | 4 completed / 0 remaining |
| Minor correction groups | 2 completed / 0 remaining |
| Ambiguous questions | 0 |
| Terminology conflicts | 0 |
| Cross-file inconsistencies | 0 |
| Missing translations | 0 |
| Placeholder content | 0 |
| Content drift | 0 |
| Risk level | HIGH |
| Signs coverage | 9 / 9 |
| Questions coverage | 72 / 72 |
| Exams coverage | 9 / 9 |
| Lessons coverage | 0 / 0 |
| Languages coverage | 4 / 4 |

`HIGH` reflects the safety consequence of confusing temporary lane layouts and emergency facilities, not an unresolved finding.

## Critical Findings and Corrections

- F79, F81, F83 and F85: replaced four incorrect meanings with the statutory lane reduction, lane deviation, central-reservation crossover and temporary two-way traffic meanings used at roadworks.
- F95: replaced the false lane-ending/service-exit description with the statutory emergency escape lane meaning.
- F98: replaced the generic lane-control description with the tunnel emergency refuge bay meaning and made the supplementary telephone/extinguisher panel explicit.
- All 72 questions were rebuilt so their correct answers and explanations match the corrected canonical signs in NL, EN, FR and AR.

## Major Findings and Corrections

- F89 and F91: broadened the content from speed-only examples to every lane-specific danger or traffic rule covered by the official text.
- F89: distinguished advance distance from rule duration and preserved its advance-sign role.
- F39: removed unsupported claims that the detour route is merely optional or that a driver may disregard later closure control.
- F79-F85: removed invented fixed zone lengths and documented that arrows and symbols must match the actual worksite.

## Minor Findings and Corrections

- Standardized ten road-management terms across the four languages, including the official French terms `piste de détresse` and `garage F98` with learner-facing clarification.
- Preserved every sign code, category, image path, question ID, difficulty, critical flag and exam manifest. Legacy image filenames remain unchanged because the image bytes are correct and the paths are an active asset contract.

## Deferred and Human Review

None. The federal and regional successor road codes taking effect on 1 June 2027 remain tracked as future law and must be reviewed before that date.

## Governance Validation

```text
Canonical ownership defined: 100%
Content Drift: 0
Cross-file inconsistencies: 0
Missing legal sources: 0
Unverified legal claims: 0
Terminology conflicts: 0
Ambiguous questions: 0
Missing translations: 0
Placeholder content: 0
Direct production edits: 0
Category status: APPROVED
```

## Evidence

- Category data: `src/main/resources/data/content_governance/category-reviews/road-management.json`
- Central index: `src/main/resources/data/content_governance/review-index.json`
- Current source: `BE-KB1975-ROAD-MANAGEMENT-CURRENT`.
- Placement source: `BE-MB1976-ROAD-MANAGEMENT-PLACEMENT`.
- Future-law source: `BE-FPS-ROAD-CODE-2027`.
- Machine-readable consistency, ambiguity, human-approval and review-date reports are stored beside this report.
