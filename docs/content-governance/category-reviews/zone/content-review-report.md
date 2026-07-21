# ZONE Category Content Review

## Status

`APPROVED` on `2026-07-21` after all legal, multilingual, consistency, importer and regression gates passed.

The review applies the current 1975 Road Code through 31 May 2027. The successor road codes starting on 1 June 2027 remain a future-law item and are not mixed into current learner answers. Low-emission-zone eligibility remains explicitly regional and time-sensitive.

## Scope

- 22 canonical `sign.json` files.
- 176 questions, eight per sign.
- 22 exams with 3 EASY, 3 MEDIUM and 2 HARD questions and a passing score of 6/8.
- 1 linked lesson page: `les-25/page-2` on speeding and Zone 30 consequences.
- Four languages: NL, EN, FR and AR.

## Quality Metrics

| Metric | Value |
| --- | --- |
| Signs reviewed | 22 |
| Questions reviewed | 176 |
| Exams reviewed | 22 |
| Lessons reviewed | 1 |
| Languages reviewed | 4 |
| Legal sources linked | 6 |
| Terminology standardized | 14 |
| Cross-language consistency | PASSED |
| Future-law items | 1 |
| Human review required | 0 |
| Critical issues | 0 remaining |
| Major issues | 0 remaining |
| Minor issues | 0 remaining |
| Critical correction groups | 7 completed / 0 remaining |
| Major correction groups | 4 completed / 0 remaining |
| Minor correction groups | 2 completed / 0 remaining |
| Ambiguous questions | 0 |
| Terminology conflicts | 0 |
| Cross-file inconsistencies | 0 |
| Missing translations | 0 |
| Placeholder content | 0 |
| Unverified legal claims | 0 |
| Content drift | 0 |
| Risk level | HIGH |
| Signs coverage | 22 / 22 |
| Questions coverage | 176 / 176 |
| Exams coverage | 22 / 22 |
| Lessons coverage | 1 / 1 |
| Languages coverage | 4 / 4 |

`HIGH` reflects the safety and enforcement consequences of access, overtaking, speed, parking, pedestrian-zone, cycle-zone and low-emission-zone rules; it does not indicate an unresolved finding.

## Critical Findings and Corrections

- ZC5: restored the complete C5 scope, including motorcycles with sidecars, in all four languages and questions.
- ZC21: replaced maximum-authorised-mass wording with the statutory actual laden mass threshold of 3,500 kg.
- ZC35: replaced the false blanket overtaking ban with the precise prohibition on overtaking harnessed vehicles or vehicles with more than two wheels on the left.
- ZC43: aligned the content and all questions with the depicted 50 km/h zone rather than a 30 km/h zone.
- ZE9a and ZE9aT: aligned the reserved E9b vehicle categories and corrected `Betalend` to paid parking rather than a time-limit rule.
- F103/F105: restored the complete pedestrian-zone conduct, displayed access conditions, walking pace, pedestrian priority, cyclist dismount duty and parking prohibition.
- F111/F113: replaced obsolete cycle-street claims with the current cycle-zone rules on road position, the 30 km/h maximum and the motor-vehicle overtaking prohibition.

## Major Findings and Corrections

- Every end-of-zone sign now ends only its corresponding zonal rule and does not imply unrestricted access, parking, overtaking or acceleration afterward.
- F117/F118 now direct learners to the competent regional or local official checker instead of hard-coding volatile national eligibility criteria.
- F4a/F4b now describe 30 km/h as a maximum and clarify that F4b does not itself establish the next speed limit.
- All zone signs now distinguish the displayed zonal rule from other local signs, supplementary inscriptions and general traffic rules.

## Minor Findings and Corrections

- Standardized fourteen zone, parking, speed, pedestrian, cycle and low-emission terms across NL, EN, FR and AR.
- Preserved every sign code, category, image path, question ID, difficulty, critical flag and exam manifest. Legacy directory and image names remain unchanged as active data and asset contracts.

## Deferred and Human Review

None. The successor road codes taking effect on 1 June 2027 remain tracked as future law. Dynamic LEZ admission criteria are intentionally not frozen in learner answers and must be checked against the competent official regional or local service.

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

- Category data: `src/main/resources/data/content_governance/category-reviews/zone.json`.
- Central index: `src/main/resources/data/content_governance/review-index.json`.
- Federal sources: `BE-KB1975-ZONE-CURRENT` and `BE-MB1976-ZONE-PLACEMENT`.
- Regional LEZ sources: `BE-FLEMISH-LEZ-CURRENT`, `BE-WALLOON-LEZ-CURRENT` and `BE-BRUSSELS-LEZ-CURRENT`.
- Sanctions source for the linked lesson: `BE-WEGCODE-OFFENCE-SANCTIONS-2026`.
- Future-law source: `BE-FPS-ROAD-CODE-2027`.
- Machine-readable consistency, ambiguity, human-approval and review-date reports are stored beside this report.
