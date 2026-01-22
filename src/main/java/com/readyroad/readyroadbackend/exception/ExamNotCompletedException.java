package com.readyroad.readyroadbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to view results of an incomplete exam.
 *
 * Story A3: View Exam Results - Production Enhancement
 *
 * HTTP Status: 400 BAD REQUEST
 * Use Case: User tries to view results before completing exam
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExamNotCompletedException extends RuntimeException {

    public ExamNotCompletedException(String message) {
        super(message);
    }

    public ExamNotCompletedException(Long examId, String currentStatus) {
        super(String.format(
            "Exam %d is still %s. Complete it first to view results.",
            examId,
            currentStatus
        ));
    }
}
