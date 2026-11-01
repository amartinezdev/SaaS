# Next Work

Each priority is a vertical slice: backend + the minimal frontend needed to actually see and
use it, not backend-only followed by a UI pass at the end. See `.ai/context/roadmap.md` —
"producto funcional" is priority #1, and that can't be judged from a Postman response.

## Priority 1 — Authentication vertical slice (Google OAuth2)

Backend:

- [ ] register OAuth2 app in Google Cloud Console (local + staging redirect URIs)
- [x] `spring-boot-starter-oauth2-client` + Google registration config
- [x] user entity/repository (keyed by `google_sub`, see `.ai/domain/users.md`)
- [x] find-or-create user on first login
- [x] `GET /api/auth/me` (current-user endpoint)
- [x] logout
- [x] CSRF bootstrap
- [x] auth unit/web tests (login initiation, logout CSRF, unauthenticated 401, local identity)
- [x] verify a real local Google login with the PostgreSQL-backed session (confirmed by user)

Frontend:

- [x] "Sign in with Google" entry point (redirect to `/oauth2/authorization/google`)
- [x] CSRF bootstrap call on app load
- [x] fetch + display current user (`GET /api/auth/me`)
- [x] logout button
- [x] show sign-in entry point when unauthenticated

## Priority 2 — Project & upload vertical slice

Backend:

- [x] project entity
- [x] project CRUD
- [x] object storage port
- [x] S3-compatible adapter (S3Mock for local development)
- [x] image upload endpoint
- [x] ownership tests

Frontend:

- [x] create project screen
- [x] image upload UI (loading/error states — see `.ai/rules/frontend.md`)
- [x] project list / dashboard

## Priority 3 — Plan & credit model (seed only, no Stripe yet)

This has to exist before anything consumes credits. It does not need real payment collection —
only a plan/entitlement model that Priority 4/5 can debit against. Stripe is layered on top of
this in Priority 6, not invented at that point.

- [x] `Plan` entity (FREE / CREATOR / PRO), seeded via migration
- [x] `Subscription` linking `User` → `Plan`, defaulting new users to FREE
- [x] `CreditTransaction` ledger — GRANT / DEBIT / REFUND / ADJUSTMENT + `idempotencyKey`, see `.ai/domain/credits.md`
- [x] grant initial FREE credits on user creation
- [x] `GET /api/me/credits`
- [x] ledger tests (idempotency, balance derivation, no double-grant)
- [x] frontend: show credit balance somewhere visible (header/dashboard)

## Priority 4 — Listing generation vertical slice

Backend:

- [x] AI provider port (`TextGenerationProvider`)
- [x] Gemini adapter
- [x] structured output
- [x] prompt version
- [x] listing validation
- [x] generation persistence
- [x] decide explicitly whether text generation consumes credits in the MVP (one credit, refunded on failure)

Frontend:

- [x] "Generate listing" action from the project screen
- [x] loading/queued state while generating
- [x] display generated title/description/tags
- [x] error state on generation failure

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
- [ ] verify the bundled frontend on staging Railway
- [ ] production environments
- [ ] environment variables
- [ ] migration command
- [ ] rate limiting on auth, uploads, AI generation and other expensive endpoints (see `.ai/rules/security.md`) — required before production traffic, not optional polish
- [ ] deployment verification

## Publishing workflow discovery

- [ ] validate MakerWorld's current `.3mf` upload form and automation rules
- [ ] choose an extension or local automation component for browser-assisted publishing
- [ ] map generated listing fields and creator-provided `.3mf` to MakerWorld's form
- [ ] add separate adapters for other platforms after validating each flow
