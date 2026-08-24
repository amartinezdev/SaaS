# REST API Rules

Use resource-oriented REST endpoints.

Examples:

```text
POST   /api/projects
GET    /api/projects
GET    /api/projects/{id}
DELETE /api/projects/{id}

POST   /api/projects/{id}/listing/generate
POST   /api/projects/{id}/images/generate
GET    /api/projects/{id}/generations

GET    /api/me
GET    /api/me/credits

POST   /api/billing/checkout
POST   /api/billing/webhooks/stripe
```

Rules:

- Validate ownership.
- Return stable error structures.
- Use appropriate HTTP status codes.
- Do not expose internal entity structure accidentally.
- Document breaking changes.
