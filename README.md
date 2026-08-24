# 3D Listing AI

AI SaaS for turning 3D models into ready-to-publish listings and promotional assets.

## Planned stack

- Frontend: Next.js + TypeScript + React + Tailwind CSS
- Backend: Java + Spring Boot
- Database: PostgreSQL
- Storage: S3-compatible object storage / Cloudflare R2
- Billing: Stripe
- AI: provider abstraction with Gemini and a free/open-source image provider
- Deployment target: Vercel + managed Spring Boot host + managed PostgreSQL + object storage

## Repository brain

Read `AGENTS.md` first.

The structured project knowledge base is under `.ai/`.

- `.ai/context/` — project context and architecture
- `.ai/rules/` — engineering rules
- `.ai/domain/` — business/domain knowledge
- `.ai/ai/` — AI architecture, prompts, validation and costs
- `.ai/platforms/` — marketplace/platform-specific rules
- `.ai/product/` — plans, pricing, roadmap and metrics
- `.ai/decisions/` — ADRs
- `.ai/state/` — current project state
- `.ai/skills/` — reusable development skills

## Local development

1. Copy `.env.example` to `.env`.
2. Start infrastructure: `docker compose up -d postgres minio`.
3. Start Spring Boot on `http://localhost:8080`.
4. Start Next.js on `http://localhost:3000`.
5. Use `.ai/context/authentication.md` for the session/CSRF flow.

Detailed setup: `docs/deployment/local.md`.

## MVP

The first milestone is:

Upload image → analyze → generate title/description/tags → generate promotional image → save/export.
