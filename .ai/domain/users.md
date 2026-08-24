# Users

A user owns projects and generated assets.

## Identity

Identity is established exclusively through Google OAuth2 login — see
[`../context/authentication.md`](../context/authentication.md) and
[`../decisions/ADR-005-authentication.md`](../decisions/ADR-005-authentication.md).

Required concepts:

- id (internal, ours — everything else in the domain references this, never the Google identity directly)
- `google_sub` — Google's stable, immutable subject identifier. **This is the identity key**, not email.
- email (from Google — display/contact use only, not an identity key)
- name (from Google, optional)
- plan/subscription relationship
- timestamps

There is no password field. The `User` entity never stores or hashes credentials.

## Why key by `google_sub` and not email

Email addresses on a Google account can change. `sub` cannot. Keying by email risks creating a
second `User` row for the same person if they ever change their Google account email, silently
splitting their projects, credits and subscription across two identities.

## Provisioning

On first login, find-or-create a `User` by `google_sub`. Do not create a new user per login.

Never trust a user ID supplied by the client when determining ownership. Derive authenticated
identity from the security context (the Spring Security session), never from a request body or
header.
