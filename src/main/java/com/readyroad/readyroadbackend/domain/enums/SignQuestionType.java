package com.readyroad.readyroadbackend.domain.enums;

/**
 * Question type for the Sign Quiz System.
 *
 * Choice-count rule (enforced by the Importer):
 *   IS_IT_ALLOWED  → exactly 2 choices (Yes / No binary)
 *   all other types → exactly 3 choices
 */
public enum SignQuestionType {

    /** Q01 — What does this traffic sign mean? (EASY) */
    WHAT_DOES_IT_MEAN,

    /** Q02 — Which category does this sign belong to? (EASY) */
    WHICH_SIGN,

    /** Q03, Q04 — What must you do when you see this sign? (MEDIUM) */
    WHAT_MUST_YOU_DO,

    /** Q05 — Is [action] allowed / required? (HARD, binary Yes/No) */
    IS_IT_ALLOWED
}
