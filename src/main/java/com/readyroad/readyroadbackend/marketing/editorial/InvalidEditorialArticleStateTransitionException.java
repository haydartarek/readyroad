package com.readyroad.readyroadbackend.marketing.editorial;

final class InvalidEditorialArticleStateTransitionException extends IllegalStateException {

    InvalidEditorialArticleStateTransitionException(
            EditorialArticleState from,
            EditorialArticleState to) {
        super("Invalid editorial article state transition: " + from + " -> " + to);
    }

    InvalidEditorialArticleStateTransitionException(
            EditorialArticleState from,
            EditorialArticleState to,
            EditorialArticleState required) {
        super("Invalid editorial article state transition: " + from + " -> " + to
                + "; approved workflow requires " + required);
    }
}
