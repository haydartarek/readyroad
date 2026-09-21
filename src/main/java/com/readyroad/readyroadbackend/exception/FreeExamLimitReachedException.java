package com.readyroad.readyroadbackend.exception;

/**
 * Raised when a non-entitled learner attempts to access a theory
 * exam question beyond the free preview boundary.
 *
 * The persisted exam itself remains IN_PROGRESS.
 */
public class FreeExamLimitReachedException extends RuntimeException {

    public static final String ERROR_CODE = "FREE_LIMIT_REACHED";

    private final Long examId;
    private final Long questionId;

    public FreeExamLimitReachedException(Long examId, Long questionId) {
        super("Free theory exam preview limit reached");
        this.examId = examId;
        this.questionId = questionId;
    }

    public Long getExamId() {
        return examId;
    }

    public Long getQuestionId() {
        return questionId;
    }
}