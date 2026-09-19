-- PostgreSQL production path. Uses Flyway's configured readyroad schema/search_path.
-- Additive and forward-only: roll back application code, retain payment records.
CREATE TABLE purchases (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    plan VARCHAR(24) NOT NULL CHECK (plan IN ('RIJVIA_3_DAYS', 'RIJVIA_1_WEEK', 'RIJVIA_4_WEEKS')),
    client_request_id VARCHAR(64) UNIQUE NOT NULL,
    checkout_session_id VARCHAR(255) UNIQUE,
    payment_intent_id VARCHAR(255) UNIQUE,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')),
    checkout_locale VARCHAR(2) NOT NULL CHECK (checkout_locale IN ('ar', 'nl', 'fr', 'en')),
    checkout_url TEXT,
    checkout_expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_purchases_user ON purchases(user_id);

CREATE TABLE stripe_webhook_events (
    stripe_event_id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(16) NOT NULL CHECK (status IN ('PROCESSED', 'IGNORED'))
);

CREATE TABLE user_entitlement (
    user_id BIGINT PRIMARY KEY REFERENCES users(id),
    status VARCHAR(16) NOT NULL DEFAULT 'FREE' CHECK (status IN ('FREE', 'ACTIVE', 'EXPIRED')),
    expires_at TIMESTAMPTZ
);
