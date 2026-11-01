# Deployment

## Target MVP

Application:
Railway Spring Boot service serving the static Next.js frontend and API from one origin.

Database:
separate Railway PostgreSQL service.

Storage:
Cloudflare R2/S3-compatible.

Billing:
Stripe.

AI:
external providers.

## Environments

- local
- staging
- production

Never reuse production secrets locally.
