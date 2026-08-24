# ADR-005 — Google-Only OAuth2 Login + Session Cookie Authentication

## Status

Accepted (revised — original version used email/password; see History below)

## Decision

Use **Google as the sole identity provider**, via Spring Security's OAuth2 Client support.
There is no local password, no `/api/auth/register`, no `/api/auth/login` form.

After Google authenticates the user, Spring Security still establishes its own server-side
session, persisted through Spring Session JDBC, represented by a secure HttpOnly cookie.

Next.js calls Spring Boot directly (not a BFF).

## Reason

The MVP is a browser-first SaaS. Password-based auth (hashing, reset flow, email verification,
brute-force protection) is significant code and security surface that is not the product.
Google already solves identity verification; the backend should not reimplement it.

A third-party managed auth platform (Clerk/Auth0/Supabase Auth) was considered and rejected for
the MVP: it would introduce a paid vendor and a token-trust boundary in the browser without
giving anything Spring Security's native OAuth2 client doesn't already provide for a
single-provider, browser-first app.

Session-cookie authentication (not JWT) is unchanged from the original decision: the app does
not need stateless tokens, and cookies avoid access/refresh token lifecycle management in the
browser. Because cookies are automatically sent by browsers, CSRF protection remains mandatory.

## Consequences

- PostgreSQL stores session state (unchanged).
- CORS must allow the exact frontend origins (unchanged).
- Frontend requests use credentials (unchanged).
- CSRF token must be supplied for state-changing requests (unchanged).
- No password storage, no password reset flow, no email verification flow to build or secure.
- Users without a Google account cannot sign up. Accepted trade-off for the MVP.
- A registered OAuth2 redirect URI is required per environment in Google Cloud Console.
- Adding a second identity provider or password auth later requires a new ADR, not a silent addition.
- Future JWT/public API requirements still require a new ADR.

## History

- Originally accepted with email/password + Spring Security sessions.
- Revised to Google-only OAuth2 login before password auth was implemented (no migration was
  necessary — no users existed yet). The session/cookie/CSRF architecture is unchanged; only the
  identity-verification step changed.
