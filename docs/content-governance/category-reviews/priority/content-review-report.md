# PRIORITY Category Content Review

## Status

`APPROVED` on `2026-07-20` after all automated validation gates passed.

The reviewed content applies the current 1975 Road Code through 31 May 2027. The successor code starting on 1 June 2027 is recorded only as a future-law item and is not mixed into current learner answers.

## Scope

- 16 canonical `sign.json` files.
- 128 questions, eight per sign.
- 16 exams with 3 EASY, 3 MEDIUM and 2 HARD questions and a passing score of 6/8.
- 28 governed pages in `les-2`, `les-15`, `les-19` and `les-21`.
- Four languages: NL, EN, FR and AR.
- No independent frontend priority-law catalog was found; runtime sign pages consume canonical API data.

## Quality Metrics

| Metric | Value |
| --- | --- |
| Signs reviewed | 16 |
| Questions reviewed | 128 |
| Exams reviewed | 16 |
| Lessons reviewed | 28 |
| Languages reviewed | 4 |
| Legal sources linked | 2 |
| Terminology standardized | 9 |
| Cross-language consistency | PASSED |
| Future-law items | 1 |
| Human review required | 0 |
| Critical issues | 0 remaining |
| Major issues | 0 remaining |
| Minor issues | 0 remaining |
| Risk level | HIGH |
| Signs coverage | 16 / 16 |
| Questions coverage | 128 / 128 |
| Exams coverage | 16 / 16 |
| Lessons coverage | 28 / 28 |
| Languages coverage | 4 / 4 |

`HIGH` describes the consequence of inaccurate priority-law content, not an unresolved finding.

## Critical Findings and Corrections

- B17 and the right-priority lessons: restored the statutory roundabout and prohibited-direction exceptions and the separate duty to give way to rail vehicles.
- B22 and B23: restored the red-or-amber scope, the exact duties to give way and the B23 placement condition that no traffic flow is crossed.
- B11: removed the impossible unmarked-junction scenario and documented that B1, B5 or B17 always announces the rule where the priority road ends.
- `les-15`: removed the invented general uphill-priority rule and distinguished passing oncoming traffic from overtaking-distance rules.
- `les-21`: replaced the false general tram-overtaking ban and unrestricted bus priority with the exact rail-vehicle, built-up-area bus-departure, stop-parking and passenger-protection duties.

## Major Findings and Corrections

- B15a-B15g: limited each sign to the announced next junction and removed unsupported claims of continuing priority.
- B1 and B5: aligned the duty with drivers on the public road or carriageway being entered and made a stop line conditional on its presence.
- B9: replaced the ambiguous pedestrian-waiting scenario with the statutory marked-crossing threshold.
- B19 and B21: aligned the paired narrow-passage rule and removed the unsupported claim that one specific driver must always reverse after a blockage.
- Reworked the linked priority lessons to follow the legal hierarchy of authorized-person orders, working lights, signs and general rules.

## Minor Findings and Corrections

- Standardized nine priority-law terms across NL, EN, FR and AR.
- Replaced translated absolutes such as "always", "all traffic" and "green traffic only" where the current code is narrower.
- Preserved every image path, question ID, type, difficulty, critical flag and exam manifest.

## Deferred and Human Review

None. B23's treatment in the successor code remains tracked as future law and must not alter current answers before 1 June 2027.

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

- Category data: `src/main/resources/data/content_governance/category-reviews/priority.json`
- Central index: `src/main/resources/data/content_governance/review-index.json`
- Current source: `BE-KB1975-PRIORITY-CURRENT`.
- Placement source: `BE-MB1976-PRIORITY-PLACEMENT`.
- Future-law source: `BE-FPS-ROAD-CODE-2027`.
- Machine-readable consistency, ambiguity, human-approval and review-date reports are stored beside this report.
