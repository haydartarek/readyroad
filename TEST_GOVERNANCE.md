# Test Governance - Official Rules (Hard Constraints)

**Status**: ENFORCED  
**Effective Date**: January 21, 2026  
**Authority**: Project Owner Directive

---

## Rule 1: Profile Separation (MANDATORY)

### ✅ REQUIRED Configuration

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")  // ONLY "test" profile
@TestPropertySource(properties = "spring.security.mode=secure")  // If JWT needed
class AuthenticationIntegrationTest {
    // test code
}
```

### ❌ FORBIDDEN Configuration

```java
// ❌ NEVER DO THIS:
@ActiveProfiles({"test", "secure"})  // WRONG: Loads MySQL + validate

// ❌ NEVER DO THIS:
@ActiveProfiles("secure")  // WRONG: Bypasses H2 test database
```

---

## Rule 2: Database Configuration (MANDATORY)

### Test Profile Must Use H2

**File**: `src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
  
  jpa:
    hibernate:
      ddl-auto: create-drop  # Auto-create schema from entities
  
  flyway:
    enabled: false  # Migrations are MySQL-specific
```

### Why This Matters

1. **H2 creates schema automatically** from JPA entities
2. **No dependency on MySQL migrations** being applied
3. **Tests run fast** (in-memory database)
4. **Tests are deterministic** (clean slate every run)
5. **New entity fields work immediately** without migration

---

## Rule 3: Security Mode Control (MANDATORY)

### Enabling JWT Security in Tests

**Method 1**: Per-Test Override (Recommended)
```java
@TestPropertySource(properties = "spring.security.mode=secure")
```

**Method 2**: Test-Specific Profile
```yaml
# application-test-secure.yml
spring:
  security:
    mode: secure
```

Then use:
```java
@ActiveProfiles({"test", "test-secure"})  // OK: Both are test profiles
```

### Default Test Behavior

```yaml
# application-test.yml
spring:
  security:
    mode: dev  # Default: No JWT required for most tests
```

---

## Rule 4: Entity Validation Separation (MANDATORY)

### ✅ CORRECT: Service-Layer Validation

**File**: `QuestionPublishService.java`

```java
@Service
public class QuestionPublishService {
    
    @Transactional
    public void publishQuestion(Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
            .orElseThrow(...);
        
        // Manual validation for publish-specific rules
        List<String> errors = new ArrayList<>();
        
        if (question.getQuestionNl() == null || question.getQuestionNl().isBlank()) {
            errors.add("NL translation required for Belgian compliance");
        }
        
        if (question.getQuestionFr() == null || question.getQuestionFr().isBlank()) {
            errors.add("FR translation required for Belgian compliance");
        }
        
        // Validation group for publish-time checks
        Set<ConstraintViolation<QuizQuestion>> violations = 
            validator.validate(question, PublishValidation.class);
        
        if (!violations.isEmpty() || !errors.isEmpty()) {
            throw new BelgianComplianceException(...);
        }
        
        question.setStatus(QuestionStatus.PUBLISHED);
        questionRepository.save(question);
    }
}
```

### ❌ FORBIDDEN: Default-Group Validation on Entities

```java
// ❌ NEVER DO THIS:
@Entity
public class QuizQuestion {
    
    @NotBlank  // WRONG: Blocks non-publishing use cases
    @Column(nullable = false)
    private String questionNl;
    
    @NotBlank  // WRONG: Causes JPA hydration errors
    @Column(nullable = false)
    private String questionFr;
}
```

### Why This Matters

1. **DRAFT questions can exist** without NL/FR
2. **JPA can load entities** without triggering validation
3. **Validation happens only at publish time** (business rule)
4. **Entity persistence != business validation**

---

## Rule 5: Migration Independence (MANDATORY)

### Problem: Tests Break When Migrations Add Columns

**Example**: Migration V37 adds `published_at` column

```sql
-- V37__Add_Question_Publish_Workflow.sql
ALTER TABLE quiz_questions 
ADD COLUMN published_at TIMESTAMP NULL;
```

**What Happens**:
- ✅ **H2 (test profile)**: Hibernate creates column automatically → Tests pass
- ❌ **MySQL (secure profile)**: `ddl-auto: validate` expects column exists → Tests fail

### Solution: Never Mix Profiles

```java
// ✅ CORRECT:
@ActiveProfiles("test")  // H2 creates schema
@TestPropertySource(properties = "spring.security.mode=secure")  // JWT enabled

// ❌ WRONG:
@ActiveProfiles({"test", "secure"})  // MySQL expects migrations applied
```

---

## Rule 6: Enforcement Mechanisms

### Pre-Commit Hook (Recommended)

```bash
#!/bin/bash
# .git/hooks/pre-commit

# Check for forbidden patterns
if git diff --cached | grep -q '@ActiveProfiles.*secure'; then
    echo "❌ ERROR: Tests must not use 'secure' profile"
    echo "Use @ActiveProfiles(\"test\") with @TestPropertySource instead"
    exit 1
fi
```

### Maven Enforcer Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <executions>
        <execution>
            <id>enforce-test-profile</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <bannedProfiles>
                        <message>Tests must not activate 'secure' profile</message>
                        <excludedProfiles>
                            <excludedProfile>secure</excludedProfile>
                        </excludedProfiles>
                    </bannedProfiles>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## Compliance Checklist

Before merging any test code, verify:

- [ ] Uses `@ActiveProfiles("test")` ONLY
- [ ] Uses `@TestPropertySource` for security mode if needed
- [ ] Does NOT reference "secure" profile
- [ ] Does NOT reference "dev" MySQL profile
- [ ] Does NOT add `@NotBlank` to entity fields without groups
- [ ] Publish validations are in service layer, not entity layer

---

## Common Mistakes & Solutions

### Mistake 1: "My test needs JWT security"

❌ **Wrong**: `@ActiveProfiles({"test", "secure"})`  
✅ **Right**: `@ActiveProfiles("test")` + `@TestPropertySource(properties = "spring.security.mode=secure")`

### Mistake 2: "Test fails with 'column not found'"

**Cause**: Using "secure" profile loads MySQL with `ddl-auto: validate`  
**Solution**: Remove "secure" from `@ActiveProfiles`, use "test" only

### Mistake 3: "Entity validation blocks persistence"

**Cause**: `@NotBlank` without validation groups  
**Solution**: Remove annotation, add validation to service layer

### Mistake 4: "How do I test production security behavior?"

**Answer**: Production behavior = JWT required  
**Solution**: Set `spring.security.mode=secure` via `@TestPropertySource`

---

## Authority & Escalation

**This is a HARD RULE, not a suggestion.**

- Any PR violating these rules will be **rejected**
- Any test using `@ActiveProfiles.*secure` will **fail CI**
- Exceptions require **explicit project owner approval**

**Questions?** See PROJECT_OWNER before deviating from these rules.

---

## Version History

| Version | Date | Change |
|---------|------|--------|
| 1.0 | 2026-01-21 | Initial governance established |

---

**END OF DOCUMENT**
