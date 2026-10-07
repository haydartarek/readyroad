package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.QuizQuestionDTO;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerRequest;
import com.readyroad.readyroadbackend.dto.practice.SubmitPracticeAnswerResponse;
import com.readyroad.readyroadbackend.mapper.QuizQuestionMapper;
import com.readyroad.readyroadbackend.service.TheoryCategoryPracticeService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.Instant;
import com.readyroad.readyroadbackend.dto.response.HomeLessonOverviewResponse.CategorySummary;

/**
 * Paid category-practice API for the theoretical question bank.
 *
 * The category code is an internal routing identifier.
 * User-facing clients must display the localized category name,
 * never the internal THxx code.
 */
@RestController
@RequestMapping("/api/practice/theory/categories")
@RequiredArgsConstructor
public class TheoryCategoryPracticeController {

    private final TheoryCategoryPracticeService theoryCategoryPracticeService;
    private final QuizQuestionMapper quizQuestionMapper;
    private final AuthenticationUtil authenticationUtil;

    @GetMapping("/{categoryCode}/summary")
    public CategorySummary getSummary(@PathVariable String categoryCode,
            @RequestParam(defaultValue = "en") String lang) {
        return theoryCategoryPracticeService.getSummary(categoryCode, lang);
    }

    @GetMapping("/{categoryCode}/exam")
    public ResponseEntity<CategoryExamResponse> getExam(@PathVariable String categoryCode,
            @RequestParam(defaultValue = "en") String lang, Authentication authentication) {
        Long userId = authenticatedUserId(authentication);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<QuizQuestionDTO> questions = quizQuestionMapper.toDTOList(
                theoryCategoryPracticeService.getExamQuestions(userId, categoryCode, lang));
        CategorySummary summary = theoryCategoryPracticeService.getSummary(categoryCode, lang);
        // The running exam counter reflects precisely the delivered question set.
        CategorySummary snapshot = new CategorySummary(summary.categoryCode(), summary.nameNl(),
                summary.nameEn(), summary.nameFr(), summary.nameAr(), questions.size(),
                summary.primary(), summary.displayOrder());
        return ResponseEntity.ok(new CategoryExamResponse(snapshot, questions, Instant.now()));
    }

    public record CategoryExamResponse(CategorySummary category,
            List<QuizQuestionDTO> questions, Instant serverTime) {
    }

    @GetMapping("/{categoryCode}/questions")
    public ResponseEntity<List<QuizQuestionDTO>> getQuestions(
            @PathVariable String categoryCode,
            @RequestParam(defaultValue = "10") int count,
            Authentication authentication) {

        Long userId = authenticatedUserId(authentication);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        List<QuizQuestion> questions =
                theoryCategoryPracticeService.getQuestions(
                        userId,
                        categoryCode,
                        count);

        return ResponseEntity.ok(
                quizQuestionMapper.toDTOList(
                        questions));
    }

    @PostMapping(
            "/{categoryCode}/questions/{questionId}/answer")
    public ResponseEntity<SubmitPracticeAnswerResponse> submitAnswer(
            @PathVariable String categoryCode,
            @PathVariable Long questionId,
            @Valid @RequestBody
            SubmitPracticeAnswerRequest request,
            Authentication authentication) {

        Long userId = authenticatedUserId(authentication);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(
                theoryCategoryPracticeService.submitAnswer(
                        userId,
                        categoryCode,
                        questionId,
                        request));
    }

    private Long authenticatedUserId(Authentication authentication) {
        return authenticationUtil.extractVerifiedUserId(authentication);
    }
}
