package com.readyroad.readyroadbackend.domain.enums;

/**
 * Question type for the Sign Quiz System.
 *
 * Choice-count rule:
 * HARD questions → exactly 2 choices
 * EASY/MEDIUM questions → up to 3 choices
 */
public enum SignQuestionType {

    /** Q01 — What does this traffic sign mean? (EASY) */
    WHAT_DOES_IT_MEAN,

    /** Q02 — Which category does this sign belong to? (EASY) */
    WHICH_SIGN,

    /** Q03, Q04 — What must you do when you see this sign? (MEDIUM) */
    WHAT_MUST_YOU_DO,

    /** Q05 — Is [action] allowed / required? */
    IS_IT_ALLOWED,

    /** Q03 variant — What hazard does this sign warn about? (MEDIUM, 3 choices) */
    HAZARD_IDENTIFICATION,

    /** Q05 variant — What action must the driver take? */
    DRIVER_ACTION,

    /** Where / on which side does this sign apply? */
    WHERE_DOES_IT_APPLY,

    /** What happens if the driver ignores this sign? */
    WHAT_HAPPENS_IF,

    /**
     * Q06 variant — Where/how far from X is the sign placed? (MEDIUM, 3 choices)
     */
    SIGN_PLACEMENT_DISTANCE
}
