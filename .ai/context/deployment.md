# Deployment and Environments

## Decision

Use **Railway** for the Spring Boot backend and PostgreSQL in the MVP.

Use **Vercel** for the Next.js frontend.

Use one GitHub repository containing both applications.

## Why Railway

Railway supports Spring Boot deployment from GitHub and provides managed PostgreSQL with
`DATABASE_URL` service references. It also supports multiple environments and deployment
configuration.

This is intentionally simpler than AWS for the first version.

## Why Vercel

Next.js is the frontend framework, and Vercel provides a straightforward deployment model
for Next.js with preview deployments and production deployments.

## Environments

Use three conceptual environments:

```text
local
staging
production
```

### Local

Developer machine.

```text
Next.js       http://localhost:3000
Spring Boot   http://localhost:8080
PostgreSQL    localhost:5432
MinIO         localhost:9000
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
api-staging.<domain>
```

### Production

Used by real users.

Suggested domains:

```text
<domain>
api.<domain>
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

Railway's PostgreSQL service exposes a `DATABASE_URL` variable that can be referenced by the
backend service.

## Frontend deployment

Vercel:

- production deployment from `main`;
- preview deployments for pull requests;
- staging can be a dedicated Vercel environment/domain if required.

The frontend receives:

```text
NEXT_PUBLIC_API_BASE_URL
```

This is not a secret.

## Backend environment variables

Staging and production must define their own:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD

APP_BASE_URL
FRONTEND_BASE_URL
ALLOWED_ORIGINS

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

SESSION_COOKIE_SECURE
SESSION_COOKIE_SAME_SITE
```

Do not commit real values.

## Database migrations

Use a migration tool such as Flyway.

Migrations run as part of deployment/pre-deploy.

Do not use Hibernate `ddl-auto=update` in staging/production.

## CI/CD

GitHub Actions:

- run backend tests;
- run frontend checks/build;
- optionally run static analysis.

Deployment:

- Railway can auto-deploy connected GitHub services;
- Vercel can auto-deploy the frontend.

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

- local `.env` for development;
- Railway environment variables for backend staging/production;
- Vercel environment variables for frontend.

Never put secrets into GitHub Actions source files.
