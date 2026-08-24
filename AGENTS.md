# AGENTS.md — 3D Listing AI

## Project

3D Listing AI is a SaaS for 3D creators. It turns a model image (and later optional STL metadata)
into a ready-to-publish listing: title, description, tags, keywords, category and promotional images.

## Source of truth

The `.ai/` directory is the structured project knowledge base.

Before coding, identify the affected area and read only the relevant documentation. Do not load the
entire `.ai/` tree by default.

## Required context lookup

- Product/task context: `.ai/context/project.md`
- Architecture: `.ai/context/architecture.md`
- Stack: `.ai/context/tech-stack.md`
- Authentication: `.ai/context/authentication.md`
- Deployment/environments: `.ai/context/deployment.md`
- Backend: `.ai/rules/backend.md`
- Frontend: `.ai/rules/frontend.md`
- API: `.ai/rules/api.md`
- Database: `.ai/rules/database.md`
- Security: `.ai/rules/security.md`
- Testing: `.ai/rules/testing.md`
- AI: `.ai/ai/overview.md` plus the relevant AI document
- Domain: relevant `.ai/domain/*.md`
- Platforms: relevant `.ai/platforms/*.md`
- Product/pricing: relevant `.ai/product/*.md`
- Architectural decisions: `.ai/decisions/README.md`
- Current work: `.ai/state/current.md`, `.ai/state/next.md`

## Skills

Project skills live under `.ai/skills/`. When a task clearly matches a skill, read its `SKILL.md`
before implementing it.

Skills describe HOW to perform a recurring task. Domain documents describe WHAT the system means.

Do not execute every skill automatically.

## Engineering rules

- Keep the MVP a modular monolith.
- Do not introduce microservices, queues, Redis, Kafka, Kubernetes or similar infrastructure without
  a concrete documented reason.
- Keep external providers behind application ports/adapters.
- Never expose secrets to the frontend.
- Business rules belong in the backend.
- Use structured AI outputs rather than parsing free-form text where possible.
- Validate AI output before returning it to users.
- Track AI generation cost and credit consumption.
- Make payment webhooks and credit operations idempotent.
- Do not store large binaries in PostgreSQL.
- Prefer small, focused changes.
- Do not rewrite files unnecessarily.
- Do not invent requirements.
- Update documentation when architecture or observable behavior changes.

## Before finishing a task

1. Run the relevant tests/checks.
2. Check security implications.
3. Check cost implications for paid AI providers.
4. Update relevant `.ai/` documentation if behavior changed.
5. Update `.ai/state/current.md` or `.ai/state/completed.md` when appropriate.
