# Next Work

Each priority is a vertical slice: backend + the minimal frontend needed to actually see and
use it, not backend-only followed by a UI pass at the end. See `.ai/context/roadmap.md` —
"producto funcional" is priority #1, and that can't be judged from a Postman response.

## Priority 1 — Authentication vertical slice (Google OAuth2)

Backend:

- [ ] register OAuth2 app in Google Cloud Console (local + staging redirect URIs)
- [ ] `spring-boot-starter-oauth2-client` + Google registration config
- [ ] user entity/repository (keyed by `google_sub`, see `.ai/domain/users.md`)
- [ ] find-or-create user on first login
- [ ] `GET /api/auth/me` (current-user endpoint)
- [ ] logout
- [ ] CSRF bootstrap
- [ ] auth tests (login redirect, session establishment, ownership checks, unauthenticated 401)

Frontend:

- [ ] "Sign in with Google" entry point (redirect to `/oauth2/authorization/google`)
- [ ] CSRF bootstrap call on app load
- [ ] fetch + display current user (`GET /api/auth/me`)
- [ ] logout button
- [ ] redirect to login when unauthenticated

## Priority 2 — Project & upload vertical slice

Backend:

- [ ] project entity
- [ ] project CRUD
- [ ] object storage port
- [ ] MinIO adapter
- [ ] image upload endpoint
- [ ] ownership tests

Frontend:

- [ ] create project screen
- [ ] image upload UI (loading/error states — see `.ai/rules/frontend.md`)
- [ ] project list / dashboard

## Priority 3 — Plan & credit model (seed only, no Stripe yet)

This has to exist before anything consumes credits. It does not need real payment collection —
only a plan/entitlement model that Priority 4/5 can debit against. Stripe is layered on top of
this in Priority 6, not invented at that point.

- [ ] `Plan` entity (FREE / CREATOR / PRO), seeded via migration
- [ ] `Subscription` linking `User` → `Plan`, defaulting new users to FREE
- [ ] `CreditTransaction` ledger — GRANT / DEBIT / REFUND / ADJUSTMENT + `idempotencyKey`, see `.ai/domain/credits.md`
- [ ] grant initial FREE credits on user creation
- [ ] `GET /api/me/credits`
- [ ] ledger tests (idempotency, balance derivation, no double-grant)
- [ ] frontend: show credit balance somewhere visible (header/dashboard)

## Priority 4 — Listing generation vertical slice

Backend:

- [ ] AI provider port (`TextGenerationProvider`)
- [ ] Gemini adapter
- [ ] structured output
- [ ] prompt version
- [ ] listing validation
- [ ] generation persistence
- [ ] decide explicitly whether text generation consumes credits in the MVP (open question in `.ai/domain/credits.md`) — do not leave it implicit

Frontend:

- [ ] "Generate listing" action from the project screen
- [ ] loading/queued state while generating
- [ ] display generated title/description/tags
- [ ] error state on generation failure

## Priority 5 — Image generation vertical slice

Backend:

- [ ] image provider port
- [ ] Creator provider
- [ ] Pro provider
- [ ] image storage
- [ ] generation lifecycle
- [ ] credit consumption (against the Priority 3 ledger)

Frontend:

- [ ] "Generate promotional image" action
- [ ] async status: QUEUED/PROCESSING/COMPLETED/FAILED (see `.ai/rules/frontend.md`)
- [ ] display generated image
- [ ] show updated credit balance after consumption

## Priority 6 — Billing (Stripe)

- [ ] Stripe customer creation tied to `User.id`
- [ ] Stripe checkout for Creator/Pro plans
- [ ] Stripe webhook handling — syncs the Priority 3 `Plan`/`Subscription` model, does not redefine it
- [ ] idempotency tests

Frontend:

- [ ] pricing/upgrade screen
- [ ] checkout redirect
- [ ] plan status display

## Priority 7 — Deploy

- [ ] staging Railway project
- [ ] staging Vercel project
- [ ] production environments
- [ ] environment variables
- [ ] migration command
- [ ] rate limiting on auth, uploads, AI generation and other expensive endpoints (see `.ai/rules/security.md`) — required before production traffic, not optional polish
- [ ] deployment verification
