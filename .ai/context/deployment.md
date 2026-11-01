# Deployment and Environments

## Decision

Use **Railway** for the Spring Boot backend and PostgreSQL in the MVP.

Build the Next.js frontend as static files inside the Spring Boot application service.

Use one GitHub repository containing both applications.

## Why Railway

Railway supports Dockerfile deployment from GitHub and provides PostgreSQL connection
variables. It also supports multiple environments and deployment
configuration.

This is intentionally simpler than AWS for the first version.

## Why a single application origin

The frontend needs no Next.js server in the MVP. Spring Boot serves its static export and
the API on one origin. This avoids a second hosting plan and cross-origin session handling.

## Environments

Use three conceptual environments:

```text
local
staging
production
```

### Local

Developer machine.

`docker compose up -d --build` starts the application, PostgreSQL, and S3Mock. The
application uses preview OAuth credentials if none are configured; sign-in requires real
Google credentials.

```text
Next.js dev   http://localhost:3000 (optional)
Spring Boot   http://localhost:8080 (serves the bundled frontend in a production build)
PostgreSQL    localhost:5433
S3Mock        localhost:9090
```

### Staging

Used for:

- integration testing;
- Stripe test mode;
- Gemini integration tests;
- deployment verification.

Suggested domains:

```text
staging.<domain>
```

### Production

Used by real users.

Suggested domains:

```text
<domain>
```

## Railway structure

```text
Railway Project: 3d-listing-ai

Environment: staging
  - backend
  - postgres

Environment: production
  - backend
  - postgres
```

Do not share a production database with staging.

Railway's PostgreSQL service exposes `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, and
`PGPASSWORD`. Configure the application `DATABASE_URL` as a JDBC URL using those values.

## Application deployment

The root Dockerfile builds the Next.js static export, copies it into Spring Boot static
resources, and produces one runtime image. Set the Railway application service's build
context to the repository root and use the root Dockerfile. The frontend calls relative
`/api` paths in deployed environments; no public API base URL is required.

## Backend environment variables

Staging and production must define their own:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

ALLOWED_ORIGINS (empty in production; local dev only)

GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET

GEMINI_API_KEY

STRIPE_SECRET_KEY
STRIPE_WEBHOOK_SECRET
STRIPE_CREATOR_PRICE_ID
STRIPE_PRO_PRICE_ID

STORAGE_ENDPOINT
STORAGE_BUCKET
STORAGE_ACCESS_KEY
STORAGE_SECRET_KEY
STORAGE_REGION

SESSION_COOKIE_SECURE
SESSION_COOKIE_SAME_SITE
```

Do not commit real values.

## Database migrations

Use a migration tool such as Flyway.

Flyway migrations run when the application starts.

Do not use Hibernate `ddl-auto=update` in staging/production.

## CI/CD

GitHub Actions:

- run backend tests;
- run frontend checks/build;
- optionally run static analysis.

Deployment:

- Railway can auto-deploy connected GitHub services;
- the frontend is deployed with the Railway application service.

Production deployment should happen from `main`.

Staging deployment should happen from a dedicated staging branch or controlled environment.

## Branch strategy

```text
main
  -> production

staging
  -> staging

feature/*
  -> pull request / preview
```

The exact branch workflow may evolve.

## Rollback

Every production deployment must be traceable to a Git commit.

If a deployment is broken:

1. stop further promotion;
2. inspect logs;
3. rollback to the last known-good deployment;
4. create a fix;
5. redeploy.

## Secrets

Secrets are configured in:

- local environment variables for development (using the `.env.example` template);
- Railway environment variables for the application and database.

Never put secrets into GitHub Actions source files.
