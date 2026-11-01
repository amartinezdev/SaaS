# Current State

## Phase

MVP implementation

## Implemented

- project brain structure
- `.ai/skills/` development skills
- product AI skills
- modular-monolith architecture
- authentication decision: Google OAuth2 login only + Spring Security + Spring Session JDBC + HttpOnly cookie + CSRF (revised from the original email/password design in ADR-005, before any auth code existed)
- deployment decision: one Railway application service (static Next.js export + Spring Boot) and Railway PostgreSQL, per ADR-008
- local Docker Compose for the application, PostgreSQL, and S3Mock
- `.env.example`
- initial Spring Boot security/health skeleton
- Flyway session migration
- Flyway users migration and Google-sub-keyed user persistence
- OAuth2 user provisioning, current-user endpoint, CSRF and logout flow
- frontend sign-in/session/logout screen
- authentication unit and web security tests
- project persistence, owner-scoped CRUD API, and image upload/download through a private S3-compatible storage adapter
- project dashboard with create, rename, delete, and source-image upload/preview
- project ownership, upload validation, and web security tests
- seeded FREE/CREATOR/PRO plans, FREE subscriptions, an idempotent credit ledger, and one configurable trial credit
- authenticated credit-balance API and dashboard summary
- Gemini listing draft generation with structured output, validation, persisted usage/cost,
  idempotent one-credit debit/refund, and a daily request limit
- listing generation and latest-draft UI on project detail
- frontend API client skeleton
- root Dockerfile for a single frontend/backend application build
- assisted MakerWorld `.3mf` publishing added to the MVP target (implementation pending
  platform flow and automation validation)
- CI workflows for frontend build and backend tests

## In progress

- Priority 1: local Google sign-in confirmed by the user; staging credentials and checks remain
- Priority 2: project and image flow implemented; live signed-in upload check remains
- Priority 4: real Gemini generation awaits a Gemini API key; the full workflow passed simulated-provider integration tests

## Blocked

- none

## Known issues

- staging and production Google OAuth2 clients are not configured;
- exact production domains are not chosen;
- free image provider has not been selected;
- provider model IDs/pricing must be verified before production;
- platform-specific rules are placeholders until verified against official documentation.

## Current milestone

Validate the first listing draft with a real Gemini API key, then build image generation.

## Next

See `.ai/state/next.md`.
