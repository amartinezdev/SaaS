# Authentication Architecture

## Decision

Use **Google as the only identity provider**, via Spring Security's OAuth2 Client
(`spring-boot-starter-oauth2-client`) — no local passwords, no custom register/login forms.

Once Google authenticates the user, Spring Security establishes its own **server-side session**
represented by an HttpOnly cookie, exactly as before. Google only proves identity; it is never
trusted for anything beyond that (see [`../domain/users.md`](../domain/users.md)).

Do not use JWTs for the MVP.

## Browser architecture

The browser/Next.js application calls the Spring Boot API directly.

```text
Browser
   |
   | HTTPS
   v
Next.js frontend ---------> Spring Boot API
                                |
                                +--> Google OAuth2 (login only, not the session)
                                |
                                +--> PostgreSQL
                                |
                                +--> Session store
```

Next.js is NOT a BFF in the MVP.

Reason (unchanged from the original decision — see [`../decisions/ADR-005-authentication.md`](../decisions/ADR-005-authentication.md)):

- avoids duplicating backend business/auth logic in Next.js;
- simpler Spring Security integration;
- easier to reason about authorization;
- no access/refresh token lifecycle in browser JavaScript.

## Login flow

```text
Frontend redirects to:
  GET /oauth2/authorization/google
        |
        v
Google login/consent screen
        |
        v
Google redirects back to:
  GET /login/oauth2/code/google
        |
        v
Spring Security verifies the code with Google,
resolves the user's Google profile (sub, email, name),
provisions/loads the local User record,
establishes a session (Spring Session JDBC),
redirects to FRONTEND_BASE_URL
```

The backend never sees or stores a password. There is no `/api/auth/register` or
`/api/auth/login` endpoint — login is the OAuth2 redirect above.

## User provisioning

On first successful login, the backend creates a `User` row keyed by Google's `sub` claim
(a stable, immutable identifier), not by email. See [`../domain/users.md`](../domain/users.md)
for why email must not be used as the identity key.

Subsequent logins with the same Google account resolve to the same `User` row.

## Cookie

The authentication session is represented by a secure cookie.

Production requirements:

- `HttpOnly=true`
- `Secure=true`
- `SameSite=Lax` initially
- narrow cookie path where practical
- no authentication token in localStorage/sessionStorage

The browser automatically sends the session cookie.

## CSRF

Because authentication uses a cookie, CSRF protection is required.

Use Spring Security's CSRF support with a cookie/header pattern suitable for a JavaScript application.

Flow:

```text
GET /api/auth/csrf
        |
        v
Spring Security sets CSRF cookie
        |
        v
Next.js reads CSRF cookie
        |
        v
Mutating request sends:
Cookie: session
X-XSRF-TOKEN: csrf-token
```

The CSRF cookie itself is not the authentication credential.

## CORS

The backend must allow only configured frontend origins.

Local:

```text
http://localhost:3000
```

Staging:

```text
https://staging.<domain>
```

Production:

```text
https://<domain>
```

Never use `*` with credentialed requests.

The frontend uses:

```text
credentials: "include"
```

for API requests.

## API

Current API:

```text
GET  /oauth2/authorization/google   (login — browser redirect, not fetch)
GET  /api/auth/me                   (current user, 401 if not authenticated)
GET  /api/auth/csrf
POST /logout                        (Spring Security default logout endpoint)
```

## Authorization

Authentication answers:

> Who is this user?

Authorization answers:

> Can this user access this project/generation/file?

Every project-owned resource must verify ownership in the backend.

Never trust a user ID supplied by the client.

## Session persistence

Use Spring Session JDBC in the MVP so sessions survive backend restarts/deployments.

PostgreSQL therefore stores:

- application data;
- Spring Session data.

This avoids introducing Redis for the MVP.

## Session expiration

Use a reasonable idle/max session lifetime and allow logout to invalidate the session.

Exact values belong in environment/configuration and may change after UX/security review.

## XSS considerations

Because the auth credential is HttpOnly, JavaScript cannot directly read it.

Still:

- never inject unsanitized HTML;
- sanitize/escape user-generated listing content;
- use React's default escaping;
- do not use `dangerouslySetInnerHTML` unless content is sanitized and the reason is documented.

## Why Google-only instead of email/password

Building password auth means owning: password hashing, password reset flow, email
verification, and brute-force protection on the login endpoint — none of which is the
product. Google already solved all of it, and verified the email for us.

## Why not a third-party managed auth platform (Clerk/Auth0/Supabase Auth)?

Those platforms solve the same problem but introduce a token-trust boundary in the browser
and a paid external dependency for the most sensitive part of the app. Spring Security's
built-in OAuth2 client talks to Google directly and still lets the backend remain the sole
session authority — no extra vendor, no tokens in the browser. If a future requirement (SSO,
enterprise, mobile app) needs it, evaluate it then via a new ADR.

## Why not JWT?

JWTs would require:

- access/refresh token lifecycle;
- rotation/revocation strategy;
- storage decisions;
- more complex browser security;
- additional CSRF considerations if stored in cookies.

The MVP does not need those trade-offs.

If a future native/mobile/public API requires stateless authentication, create an ADR before
introducing JWT.

## Known trade-off

Users without a Google account cannot register. This is an accepted product decision for the
MVP, not an oversight — see [`../decisions/ADR-005-authentication.md`](../decisions/ADR-005-authentication.md).
If this becomes a real barrier (support requests, drop-off data), add email/password or another
provider behind the same session model, via a new ADR — do not silently bolt it on.

## Deployment implication

Frontend and backend may be on different origins.

The backend must therefore have explicit:

- allowed origins;
- credentialed CORS;
- CSRF configuration;
- cookie configuration;
- a registered OAuth2 redirect URI per environment (local/staging/production) in the Google
  Cloud Console — see [`../../docs/deployment/local.md`](../../docs/deployment/local.md).

Keep these values environment-specific.
