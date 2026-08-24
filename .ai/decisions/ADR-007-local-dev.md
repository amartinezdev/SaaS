# ADR-007 — Dockerized Local Infrastructure

## Status

Accepted

## Decision

Use Docker Compose for local PostgreSQL and MinIO.

## Reason

A new developer should be able to clone the repository and start required infrastructure without
installing PostgreSQL or an S3-compatible server directly.

## Consequences

- Docker Desktop is a local prerequisite.
- `.env.example` documents required local variables.
- Production infrastructure is not copied blindly into local development.
