CREATE TABLE generations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_users(id),
    project_id UUID REFERENCES projects(id) ON DELETE SET NULL,
    request_key VARCHAR(120) NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    provider VARCHAR(40) NOT NULL,
    model VARCHAR(100) NOT NULL,
    prompt_version VARCHAR(20) NOT NULL,
    input_tokens INTEGER,
    output_tokens INTEGER,
    estimated_cost_usd NUMERIC(12, 8),
    credits_used INTEGER NOT NULL DEFAULT 0,
    error_code VARCHAR(60),
    started_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    CONSTRAINT generations_request_unique UNIQUE (user_id, request_key)
);

CREATE INDEX generations_project_started_idx ON generations (project_id, started_at DESC);

CREATE TABLE listings (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    generation_id UUID NOT NULL UNIQUE REFERENCES generations(id),
    title VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    tags JSONB NOT NULL,
    keywords JSONB NOT NULL,
    category VARCHAR(80) NOT NULL,
    language VARCHAR(10) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX listings_project_created_idx ON listings (project_id, created_at DESC);
