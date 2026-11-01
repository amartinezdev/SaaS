# ADR-007 — Dockerized Local Infrastructure

## Status

Accepted

## Decision

Use Docker Compose for local PostgreSQL and S3Mock. S3Mock pre-creates the `listing-ai`
bucket and stores development objects in a Docker volume. Production still uses an
S3-compatible object store such as Cloudflare R2.

The same Compose file also builds and starts the application for an integrated local preview.
Preview OAuth credentials allow the public home page to load; real Google credentials are
required to sign in.

MinIO was originally selected, but its public Docker image is no longer pullable on a
clean host. S3Mock is sufficient for local upload flow development.

## Reason

A new developer should be able to clone the repository and start required infrastructure without
installing PostgreSQL or an S3-compatible server directly.

## Consequences

- Docker Desktop is a local prerequisite.
- `.env.example` documents required local variables.
- Production infrastructure is not copied blindly into local development.
