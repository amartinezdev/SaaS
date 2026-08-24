# ADR-002 — AI Provider Abstraction

## Status

Accepted

## Decision

All AI providers are accessed through application ports/adapters.

## Reason

AI pricing, model quality and provider availability change quickly.

The business domain should not depend directly on a provider SDK.

## Consequence

Slightly more abstraction now, but provider replacement is significantly easier.
