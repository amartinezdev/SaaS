# Security Rules

## Authentication

MVP authentication uses:

- Google OAuth2 login only (Spring Security OAuth2 Client) — no local passwords
- Spring Security
- server-side sessions
- Spring Session JDBC
- HttpOnly Secure SameSite=Lax session cookie
- CSRF protection for cookie-authenticated JavaScript requests

See `.ai/context/authentication.md`.

Do not introduce JWT without an ADR. Do not add a second identity provider or password auth
without an ADR.

## Secrets

Never commit:

- API keys
- Google OAuth client secret
- JWT secrets (not used by the MVP)
- Stripe secrets
- database passwords
- storage secrets

## Authorization

Every project/file/generation access must verify ownership or explicit permission.

## Uploads

Validate:

- size;
- MIME;
- extension;
- content where practical.

Store untrusted uploads outside executable paths.

## AI prompts

Never place system secrets into user-visible prompts.

Treat user-provided text as untrusted input.

## Billing

Verify Stripe webhook signatures.

Never trust frontend billing state.

## Abuse prevention

Rate-limit:

- authentication;
- uploads;
- AI generations;
- expensive endpoints.
