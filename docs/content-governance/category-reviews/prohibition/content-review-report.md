# PROHIBITION Category Content Review

## Status

`APPROVED` on `2026-07-20` after all automated validation gates passed.

The reviewed content applies the current 1975 Road Code through 31 May 2027. The successor code starting on 1 June 2027 is recorded only as a future-law item and is not mixed into current learner answers.

## Scope

- 33 canonical `sign.json` files.
- 264 questions, eight per sign.
- 33 exams with 3 EASY, 3 MEDIUM and 2 HARD questions and a passing score of 6/8.
- 19 governed pages in `les-1`, `les-3`, `les-12`, `les-13`, `les-16`, `les-17` and `les-22`.
- Four languages: NL, EN, FR and AR.

## Quality Metrics

| Metric | Value |
| --- | --- |
| Signs reviewed | 33 |
| Questions reviewed | 264 |
| Exams reviewed | 33 |
| Lessons reviewed | 19 |
| Languages reviewed | 4 |
| Legal sources linked | 3 |
| Terminology standardized | 13 |
| Cross-language consistency | PASSED |
| Future-law items | 1 |
| Human review required | 0 |
| Critical issues | 0 remaining |
| Major issues | 0 remaining |
| Minor issues | 0 remaining |
| Critical correction groups | 5 completed / 0 remaining |
| Major correction groups | 5 completed / 0 remaining |
| Minor correction groups | 3 completed / 0 remaining |
| Ambiguous questions | 0 |
| Terminology conflicts | 0 |
| Cross-file inconsistencies | 0 |
| Missing translations | 0 |
| Placeholder content | 0 |
| Content drift | 0 |
| Risk level | HIGH |
| Signs coverage | 33 / 33 |
| Questions coverage | 264 / 264 |
| Exams coverage | 33 / 33 |
| Lessons coverage | 19 / 19 |
| Languages coverage | 4 / 4 |

`HIGH` describes the consequence of inaccurate prohibition-law content, not an unresolved finding.

## Critical Findings and Corrections

- C1 and C3: distinguished one-direction entry from access prohibited in both directions and restored lawful supplementary-plate exceptions.
- C5, C6 and C23: restored the exact statutory vehicle scopes, including sidecars, construction characteristics and the designed-and-constructed goods criterion.
- C24a-C24c: limited the dangerous-goods scope to categories officially designated by the competent ministers and preserved the ADR tunnel qualifier for C24a.
- C43, C45 and C46: restored every statutory endpoint, prevented C45 from implying a new unrestricted speed and limited C46 to C33, C35, C39 and C43 under the placement rules.
- Linked overtaking lessons: removed invented speed margins, absolute right-overtaking claims and unsupported fixed sanctions.

## Major Findings and Corrections

- C31a/C31b: documented M2, M3, M11 and M12 exceptions and retained the next-junction scope.
- C35/C37 and C39/C41: replaced zone wording with the statutory road-section scope.
- C21/C23 lessons: distinguished actual laden mass from maximum authorised mass.
- C3 error-recovery guidance: replaced unsafe immediate-exit wording and unsupported offence grading with a first-safe-lawful-opportunity rule.
- C1 lessons: aligned exceptions, recovery guidance and sanctions with the current legal hierarchy and removed any claim of a general Belgian points licence.

## Minor Findings and Corrections

- Standardized 13 prohibition-law terms across NL, EN, FR and AR.
- Restored French accents and Arabic orthography where earlier content was mechanically flattened.
- Preserved every image path, question ID, type, difficulty, critical flag and exam manifest.

## Deferred and Human Review

None. The successor code’s future treatment of motorcycles under the overtaking rules remains tracked and must not alter current answers before 1 June 2027.

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

- Category data: `src/main/resources/data/content_governance/category-reviews/prohibition.json`
- Central index: `src/main/resources/data/content_governance/review-index.json`
- Current source: `BE-KB1975-PROHIBITION-CURRENT`.
- Placement source: `BE-MB1976-PROHIBITION-PLACEMENT`.
- Sanctions source: `BE-WEGCODE-OFFENCE-SANCTIONS-2026`.
- Future-law source: `BE-FPS-ROAD-CODE-2027`.
- Machine-readable consistency, ambiguity, human-approval and review-date reports are stored beside this report.
