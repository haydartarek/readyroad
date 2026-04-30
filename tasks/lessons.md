# Lessons

- For traffic-sign content, always start from `signs_import/<SIGN_CODE>/`: inspect `questions.json`, `exams.json`, and `sign.json`; fix source first; add a DB migration only when persisted content must be synchronized; touch sanitizer/runtime masking only for necessary legacy compatibility after source and DB are aligned.
- When a family has already been corrected in source and persisted via migration, remove any leftover sign-specific runtime masking that only exists to compensate for pre-fix content.
