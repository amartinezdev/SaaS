# 3D Listing AI

AI SaaS for turning 3D models into ready-to-publish listings and promotional assets.

## Planned stack

- Frontend: Next.js + TypeScript + React + Tailwind CSS
- Backend: Java + Spring Boot
- Database: PostgreSQL
- Storage: S3-compatible object storage / Cloudflare R2
- Billing: Stripe
- AI: provider abstraction with Gemini and a free/open-source image provider
- Deployment target: one Railway Spring Boot service (including the static frontend),
  separate Railway PostgreSQL, and object storage

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
2. Start the application and local infrastructure: `docker compose up -d --build`.
3. Open `http://localhost:8080`.
4. Optionally start the Next.js development server on `http://localhost:3000` after copying
   `frontend/.env.example` to `frontend/.env.local`.
5. Use `.ai/context/authentication.md` for the session/CSRF flow.

Detailed setup: `docs/deployment/local.md`.

Production deployment uses the root `Dockerfile` to bundle both applications into one
Spring Boot runtime image.

## MVP

The first milestone is:

Upload model image → analyze → generate title/description/tags → generate promotional image →
review → assist publication of the creator's `.3mf` and listing on MakerWorld.

The marketplace publishing step needs validation of MakerWorld's current form and rules;
the browser automation component has not been implemented yet.
