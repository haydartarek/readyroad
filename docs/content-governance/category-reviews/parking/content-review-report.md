# PARKING Category Content Review

## Status

`APPROVED` on `2026-07-19`.

The category is closed. Current content follows the 1975 Road Code through 31 May 2027; the official successor-code date is recorded only as a future-law item.

## Scope

- 15 canonical `sign.json` files.
- 120 questions, eight per sign.
- 15 exams with the required 3 EASY, 3 MEDIUM, 2 HARD distribution and passing score 6/8.
- 4 governed pages in `les-4`.
- Four languages: NL, EN, FR and AR.
- No category-specific frontend legal copy was found; runtime pages consume canonical API and lesson content.

## Quality Metrics

| Metric | Value |
| --- | --- |
| Signs reviewed | 15 |
| Questions reviewed | 120 |
| Exams reviewed | 15 |
| Lessons reviewed | 4 |
| Languages reviewed | 4 |
| Legal sources linked | 4 |
| Terminology standardized | 16 |
| Cross-language consistency | PASSED |
| Future-law items | 1 |
| Human review required | 0 |
| Critical issues | 0 remaining |
| Major issues | 0 remaining |
| Minor issues | 0 remaining |
| Risk level | HIGH |
| Signs coverage | 15 / 15 |
| Questions coverage | 120 / 120 |
| Exams coverage | 15 / 15 |
| Lessons coverage | 4 / 4 |
| Languages coverage | 4 / 4 |

`HIGH` describes the consequence of inaccurate parking-law content, not an unresolved finding.

## Critical Findings and Corrections

- E1 and `les-4/page-1`: replaced the false driver-presence and fixed-duration test with the statutory purpose-and-necessary-duration definition.
- E3: separated prohibited voluntary stopping from forced immobilisation caused by traffic, a binding order, breakdown or emergency.
- E9a: removed claims that the sign alone guarantees free, unlimited or condition-free parking.
- Electric parking: restored the statutory electric-or-hybrid scope and mandatory connection at public charging infrastructure.
- Disability parking: corrected the displayed card elements to the wheelchair symbol and unique card number and restored municipal payment and resident-space conditions.
- E9j: replaced invented supplementary schedules with the fixed statutory 07:30–18:00 bicycle and 18:00–07:30 motor-vehicle periods.

## Major Findings and Corrections

- Corrected 57 ambiguous or legally overbroad questions in all four languages without changing IDs, types, difficulties or exam manifests.
- Corrected E9b, E9c and E9d vehicle-category wording and removed unsupported logistics-purpose claims.
- Restored the 1.50-metre pedestrian rule for applicable verge and marked pavement parking and the 3-metre free-passage rule on the carriageway.
- Rewrote all four pages of the linked parking lesson and removed unsupported hazard-light, hydrant and 60-second rules.

## Minor Findings and Corrections

- Standardized 16 parking-law terms across NL, EN, FR and AR.
- Standardized Arabic `الركن` for parking and `التوقف` for stopping where legal distinction matters.
- Preserved all image paths, question counts, difficulty distributions and exam manifests.

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

- Category data: `src/main/resources/data/content_governance/category-reviews/parking.json`
- Central index: `src/main/resources/data/content_governance/review-index.json`
- Source registry: `src/main/resources/data/content_governance/legal-sources.json`
- Terminology glossary: `src/main/resources/data/content_governance/terminology-glossary.json`
- Current legal source: `BE-KB1975-PARKING-CURRENT`.
- Placement source: `BE-MB1976-PARKING-PLACEMENT`.
- Disability-card source: `BE-FPS-DISABILITY-PARKING-CARD-2026`.
- Future-law source: `BE-FPS-ROAD-CODE-2027`.
- Machine-readable consistency, ambiguity, human-approval and review-date reports are stored beside this report.
