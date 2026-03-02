-- Phase 2: Import History table for tracking all import operations
CREATE TABLE IF NOT EXISTS import_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    performed_by    VARCHAR(100)  NOT NULL,
    performed_at    DATETIME      NOT NULL,
    import_type     VARCHAR(50)   NOT NULL,
    file_name       VARCHAR(255)  NOT NULL,
    file_checksum   VARCHAR(64),
    dry_run         BOOLEAN       NOT NULL DEFAULT FALSE,
    created_count   INT           NOT NULL DEFAULT 0,
    updated_count   INT           NOT NULL DEFAULT 0,
    skipped_count   INT           NOT NULL DEFAULT 0,
    status          VARCHAR(20)   NOT NULL DEFAULT 'SUCCESS',
    error_summary   TEXT,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_import_history_performed_at (performed_at DESC),
    INDEX idx_import_history_type (import_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
