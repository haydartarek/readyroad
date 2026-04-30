-- V7__Add_Content_Tables.sql
-- ========================================
-- Traffic Rules Table
-- ========================================
CREATE TABLE traffic_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_code VARCHAR(20) NOT NULL UNIQUE,
    title_ar TEXT NOT NULL,
    title_en TEXT NOT NULL,
    title_nl TEXT NOT NULL,
    title_fr TEXT NOT NULL,
    content_ar TEXT,
    content_en TEXT,
    content_nl TEXT,
    content_fr TEXT,
    category_id BIGINT,                          -- ✅ FK بدلاً من VARCHAR
    importance_level ENUM('HIGH','MEDIUM','LOW') DEFAULT 'MEDIUM',
    applies_to ENUM('ALL','CAR','MOTORCYCLE','BICYCLE','TRUCK','BUS') DEFAULT 'ALL', -- ✅ ENUM
    penalty_info_ar TEXT,
    penalty_info_en TEXT,
    penalty_info_nl TEXT,
    penalty_info_fr TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,         -- ✅ DEFAULT
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_category (category_id),
    INDEX idx_importance (importance_level),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Traffic Sign Rules Mapping Table
-- ========================================
CREATE TABLE traffic_sign_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    traffic_sign_id BIGINT NOT NULL,
    traffic_rule_id BIGINT NOT NULL,
    relationship_type ENUM('REQUIRED','RELATED','EXCEPTION') DEFAULT 'RELATED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,         -- ✅ DEFAULT
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE CASCADE,
    FOREIGN KEY (traffic_rule_id) REFERENCES traffic_rules(id) ON DELETE CASCADE,
    UNIQUE KEY uq_sign_rule (traffic_sign_id, traffic_rule_id),      -- ✅ منع التكرار
    INDEX idx_sign (traffic_sign_id),
    INDEX idx_rule (traffic_rule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Traffic Sign Detailed Descriptions Table
-- ========================================
CREATE TABLE traffic_sign_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    traffic_sign_id BIGINT NOT NULL UNIQUE,
    detailed_description_ar TEXT,
    detailed_description_en TEXT,
    detailed_description_nl TEXT,
    detailed_description_fr TEXT,
    when_to_use_ar TEXT,
    when_to_use_en TEXT,
    when_to_use_nl TEXT,
    when_to_use_fr TEXT,
    common_mistakes_ar TEXT,
    common_mistakes_en TEXT,
    common_mistakes_nl TEXT,
    common_mistakes_fr TEXT,
    tips_ar TEXT,
    tips_en TEXT,
    tips_nl TEXT,
    tips_fr TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,         -- ✅ DEFAULT
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (traffic_sign_id) REFERENCES traffic_signs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
