package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamQuestion;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamQuestionRepository;
import com.readyroad.readyroadbackend.dto.AdminExamQuestionRequest;
import com.readyroad.readyroadbackend.dto.response.AdminExamQuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final CategoryRepository categoryRepository;

    public ExamQuestionService(ExamQuestionRepository examQuestionRepository,
            CategoryRepository categoryRepository) {
        this.examQuestionRepository = examQuestionRepository;
        this.categoryRepository = categoryRepository;
    }

    // ── Read ─────────────────────────────────────────────

    public List<ExamQuestion> getAllExamQuestions() {
        return examQuestionRepository.findByIsActiveTrue();
    }

    public Optional<ExamQuestion> getQuestionById(Long id) {
        return examQuestionRepository.findById(id);
    }

    public List<ExamQuestion> getRandomQuestions(int limit) {
        return examQuestionRepository.findRandomQuestions()
                .stream().limit(limit).toList();
    }

    public List<ExamQuestion> getRandomQuestionsByCategory(Long categoryId, int limit) {
        return examQuestionRepository.findRandomQuestionsByCategory(categoryId)
                .stream().limit(limit).toList();
    }

    public Long getTotalQuestionsCount() {
        return examQuestionRepository.countByIsActiveTrue();
    }

    public Long getQuestionsCountByCategory(Long categoryId) {
        return examQuestionRepository.countByCategoryIdAndIsActiveTrue(categoryId);
    }

    /** Paginated list for the admin panel. */
    public Page<ExamQuestion> getQuestionsPaginated(Pageable pageable) {
        return examQuestionRepository.findAll(pageable);
    }

    /** Single question for admin edit form. */
    public AdminExamQuestionResponse getAdminQuestionById(Long id) {
        ExamQuestion q = examQuestionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam question not found"));
        return toResponse(q);
    }

    // ── Write ────────────────────────────────────────────

    @Transactional
    public AdminExamQuestionResponse createQuestion(AdminExamQuestionRequest req) {
        Category category = resolveCategory(req.getCategoryCode());
        ExamQuestion q = new ExamQuestion();
        mapFields(req, q, category);
        return toResponse(examQuestionRepository.save(q));
    }

    @Transactional
    public AdminExamQuestionResponse updateQuestion(Long id, AdminExamQuestionRequest req) {
        ExamQuestion q = examQuestionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam question not found"));
        Category category = resolveCategory(req.getCategoryCode());
        mapFields(req, q, category);
        return toResponse(examQuestionRepository.save(q));
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!examQuestionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam question not found");
        }
        examQuestionRepository.deleteById(id);
    }

    // ── Helpers ──────────────────────────────────────────

    private Category resolveCategory(String code) {
        return categoryRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Category not found: " + code));
    }

    private void mapFields(AdminExamQuestionRequest req, ExamQuestion q, Category category) {
        q.setCategory(category);
        q.setQuestionEn(req.getQuestionEn());
        q.setQuestionAr(nullOrBlank(req.getQuestionAr()) ? req.getQuestionEn() : req.getQuestionAr());
        q.setQuestionNl(nullOrBlank(req.getQuestionNl()) ? req.getQuestionEn() : req.getQuestionNl());
        q.setQuestionFr(nullOrBlank(req.getQuestionFr()) ? req.getQuestionEn() : req.getQuestionFr());

        q.setOption1En(req.getOption1En());
        q.setOption1Ar(nullOrBlank(req.getOption1Ar()) ? req.getOption1En() : req.getOption1Ar());
        q.setOption1Nl(nullOrBlank(req.getOption1Nl()) ? req.getOption1En() : req.getOption1Nl());
        q.setOption1Fr(nullOrBlank(req.getOption1Fr()) ? req.getOption1En() : req.getOption1Fr());

        q.setOption2En(req.getOption2En());
        q.setOption2Ar(nullOrBlank(req.getOption2Ar()) ? req.getOption2En() : req.getOption2Ar());
        q.setOption2Nl(nullOrBlank(req.getOption2Nl()) ? req.getOption2En() : req.getOption2Nl());
        q.setOption2Fr(nullOrBlank(req.getOption2Fr()) ? req.getOption2En() : req.getOption2Fr());

        // option3 is optional (2 or 3 choices — Belgian standard). null means absent.
        if (nullOrBlank(req.getOption3En())) {
            q.setOption3En(null);
            q.setOption3Ar(null);
            q.setOption3Nl(null);
            q.setOption3Fr(null);
        } else {
            q.setOption3En(req.getOption3En());
            q.setOption3Ar(nullOrBlank(req.getOption3Ar()) ? req.getOption3En() : req.getOption3Ar());
            q.setOption3Nl(nullOrBlank(req.getOption3Nl()) ? req.getOption3En() : req.getOption3Nl());
            q.setOption3Fr(nullOrBlank(req.getOption3Fr()) ? req.getOption3En() : req.getOption3Fr());
        }

        // option4 is never used in admin CRUD — always null (legacy data may have
        // values)
        q.setOption4En(null);
        q.setOption4Ar(null);
        q.setOption4Nl(null);
        q.setOption4Fr(null);

        q.setCorrectAnswer(req.getCorrectAnswer());

        q.setExplanationEn(req.getExplanationEn());
        q.setExplanationAr(req.getExplanationAr());
        q.setExplanationNl(req.getExplanationNl());
        q.setExplanationFr(req.getExplanationFr());

        q.setImageUrl(req.getImageUrl());

        if (req.getDifficulty() != null) {
            try {
                q.setDifficulty(ExamQuestion.DifficultyLevel.valueOf(req.getDifficulty().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                q.setDifficulty(ExamQuestion.DifficultyLevel.MEDIUM);
            }
        }
        if (req.getIsImportant() != null)
            q.setIsImportant(req.getIsImportant());
        if (req.getIsActive() != null)
            q.setIsActive(req.getIsActive());
    }

    private boolean nullOrBlank(String s) {
        return s == null || s.isBlank();
    }

    public AdminExamQuestionResponse toResponse(ExamQuestion q) {
        Category c = q.getCategory();
        return new AdminExamQuestionResponse(
                q.getId(),
                c.getCode(),
                c.getNameEn(),
                c.getNameAr(),
                c.getNameNl(),
                c.getNameFr(),
                q.getQuestionEn(), q.getQuestionAr(), q.getQuestionNl(), q.getQuestionFr(),
                q.getOption1En(), q.getOption1Ar(), q.getOption1Nl(), q.getOption1Fr(),
                q.getOption2En(), q.getOption2Ar(), q.getOption2Nl(), q.getOption2Fr(),
                q.getOption3En(), q.getOption3Ar(), q.getOption3Nl(), q.getOption3Fr(),
                q.getOption4En(), q.getOption4Ar(), q.getOption4Nl(), q.getOption4Fr(),
                q.getCorrectAnswer(),
                q.getExplanationEn(), q.getExplanationAr(), q.getExplanationNl(), q.getExplanationFr(),
                q.getImageUrl(),
                q.getDifficulty() != null ? q.getDifficulty().name() : "MEDIUM",
                q.getIsImportant(),
                q.getIsActive(),
                q.getCreatedAt(),
                q.getUpdatedAt());
    }
}
