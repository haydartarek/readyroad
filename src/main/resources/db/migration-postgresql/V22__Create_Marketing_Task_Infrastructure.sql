CREATE TABLE agent_definitions (
    id BIGSERIAL PRIMARY KEY,
    agent_type VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(160) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE agent_tasks (
    id BIGSERIAL PRIMARY KEY,
    agent_type VARCHAR(64) NOT NULL REFERENCES agent_definitions(agent_type),
    task_type VARCHAR(128) NOT NULL,
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    payload_version INTEGER NOT NULL DEFAULT 1 CHECK (payload_version > 0),
    priority SMALLINT NOT NULL DEFAULT 1 CHECK (priority BETWEEN 0 AND 3),
    status VARCHAR(32) NOT NULL CHECK (status IN (
        'PENDING', 'SCHEDULED', 'WAITING_APPROVAL', 'APPROVED', 'RUNNING',
        'COMPLETED', 'RETRY_SCHEDULED', 'FAILED', 'REJECTED', 'CANCELLED'
    )),
    scheduled_at TIMESTAMPTZ,
    attempts INTEGER NOT NULL DEFAULT 0 CHECK (attempts >= 0),
    max_attempts INTEGER NOT NULL DEFAULT 4 CHECK (max_attempts >= 1),
    next_retry_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(160) NOT NULL,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    failed_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    error_code VARCHAR(128),
    error_message TEXT,
    requires_approval BOOLEAN NOT NULL DEFAULT FALSE,
    approval_mode VARCHAR(64) NOT NULL,
    approval_source VARCHAR(128) NOT NULL,
    approved_by VARCHAR(160),
    approved_at TIMESTAMPTZ,
    rejected_by VARCHAR(160),
    rejected_at TIMESTAMPTZ,
    rejection_reason TEXT,
    idempotency_key VARCHAR(255) NOT NULL,
    correlation_id VARCHAR(128) NOT NULL,
    parent_task_id BIGINT REFERENCES agent_tasks(id),
    source_type VARCHAR(128),
    source_id VARCHAR(255),
    locked_by VARCHAR(160),
    locked_at TIMESTAMPTZ,
    lock_expires_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_agent_tasks_idempotency UNIQUE (agent_type, task_type, idempotency_key)
);

CREATE INDEX idx_agent_tasks_claim
    ON agent_tasks (status, priority DESC, scheduled_at ASC, created_at ASC);
CREATE INDEX idx_agent_tasks_retry ON agent_tasks (status, next_retry_at);
CREATE INDEX idx_agent_tasks_expired_locks ON agent_tasks (status, lock_expires_at);
CREATE INDEX idx_agent_tasks_correlation ON agent_tasks (correlation_id);
CREATE INDEX idx_agent_tasks_parent ON agent_tasks (parent_task_id);
CREATE INDEX idx_agent_tasks_source ON agent_tasks (source_type, source_id);

CREATE TABLE agent_task_attempts (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE CASCADE,
    attempt_number INTEGER NOT NULL CHECK (attempt_number > 0),
    status VARCHAR(32) NOT NULL CHECK (status IN (
        'RUNNING', 'COMPLETED', 'INTERRUPTED', 'RETRY_SCHEDULED', 'FAILED'
    )),
    worker_id VARCHAR(160) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    error_code VARCHAR(128),
    error_message TEXT,
    short_stack TEXT,
    retryable BOOLEAN,
    next_retry_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_agent_task_attempt UNIQUE (task_id, attempt_number)
);

CREATE INDEX idx_agent_task_attempts_task ON agent_task_attempts (task_id, attempt_number);

CREATE TABLE agent_execution_logs (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE CASCADE,
    attempt_id BIGINT REFERENCES agent_task_attempts(id) ON DELETE SET NULL,
    level VARCHAR(16) NOT NULL CHECK (level IN ('DEBUG', 'INFO', 'WARN', 'ERROR')),
    event_code VARCHAR(128) NOT NULL,
    message TEXT NOT NULL,
    safe_context JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agent_execution_logs_task ON agent_execution_logs (task_id, created_at DESC);
CREATE INDEX idx_agent_execution_logs_error ON agent_execution_logs (level, created_at DESC);

CREATE TABLE agent_approvals (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES agent_tasks(id) ON DELETE CASCADE,
    payload_version INTEGER NOT NULL CHECK (payload_version > 0),
    requested_at TIMESTAMPTZ NOT NULL,
    requested_by VARCHAR(160) NOT NULL,
    approved_by VARCHAR(160),
    approved_at TIMESTAMPTZ,
    rejected_by VARCHAR(160),
    rejected_at TIMESTAMPTZ,
    decision VARCHAR(32) NOT NULL CHECK (decision IN ('PENDING', 'APPROVED', 'REJECTED')),
    reason TEXT,
    payload_snapshot JSONB NOT NULL,
    approval_mode VARCHAR(64) NOT NULL,
    approval_source VARCHAR(128) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_agent_approvals_task_payload UNIQUE (task_id, payload_version)
);

CREATE INDEX idx_agent_approvals_pending ON agent_approvals (decision, requested_at);

CREATE TABLE agent_settings (
    id BIGSERIAL PRIMARY KEY,
    agent_type VARCHAR(64) NOT NULL REFERENCES agent_definitions(agent_type),
    setting_key VARCHAR(128) NOT NULL,
    setting_value JSONB NOT NULL,
    updated_by VARCHAR(160) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_agent_settings_key UNIQUE (agent_type, setting_key)
);

CREATE TABLE agent_schedules (
    id BIGSERIAL PRIMARY KEY,
    agent_type VARCHAR(64) NOT NULL REFERENCES agent_definitions(agent_type),
    schedule_key VARCHAR(128) NOT NULL,
    task_type VARCHAR(128) NOT NULL,
    priority SMALLINT NOT NULL DEFAULT 1 CHECK (priority BETWEEN 0 AND 3),
    cron_expression VARCHAR(128) NOT NULL,
    zone_id VARCHAR(64) NOT NULL DEFAULT 'Europe/Brussels',
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    requires_approval BOOLEAN NOT NULL DEFAULT FALSE,
    approval_mode VARCHAR(64) NOT NULL DEFAULT 'STANDING_OWNER_AUTHORIZATION',
    approval_source VARCHAR(128) NOT NULL DEFAULT 'MASTER_SPEC_V3',
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    last_run_at TIMESTAMPTZ,
    next_run_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_agent_schedules_key UNIQUE (agent_type, schedule_key)
);

CREATE INDEX idx_agent_schedules_due ON agent_schedules (enabled, next_run_at);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT REFERENCES agent_tasks(id) ON DELETE SET NULL,
    event_type VARCHAR(128) NOT NULL,
    actor VARCHAR(160) NOT NULL,
    entity_type VARCHAR(128),
    entity_id VARCHAR(255),
    correlation_id VARCHAR(128),
    safe_details JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_task ON audit_logs (task_id, created_at DESC);
CREATE INDEX idx_audit_logs_entity ON audit_logs (entity_type, entity_id, created_at DESC);
CREATE INDEX idx_audit_logs_event ON audit_logs (event_type, created_at DESC);
