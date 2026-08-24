# ADR-003 — Object Storage for Binary Assets

## Status

Accepted

## Decision

Store images and future STL files in object storage, not PostgreSQL.

## Reason

Binary assets can grow rapidly and have different lifecycle/serving characteristics than relational data.

PostgreSQL stores metadata and ownership.
