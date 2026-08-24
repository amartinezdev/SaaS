# ADR-006 — Railway + Vercel MVP Deployment

## Status

Accepted

## Decision

- Next.js frontend: Vercel
- Spring Boot backend: Railway
- PostgreSQL: Railway
- Object storage: Cloudflare R2/S3-compatible
- CI: GitHub Actions
- Deployment: Git-connected automatic deployments with controlled staging/production environments

## Reason

This provides a low-operations path for a small SaaS while keeping frontend/backend/database
separated and configurable.

AWS/Kubernetes are intentionally deferred.

## Consequences

- Environment variables are managed per deployment environment.
- Staging and production databases are separate.
- Deployment is tied to Git commits.
