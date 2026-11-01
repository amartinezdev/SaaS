# ADR-008 — Single-origin Railway deployment

## Status

Accepted; supersedes ADR-006.

## Decision

Export the Next.js frontend as static files and bundle them into the Spring Boot JAR.
Deploy one application service plus a separate PostgreSQL service in Railway per environment.
Keep generated and uploaded binaries in S3-compatible object storage.

The browser and API use one HTTPS origin. Next.js is a build tool, not a production server.
Google OAuth redirects back to the root of that origin. A separate Next.js development server
may run locally, with explicit CORS for `http://localhost:3000` only.

## Reason

The current frontend has no server-side Next.js requirements. One application deployment
avoids paying for a second frontend host and simplifies session cookies, CORS, and OAuth
redirects. The monolith and database remain separate Railway services.

## Consequences

- Frontend features must support Next.js static export; API and authentication stay in Spring Boot.
- The root Dockerfile builds both applications and copies the frontend export into the JAR.
- A frontend change redeploys the application service.
- Staging and production each need their own PostgreSQL and Google OAuth redirect URI.
