# Traffic-Sign Repair Plan

## Confirmed Standard

- Source-first baseline: `src/main/resources/data/signs_import/<SIGN_CODE>/questions.json`, `exams.json`, and `sign.json` are the primary content source.
- DB migrations should only synchronize persisted content with that source.
- Sanitizer/runtime masking should remain only where legacy compatibility is still required after source and DB are aligned.

## Audit Classification

### Keep

- `src/main/resources/data/signs_import/B15d/questions.json` and the rest of the B15 family source folders.
- `src/main/resources/db/migration/V227__Deep_Polish_B15_Priority_Configuration_Family.sql`
- `src/main/resources/db/migration/V228__Deep_Polish_B15fg_Priority_Configuration.sql`
- `src/main/resources/db/migration/V229__Fix_B15b_Q03_Residual_Artifact.sql`
- `src/main/resources/db/migration/V230__Deep_Polish_D1e_D1f_Mandatory_Passage_Signs.sql`
- `src/main/resources/db/migration/V231__Deep_Polish_B23_Cyclists_Go_Straight_At_Red.sql`
- `src/main/resources/db/migration/V232__Deep_Polish_B19_Narrow_Passage_Give_Way.sql`
- `src/main/resources/db/migration/V233__Deep_Polish_B21_Narrow_Passage_Priority.sql`
- `src/main/java/com/readyroad/readyroadbackend/service/RoadSignReferenceTextResolver.java` as a generic compatibility layer. No audited family-specific repair is currently required there.
- Frontend image-manifest references for the audited sign codes. No learner-text mapping repair was found there.

### Revise

- `src/main/java/com/readyroad/readyroadbackend/util/DrivingTextSanitizer.java`
  - Remove the stale B15b-specific learner-text rewrites that are now covered by source + DB alignment.
- `src/test/java/com/readyroad/readyroadbackend/util/DrivingTextSanitizerTest.java`
  - Replace the test that locks in B15b runtime masking with a source-first expectation.

### Revert

- None confirmed in the audited family scope.

## Implementation Checklist

- [x] Remove B15b-specific sanitizer rewrites from `DrivingTextSanitizer`.
- [x] Update `DrivingTextSanitizerTest` to stop asserting B15b runtime masking.
- [x] Run focused tests for sanitizer and resolver behavior. (28/28 pass)
- [ ] Run targeted verification for B15b learner-facing output after the code change.
- [x] Update this checklist with completion state after verification.
