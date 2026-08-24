# Current State

## Phase

Architecture bootstrap

## Implemented

- project brain structure
- `.ai/skills/` development skills
- product AI skills
- modular-monolith architecture
- authentication decision: Google OAuth2 login only + Spring Security + Spring Session JDBC + HttpOnly cookie + CSRF (revised from the original email/password design in ADR-005, before any auth code existed)
- deployment decision: Vercel + Railway + Railway PostgreSQL
- local Docker Compose for PostgreSQL + MinIO
- `.env.example`
- initial Spring Boot security/health skeleton
- Flyway session migration
- frontend API client skeleton
- CI workflow skeletons (currently disabled — see Known issues)

## In progress

- Priority 1: Google OAuth2 login vertical slice (see `.ai/state/next.md`)

## Blocked

- none

## Known issues

- CI is disabled: `.github/` was renamed to `github/` (no leading dot) because the workflows
  failed on every push (no CI-relevant code exists yet) and spammed failure emails. Rename it
  back to `.github` once there's something worth running — see `github/workflows/`.
- Google OAuth2 client credentials (client ID/secret) not yet created in Google Cloud Console for any environment;
- exact production domains are not chosen;
- free image provider has not been selected;
- provider model IDs/pricing must be verified before production;
- platform-specific rules are placeholders until verified against official documentation.

## Current milestone

Build the first end-to-end listing generation vertical slice.

## Next

See `.ai/state/next.md`.
