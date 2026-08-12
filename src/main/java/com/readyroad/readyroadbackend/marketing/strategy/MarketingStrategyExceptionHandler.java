package com.readyroad.readyroadbackend.marketing.strategy;

import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = MarketingStrategyAdminController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MarketingStrategyExceptionHandler {

    @ExceptionHandler(BlockedStrategyContextException.class)
    ResponseEntity<StrategyErrorResponse> blocked(BlockedStrategyContextException error) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new StrategyErrorResponse(error.errorCode(), error.getMessage()));
    }

    @ExceptionHandler(MarketingTaskExecutionException.class)
    ResponseEntity<StrategyErrorResponse> invalid(MarketingTaskExecutionException error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new StrategyErrorResponse(error.errorCode(), error.getMessage()));
    }

    record StrategyErrorResponse(String errorCode, String message) {}
}
