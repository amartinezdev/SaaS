# Skill: Create Spring Module

## Purpose

Create a new backend domain module without breaking modular-monolith boundaries.

## Procedure

1. Identify domain concept.
2. Read relevant `.ai/domain/*.md`.
3. Define entities/value objects only if needed.
4. Define application service/use cases.
5. Define repository port or Spring repository as appropriate.
6. Define DTOs.
7. Define controller.
8. Add tests.
9. Check security/ownership.
10. Update API documentation.

## Avoid

- giant services;
- generic `utils` dumping grounds;
- cross-module database coupling without justification;
- unnecessary interfaces.
