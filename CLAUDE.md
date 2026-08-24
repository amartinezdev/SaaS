# CLAUDE.md

This repository uses `.ai/` as its structured project brain.

## Workflow

1. Inspect the request.
2. Identify affected domains.
3. Read the minimum relevant `.ai/` documents.
4. Check current implementation before changing it.
5. Check applicable project skills under `.ai/skills/`.
6. Implement the smallest correct change.
7. Run relevant tests.
8. Update project state/documentation when needed.

## Context discipline

Do not read the entire `.ai/` directory unless explicitly asked.

Prefer targeted retrieval. Large documentation files should not be loaded when a smaller,
task-specific file is sufficient.

## Authentication and deployment

For authentication changes, read `.ai/context/authentication.md`.
For deployment/environment changes, read `.ai/context/deployment.md`.

## Architecture

The MVP is a modular monolith:

- Next.js + TypeScript frontend
- Spring Boot + Java backend
- PostgreSQL
- Object storage
- External AI providers behind interfaces
- Stripe for billing

Do not introduce a new architectural pattern or infrastructure component silently.

## AI

AI providers must be isolated behind ports/adapters.

Do not put Gemini API calls directly in controllers or frontend code.

Before modifying AI behavior, inspect:

- `.ai/ai/overview.md`
- `.ai/ai/providers.md`
- relevant generation/validation/skills documentation
- `.ai/domain/credits.md` for paid generation

## Code quality

Prefer maintainable, explicit code over clever abstractions.

Do not add dependencies without justification.

Do not hard-code provider model names throughout the codebase.

## Secrets

Never commit real API keys, tokens, passwords or webhook secrets.

Use `.env.example` and environment variables.

## Git authorship

All commits and pushes in this repository are made under the repo owner's own name/email
(the configured `user.name`/`user.email`), even when Claude authors the change.

Do not add a `Co-Authored-By: Claude` (or any AI) trailer, and do not otherwise credit Claude/AI
as a contributor in commit messages, PR descriptions, or anywhere else in git history. This
overrides Claude Code's default commit-message template for this repository specifically.
