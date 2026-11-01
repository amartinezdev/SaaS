# API Documentation

The API contract should eventually be represented using OpenAPI.

Initial endpoint groups:

- authentication
- users
- projects
- listings
- generations
- credits
- billing

Do not duplicate the source of truth between hand-written docs and generated OpenAPI indefinitely.

## Authentication (current)

- `GET /oauth2/authorization/google`: browser redirect to Google.
- `GET /api/auth/csrf`: public; returns a CSRF token and sets the CSRF cookie.
- `GET /api/auth/me`: returns `{ id, email, name }` for the session user; 401 without a session.
- `POST /logout`: requires `X-XSRF-TOKEN`; invalidates the session and returns 204.

The `id` is the application's UUID. Google `sub` remains an internal identity key.

## Projects (current)

All routes require a signed-in session. Mutations require `X-XSRF-TOKEN` from
`GET /api/auth/csrf`. Projects belonging to another user return 404.

- `POST /api/projects` with `{ "name": "..." }`: create, returning 201 and the project.
- `GET /api/projects`: list the user's projects.
- `GET /api/projects/{id}`: get one project.
- `PUT /api/projects/{id}` with `{ "name": "..." }`: rename.
- `DELETE /api/projects/{id}`: delete, returning 204.
- `POST /api/projects/{id}/image`: multipart field `file`, PNG/JPEG/WebP, maximum 10 MiB.
- `GET /api/projects/{id}/image`: retrieve the private source image.

Project responses contain `id`, `name`, `hasImage`, `createdAt`, and `updatedAt`.
Validation, missing-project, oversize-upload, and storage errors use `{ "code", "message" }`.

## Credits (current)

- `GET /api/me/credits`: requires a signed-in session; returns `{ "plan": "FREE", "balance": 1 }`
  for a newly created account with the default trial allocation. Returns 401 without a session.

The credit ledger can only be changed by backend operations. The current API has no
client-controlled credit grant, debit, or plan change endpoint.

## Listing generation (current)

- `POST /api/projects/{id}/listing/generate`: authenticated, CSRF-protected. Send
  `Idempotency-Key: <unique request key>` and `{ "language": "en" }` or `es`.
  Requires a project image and one credit; returns the validated draft.
- `GET /api/projects/{id}/listing`: returns the latest draft for the signed-in owner.

The response contains `id`, `title`, `description`, `tags`, `keywords`, `category`,
`language`, and `createdAt`. The draft is general rather than platform-specific and
must be reviewed by the creator. The backend limits attempts per user/day; failed
generations refund the reserved credit. Errors use `{ "code", "message" }`.
