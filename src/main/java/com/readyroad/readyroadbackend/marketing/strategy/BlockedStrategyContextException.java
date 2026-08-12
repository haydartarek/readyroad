package com.readyroad.readyroadbackend.marketing.strategy;

import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;

public class BlockedStrategyContextException extends MarketingTaskExecutionException {

    public static final String ERROR_CODE = "BLOCKED_STRATEGY_CONTEXT";

    public BlockedStrategyContextException(String missingContext) {
        super(ERROR_CODE, "Required strategy context is missing or inactive: " + missingContext);
    }
}
