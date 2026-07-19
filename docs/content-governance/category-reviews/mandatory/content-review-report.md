# MANDATORY Category Content Review

## Status

`APPROVED` on `2026-07-19`.

The category is closed. Current content follows the 1975 Road Code through 31 May 2027; the official successor-code date is recorded only as a future-law item.

## Scope

- 18 canonical `sign.json` files.
- 144 questions, eight per sign.
- 18 exams with the required 3 EASY, 3 MEDIUM, 2 HARD distribution and passing score 6/8.
- 13 governed lesson pages across `les-1`, `les-7`, `les-20` and `les-23`.
- Four languages: NL, EN, FR and AR.
- No category-specific UI legal copy was found; the frontend consumes canonical API content.

## Quality Metrics

| Metric | Value |
| --- | --- |
| Signs reviewed | 18 |
| Questions reviewed | 144 |
| Exams reviewed | 18 |
| Lessons reviewed | 13 |
| Languages reviewed | 4 |
| Legal sources linked | 3 |
| Terminology standardized | 13 |
| Cross-language consistency | PASSED |
| Future-law items | 1 |
| Human review required | 0 |
| Critical issues | 0 remaining |
| Major issues | 0 remaining |
| Minor issues | 0 remaining |
| Risk level | HIGH |
| Signs coverage | 18 / 18 |
| Questions coverage | 144 / 144 |
| Exams coverage | 18 / 18 |
| Lessons coverage | 13 / 13 |
| Languages coverage | 4 / 4 |

`HIGH` describes the consequence of inaccurate traffic-law content, not an unresolved finding.

## Critical Findings and Corrections

- D5: separated mandatory circular direction from priority; B1/B5 and Article 12 now govern entry scenarios explicitly.
- D7: restored class A moped scope, pedestrian fallback, lawful cycle-path departure and speed-dependent rules.
- D9: removed unconditional speed-pedelec inclusion and restored the separated pedestrian/cycle layout.
- D10: removed the ambiguous generic-moped rule and distinguished its shared pedestrian/cyclist scope from D9.
- D11 and D13: restored the statutory pedestrian order and the rule for a person walking a bicycle.

## Major Findings and Corrections

- Enriched all 18 sign records with current legal meaning, driver duties and narrowly stated exceptions.
- Corrected 17 ambiguous or legally overbroad questions in four languages without changing IDs, types, difficulty or exam manifests.
- Corrected 13 lesson pages, including cyclist priority, roundabout signalling, temporary orange markings and safe missed-turn guidance.
- Recorded the 1 June 2027 change only as future law; no future rule is presented as current.

## Minor Findings and Corrections

- Standardized 13 mandatory-sign terms across NL, EN, FR and AR.
- Replaced vague `should` wording with precise duties where the law imposes an obligation.
- Confirmed that no duplicated mandatory-sign legal explanation is embedded in frontend UI messages.

## Deferred and Human Review

None.

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

- Category data: `src/main/resources/data/content_governance/category-reviews/mandatory.json`
- Central index: `src/main/resources/data/content_governance/review-index.json`
- Source registry: `src/main/resources/data/content_governance/legal-sources.json`
- Terminology glossary: `src/main/resources/data/content_governance/terminology-glossary.json`
- Current legal source: `BE-KB1975-MANDATORY-CURRENT`.
- Placement source: `BE-MB1976-MANDATORY-PLACEMENT`.
- Future-law source: `BE-FPS-ROAD-CODE-2027`.
- Machine-readable consistency, ambiguity, human-approval and review-date reports are stored beside this report.
