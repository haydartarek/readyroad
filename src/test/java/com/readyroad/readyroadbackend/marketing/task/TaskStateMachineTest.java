package com.readyroad.readyroadbackend.marketing.task;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.readyroad.readyroadbackend.marketing.domain.TaskStatus;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TaskStateMachineTest {

    private final TaskStateMachine stateMachine = new TaskStateMachine();

    @ParameterizedTest
    @MethodSource("allowedTransitions")
    void acceptsOnlyApprovedStateTransitions(TaskStatus from, TaskStatus to) {
        assertThatCode(() -> stateMachine.validate(from, to)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @MethodSource("invalidTransitions")
    void rejectsUndefinedStateTransitions(TaskStatus from, TaskStatus to) {
        assertThatThrownBy(() -> stateMachine.validate(from, to))
                .isInstanceOf(InvalidTaskStateTransitionException.class)
                .hasMessageContaining(from.name())
                .hasMessageContaining(to.name());
    }

    private static Stream<Arguments> allowedTransitions() {
        return Stream.of(
                Arguments.of(TaskStatus.PENDING, TaskStatus.RUNNING),
                Arguments.of(TaskStatus.PENDING, TaskStatus.SCHEDULED),
                Arguments.of(TaskStatus.PENDING, TaskStatus.WAITING_APPROVAL),
                Arguments.of(TaskStatus.PENDING, TaskStatus.CANCELLED),
                Arguments.of(TaskStatus.SCHEDULED, TaskStatus.RUNNING),
                Arguments.of(TaskStatus.SCHEDULED, TaskStatus.CANCELLED),
                Arguments.of(TaskStatus.WAITING_APPROVAL, TaskStatus.APPROVED),
                Arguments.of(TaskStatus.WAITING_APPROVAL, TaskStatus.REJECTED),
                Arguments.of(TaskStatus.WAITING_APPROVAL, TaskStatus.CANCELLED),
                Arguments.of(TaskStatus.APPROVED, TaskStatus.RUNNING),
                Arguments.of(TaskStatus.APPROVED, TaskStatus.SCHEDULED),
                Arguments.of(TaskStatus.APPROVED, TaskStatus.WAITING_APPROVAL),
                Arguments.of(TaskStatus.APPROVED, TaskStatus.CANCELLED),
                Arguments.of(TaskStatus.RUNNING, TaskStatus.COMPLETED),
                Arguments.of(TaskStatus.RUNNING, TaskStatus.RETRY_SCHEDULED),
                Arguments.of(TaskStatus.RUNNING, TaskStatus.FAILED),
                Arguments.of(TaskStatus.RETRY_SCHEDULED, TaskStatus.RUNNING),
                Arguments.of(TaskStatus.RETRY_SCHEDULED, TaskStatus.CANCELLED));
    }

    private static Stream<Arguments> invalidTransitions() {
        return Stream.of(
                Arguments.of(TaskStatus.COMPLETED, TaskStatus.RUNNING),
                Arguments.of(TaskStatus.FAILED, TaskStatus.RUNNING),
                Arguments.of(TaskStatus.REJECTED, TaskStatus.APPROVED),
                Arguments.of(TaskStatus.CANCELLED, TaskStatus.PENDING),
                Arguments.of(TaskStatus.PENDING, TaskStatus.COMPLETED));
    }
}
