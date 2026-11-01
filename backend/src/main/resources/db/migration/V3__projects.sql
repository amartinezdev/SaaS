CREATE TABLE projects (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES app_users(id),
    name VARCHAR(160) NOT NULL,
    image_key VARCHAR(512),
    image_content_type VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX projects_owner_created_idx ON projects (owner_id, created_at DESC);
