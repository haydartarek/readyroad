# ReadyRoad Supabase Access Model

Last verified: 2026-07-28

## Decision

ReadyRoad uses Spring Boot as the only application data path. The web and
mobile clients call the Spring Boot API and contain no Supabase URL, anon key,
service-role key, PostgREST call, GraphQL call, RPC call, or Storage policy
dependency.

Spring Boot connects through PostgreSQL JDBC as `readyroad_app` (the pooler
username includes the project suffix). That role owns the objects in the
`readyroad` schema. No Supabase API key is used by the backend.

The selected model is therefore:

- Direct client database access: not required.
- `anon`, `authenticated`, and `service_role` schema usage: denied.
- Table and sequence grants to those API roles: none.
- RLS: enabled on 41 application tables as defense in depth, without `FORCE
  ROW LEVEL SECURITY`.
- `flyway_schema_history`: protected by owner-only access and zero API-role
  grants. It is deliberately excluded from RLS because changing that table
  inside a Flyway migration conflicts with Flyway's own migration-history lock.
- RLS policies: none, intentionally. Clients have no legitimate direct data
  use case.
- Backend access: preserved through the table-owning `readyroad_app` role.

## Verification Baseline

- Base tables: 42.
- Views: 2 (`user_quiz_stats`, `weak_areas_summary`).
- Table owner: `readyroad_app`.
- Tables with API-role grants before hardening: 0.
- Tables with RLS before hardening: 0.
- Existing RLS policies: 0.
- `anon` schema usage: denied.
- `authenticated` schema usage: denied.
- `service_role` schema usage: denied.
- Browser Supabase references: 0.
- Mobile Supabase references: 0.
- Backend health baseline: HTTP 200.
- Encrypted pre-change backup:
  `readyroad-db-20260728-130415.dump.age`.

## Table Inventory

All rows use the same access decision: direct client access is **No**, current
API grants are **None**, policies are **None**, backend access is **Owner**, and
the decision is **Enable RLS and keep API roles revoked**.

| Table | Purpose | User data | Risk |
|---|---|---:|---|
| achievements | Earned user achievements | Yes | High |
| admin_system_settings | Administrative runtime settings | No | High |
| auth_identities | OAuth identities | Yes | Critical |
| categories | Learning category reference data | No | Low |
| dev_exam_categories | Developer exam reference categories | No | Low |
| dev_exam_category_i18n | Developer exam translations | No | Low |
| dev_exam_choices | Developer exam choices | No | Low |
| dev_exam_questions | Developer exam questions | No | Low |
| dev_exam_settings | Developer exam settings | No | Medium |
| exam_questions | Official exam question references | No | Medium |
| exam_simulation_answers | User official-exam answers | Yes | Critical |
| exam_simulation_questions | Questions assigned to user exams | Yes | High |
| exam_simulations | User official-exam sessions/results | Yes | Critical |
| flyway_schema_history | Migration integrity history; owner-only grant protection | No | Critical |
| import_history | Administrative import audit | No | High |
| lesson_pages | Canonical lesson pages | No | Medium |
| lessons | Canonical lessons | No | Medium |
| notifications | User notifications | Yes | High |
| password_reset_tokens | Password reset credentials | Yes | Critical |
| quiz_answer_options | General quiz answer reference data | No | Low |
| quiz_attempts | User quiz attempts | Yes | High |
| quiz_questions | General quiz question reference data | No | Medium |
| quiz_user_answers | User quiz answers | Yes | Critical |
| road_signs | Canonical traffic signs | No | Medium |
| sign_choices | Sign-question answer reference data | No | Low |
| sign_exam_questions | Questions assigned to sign exams | Yes | High |
| sign_exam_results | User sign-exam results | Yes | Critical |
| sign_exams | Sign exam definitions | No | Medium |
| sign_import_runs | Sign importer audit | No | High |
| sign_practice_answers | User sign-practice answers | Yes | Critical |
| sign_practice_sessions | User sign-practice sessions | Yes | Critical |
| sign_questions | Sign question reference data | No | Medium |
| sign_random_practice_questions | Random questions assigned to users | Yes | High |
| sign_random_practice_sessions | User random-practice sessions | Yes | Critical |
| traffic_rules | Canonical traffic-rule content | No | Medium |
| user_category_progress | User category aggregates | Yes | Critical |
| user_error_patterns | User learning analytics | Yes | Critical |
| user_lesson_progress | User lesson progress | Yes | Critical |
| user_question_history | User answer history and timing | Yes | Critical |
| user_roles | Role reference data | No | High |
| user_weak_areas | User weak-area analytics | Yes | Critical |
| users | Accounts, roles, and preferences | Yes | Critical |

## Views And Other Objects

- `user_quiz_stats` contains user identity and aggregate quiz data.
- `weak_areas_summary` contains user learning analytics.
- Both views inherit the schema/table grant denial and are unavailable to API
  roles.
- No client RPC or Storage use case was found.
- The effective `readyroad` Data API exposure is zero because API roles have
  neither schema usage nor object grants, regardless of the dashboard's exposed
  schema list.

## Dry Run

The production SQL was executed inside a transaction and rolled back.

- RLS tables inside dry run: 41.
- Grant-protected Flyway history tables: 1.
- `FORCE RLS`: disabled on all protected tables.
- `readyroad_app` SELECT/UPDATE on `users`: allowed.
- `anon` SELECT on `users`: denied.
- `authenticated` SELECT on `users`: denied.
- `service_role` SELECT on `users`: denied.

## Rollback

The matching rollback is
`docs/infrastructure/supabase-rls-rollback.sql`. The pre-change API-role grants
were already empty, so rollback only disables RLS. It does not grant public
access or modify production data.
