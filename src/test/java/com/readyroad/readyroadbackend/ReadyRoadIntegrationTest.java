package com.readyroad.readyroadbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Comprehensive Integration Test Suite for ReadyRoad Backend
 *
 * **Purpose:** Tests application context loading and bean availability ONLY.
 *
 * **What This Test DOES:**
 * - ✅ Verifies Spring Boot application context starts
 * - ✅ Validates all required beans (repositories, services, controllers)
 * - ✅ Confirms database schema creation (H2 in-memory)
 * - ✅ Tests JPA entity mappings
 *
 * **What This Test DOES NOT:**
 * - ❌ Does NOT test JWT authentication (profile "secure" not active)
 * - ❌ Does NOT test endpoint security (no MockMvc calls)
 * - ❌ Does NOT test HTTP requests/responses
 * - ❌ Does NOT validate authorization rules
 *
 * **Security Testing:**
 * For JWT authentication and endpoint security testing, see:
 * {@link com.readyroad.readyroadbackend.integration.AuthenticationIntegrationTest}
 *
 * **Profile:** "test" only (no "secure")
 * **Database:** H2 in-memory (MySQL compatibility mode)
 * **Flyway:** Disabled (schema created by Hibernate)
 *
 * @author ReadyRoad Team
 * @since 2026-01-17
 */
@SpringBootTest
@ActiveProfiles("test")  // ⚠️ NOTE: "secure" profile NOT active - no JWT security here
@DisplayName("ReadyRoad Backend - Application Context Tests")
public class ReadyRoadIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    // ==================== CONTEXT AND BEAN TESTS ====================

    @Test
    @DisplayName("Context Test: Application context should load successfully")
    public void testContextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Bean Test: CategoryRepository should be available")
    public void testCategoryRepositoryBean() {
        assertThat(applicationContext.containsBean("categoryRepository")).isTrue();
    }

    @Test
    @DisplayName("Bean Test: TrafficSignRepository should be available")
    public void testTrafficSignRepositoryBean() {
        assertThat(applicationContext.containsBean("trafficSignRepository")).isTrue();
    }

    @Test
    @DisplayName("Bean Test: LessonRepository should be available")
    public void testLessonRepositoryBean() {
        assertThat(applicationContext.containsBean("lessonRepository")).isTrue();
    }

    @Test
    @DisplayName("Bean Test: ExamQuestionRepository should be available")
    public void testExamQuestionRepositoryBean() {
        assertThat(applicationContext.containsBean("examQuestionRepository")).isTrue();
    }

    @Test
    @DisplayName("Bean Test: PracticeQuestionRepository should be available")
    public void testPracticeQuestionRepositoryBean() {
        assertThat(applicationContext.containsBean("practiceQuestionRepository")).isTrue();
    }

    @Test
    @DisplayName("Service Test: CategoryService should be available")
    public void testCategoryServiceBean() {
        assertThat(applicationContext.containsBean("categoryService")).isTrue();
    }

    @Test
    @DisplayName("Service Test: TrafficSignService should be available")
    public void testTrafficSignServiceBean() {
        assertThat(applicationContext.containsBean("trafficSignService")).isTrue();
    }

    @Test
    @DisplayName("Service Test: LessonService should be available")
    public void testLessonServiceBean() {
        assertThat(applicationContext.containsBean("lessonService")).isTrue();
    }

    @Test
    @DisplayName("Service Test: ExamQuestionService should be available")
    public void testExamQuestionServiceBean() {
        assertThat(applicationContext.containsBean("examQuestionService")).isTrue();
    }

    @Test
    @DisplayName("Service Test: PracticeQuestionService should be available")
    public void testPracticeQuestionServiceBean() {
        assertThat(applicationContext.containsBean("practiceQuestionService")).isTrue();
    }

    @Test
    @DisplayName("Controller Test: CategoryController should be available")
    public void testCategoryControllerBean() {
        assertThat(applicationContext.containsBean("categoryController")).isTrue();
    }

    @Test
    @DisplayName("Controller Test: TrafficSignController should be available")
    public void testTrafficSignControllerBean() {
        assertThat(applicationContext.containsBean("trafficSignController")).isTrue();
    }

    @Test
    @DisplayName("Controller Test: LessonController should be available")
    public void testLessonControllerBean() {
        assertThat(applicationContext.containsBean("lessonController")).isTrue();
    }
}
