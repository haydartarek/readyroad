package com.readyroad.readyroadbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:random_quiz_endpoint;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL",
        "spring.jpa.open-in-view=false"
})
class RandomQuizEndpointTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private Long categoryId;
    private User staffUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        quizQuestionRepository.deleteAll();
        Category category = categoryRepository.findByCode("RANDOM")
                .orElseGet(() -> categoryRepository.saveAndFlush(createCategory()));
        categoryId = category.getId();

        for (int index = 1; index <= 3; index++) {
            quizQuestionRepository.save(createPublishedQuestion(category, index));
        }
        quizQuestionRepository.flush();

        staffUser = userRepository.findByUsername("random-quiz-staff")
                .orElseGet(() -> {
                    User staff = new User();
                    staff.setUsername("random-quiz-staff");
                    staff.setEmail("random-quiz-staff@example.test");
                    staff.setFullName("Random Quiz Staff");
                    staff.setPasswordHash("unused-test-password-hash");
                    staff.setRole(Role.ADMIN);
                    return userRepository.saveAndFlush(staff);
                });
    }

    @Test
    void returnsMappedCategoriesAndOptionsAfterTheServiceTransactionCloses() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/quiz/random").param("count", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].categoryCode")
                        .value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo("RANDOM"))))
                .andExpect(jsonPath("$[*].categoryNameEn")
                        .value(org.hamcrest.Matchers.everyItem(
                                org.hamcrest.Matchers.equalTo("Random quiz category"))))
                .andExpect(jsonPath("$[*].options.length()")
                        .value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo(3))))
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        Set<Long> ids = new HashSet<>();
        response.forEach(question -> ids.add(question.get("id").asLong()));

        assertThat(ids).hasSize(2);
    }

    @Test
    void categoryQuizReturnsMappedCategoryAndOptionsAfterTheServiceTransactionCloses() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/quiz/category/{categoryId}", categoryId)
                        .param("count", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].categoryCode")
                        .value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo("RANDOM"))))
                .andExpect(jsonPath("$[*].categoryNameEn")
                        .value(org.hamcrest.Matchers.everyItem(
                                org.hamcrest.Matchers.equalTo("Random quiz category"))))
                .andExpect(jsonPath("$[*].options.length()")
                        .value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo(3))))
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        Set<Long> ids = new HashSet<>();
        response.forEach(question -> ids.add(question.get("id").asLong()));

        assertThat(ids).hasSize(2);
    }

    @Test
    void theoryExamReturnsMappedCategoriesAndOptionsAfterTheServiceTransactionCloses() throws Exception {
        mockMvc.perform(get("/api/quiz/theory-exam")
                        .with(user(staffUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].categoryCode")
                        .value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo("RANDOM"))))
                .andExpect(jsonPath("$[*].categoryNameEn")
                        .value(org.hamcrest.Matchers.everyItem(
                                org.hamcrest.Matchers.equalTo("Random quiz category"))))
                .andExpect(jsonPath("$[*].options.length()")
                        .value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo(3))));
    }

    @Test
    void theoryExamRejectsAnonymousRequestsEvenInDevelopmentMode() throws Exception {
        mockMvc.perform(get("/api/quiz/theory-exam")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setCode("RANDOM");
        category.setNameAr("Random quiz category AR");
        category.setNameEn("Random quiz category");
        category.setNameNl("Random quiz category NL");
        category.setNameFr("Random quiz category FR");
        category.setDisplayOrder(1);
        category.setIsActive(true);
        return category;
    }

    private QuizQuestion createPublishedQuestion(Category category, int index) {
        QuizQuestion question = new QuizQuestion();
        question.setQuestionAr("Random question AR " + index);
        question.setQuestionEn("Random question EN " + index);
        question.setQuestionNl("Random question NL " + index);
        question.setQuestionFr("Random question FR " + index);
        question.setQuestionType(QuizQuestion.QuestionType.MULTIPLE_CHOICE);
        question.setDifficultyLevel(QuizQuestion.DifficultyLevel.EASY);
        question.setCategory(category);
        question.setStatus(QuizQuestion.QuestionStatus.PUBLISHED);
        question.setIsActive(true);
        question.addOption(createOption("A", true, 1));
        question.addOption(createOption("B", false, 2));
        question.addOption(createOption("C", false, 3));
        return question;
    }

    private QuizAnswerOption createOption(String text, boolean correct, int displayOrder) {
        QuizAnswerOption option = new QuizAnswerOption();
        option.setOptionTextAr("Option AR " + text);
        option.setOptionTextEn("Option EN " + text);
        option.setOptionTextNl("Option NL " + text);
        option.setOptionTextFr("Option FR " + text);
        option.setIsCorrect(correct);
        option.setDisplayOrder(displayOrder);
        return option;
    }
}
