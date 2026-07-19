# INFORMATION Category Content Review

## Status

`APPROVED` on `2026-07-19`.

The category is closed. The current 1975 Road Code remains the operational legal source through 31 May 2027; the official 1 June 2027 successor-code date is recorded only as a future-law item.

## Scope

- 37 canonical `sign.json` files.
- 296 questions, eight per sign.
- 37 exams with the required 3 EASY, 3 MEDIUM, 2 HARD distribution and passing score 6/8.
- 0 linked lesson items; the lesson catalog contains no INFORMATION-specific page.
- Four languages: NL, EN, FR, and AR.
- Image paths, legal UI wording, and canonical importer alignment.

## Quality Metrics

| Metric | Value |
| --- | --- |
| Signs reviewed | 37 |
| Questions reviewed | 296 |
| Lessons reviewed | 0 |
| Legal sources linked | 2 |
| Terminology standardized | 14 |
| Cross-language consistency | PASSED |
| Future-law items | 1 |
| Human review required | 0 |
| Risk level | HIGH |
| Signs coverage | 37 / 37 |
| Questions coverage | 296 / 296 |
| Exams coverage | 37 / 37 |
| Lessons coverage | 0 / 0 |
| Languages coverage | 4 / 4 |
| Legal-source coverage | 100% |
| Terminology coverage | 100% |
| Cross-language coverage | 100% |
| Critical issues remaining | 0 |
| Major issues remaining | 0 |
| Minor issues remaining | 0 |

`HIGH` describes the consequence of inaccurate traffic-law content, not an unresolved finding.

## Critical Findings and Corrections

- F5: separated the 70 km/h vehicle-capability access condition from the motorway driving rule and retained the general duty to adapt speed for safety.
- F8: restored the official scope of a tunnel longer than 500 metres and removed an unsupported blanket daytime dipped-headlight claim.
- F45L/F45R: aligned the text and all questions with the actual left/right road-layout symbols instead of inventing pedestrian/cyclist-only passages.
- F50bis variants: separated the pedestrian-crossing warning from the cyclist/two-wheeled-moped turning conflict and removed unconditional-stop wording.
- F97: limited zipper merging to heavily slowed traffic and to the point immediately before the narrowing.
- F99a/F99b/F99c and F101a/F101b/F101c: corrected access, designated-part use, statutory exceptions, 30 km/h limits, full-width use and the legal effect of end signs.

## Major Findings and Corrections

- F7/F11: removed automatic assumptions about speed or access on the following road.
- F12a/F12b: standardized the Belgian `woonerf/erf` concept instead of treating it as an ordinary residential area.
- F13 and F19: aligned lane-direction and one-way explanations with markings and supplementary contra-flow signs.
- F47: clarified that the sign ends the works area but does not automatically cancel every separately signed restriction.
- Standardized current-law and future-law wording so the 2027 code is never presented as effective today.

## Minor Findings and Corrections

- Restored French accents and standardized terminology across NL, EN, FR and AR.
- Replaced repetitive generic explanations with sign-specific educational guidance while preserving question type and difficulty distribution.

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

- Category data: `src/main/resources/data/content_governance/category-reviews/information.json`
- Central index: `src/main/resources/data/content_governance/review-index.json`
- Source registry: `src/main/resources/data/content_governance/legal-sources.json`
- Terminology glossary: `src/main/resources/data/content_governance/terminology-glossary.json`
- Current official source: `BE-KB1975-INFORMATION-CURRENT`.
- Future-law source: `BE-FPS-ROAD-CODE-2027`.
- Machine-readable consistency, ambiguity, human-approval and review-date reports are stored beside this report.
