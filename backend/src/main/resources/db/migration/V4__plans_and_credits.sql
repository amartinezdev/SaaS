CREATE TABLE plans (
    code VARCHAR(20) PRIMARY KEY,
    display_name VARCHAR(80) NOT NULL
);

INSERT INTO plans (code, display_name) VALUES
    ('FREE', 'Free'),
    ('CREATOR', 'Creator'),
    ('PRO', 'Pro');

CREATE TABLE subscriptions (
    user_id UUID PRIMARY KEY REFERENCES app_users(id),
    plan_code VARCHAR(20) NOT NULL REFERENCES plans(code),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE credit_transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_users(id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('GRANT', 'DEBIT', 'REFUND', 'ADJUSTMENT')),
    amount BIGINT NOT NULL CHECK (amount <> 0),
    reason VARCHAR(255) NOT NULL,
    idempotency_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT credit_transaction_direction CHECK (
        (type IN ('GRANT', 'REFUND') AND amount > 0) OR
        (type = 'DEBIT' AND amount < 0) OR
        type = 'ADJUSTMENT'
    ),
    CONSTRAINT credit_transaction_idempotency UNIQUE (user_id, idempotency_key)
);

CREATE INDEX credit_transactions_user_created_idx ON credit_transactions (user_id, created_at DESC);

INSERT INTO subscriptions (user_id, plan_code, created_at, updated_at)
SELECT id, 'FREE', created_at, created_at FROM app_users;

INSERT INTO credit_transactions (id, user_id, type, amount, reason, idempotency_key, created_at)
SELECT gen_random_uuid(), id, 'GRANT', 1, 'Initial free credit', 'welcome:' || id, created_at
FROM app_users;
