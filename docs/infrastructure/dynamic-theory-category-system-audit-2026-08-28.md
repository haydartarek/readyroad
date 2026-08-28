# Dynamic Theory Category System Audit

Date: 2026-08-28

## Contract corrections

- Categories with five or fewer eligible questions are excluded; eligibility
  begins at six questions.
- The eight-hour user cooldown is a hard rule. No fallback query can reinsert a
  cooling question.
- If fewer than 50 questions remain after hard rules, the existing controlled
  unavailable result reports the actual eligible capacity.
- Bank category eligibility remains separate from per-user availability, so a
  user-specific shortage relaxes and redistributes quota without deleting the
  category from the bank blueprint.

## Verified production data snapshot

- Production contains 218 active theory questions.
- 216 are source-linked; questions 219 and 220 are valid text-only questions.
- Required-field failures: 0.

## Explicitly unresolved decision

The implementation still applies `DEFAULT_CATEGORY_WEIGHT = 10` when no exam
weight is configured. V2 does not explicitly define the future-category default,
so this audit does not invent or change that rule.

## Targeted validation

- Allocator, bank-health, and category-service unit tests: 21 passed.
- PostgreSQL/Testcontainers cooldown tests: 8 passed.
- Admin category and bank-health frontend tests: 6 passed.
- Seeded allocator simulation: 500 runs with zero duplicate or cooldown
  violations.
