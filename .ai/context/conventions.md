# Coding Conventions

## General

- Prefer clear names over abbreviations.
- Keep functions focused.
- Avoid speculative abstractions.
- Prefer immutable DTOs/value objects where appropriate.
- Validate inputs at boundaries.
- Keep business rules out of controllers.

## Java

- Use package-by-feature rather than package-by-layer at the root.
- DTOs for API boundaries.
- Services/application use cases for business orchestration.
- Repositories should not contain business policy.
- Use transactions deliberately.
- Avoid exposing JPA entities directly as API responses.

## TypeScript

- Strict TypeScript.
- Avoid `any`.
- Keep API access centralized.
- Keep business logic out of presentational components.
- Prefer reusable components when duplication is meaningful.

## Naming

Use English for code identifiers.

Use domain language consistently.

## Comments

Comments should explain WHY, not restate WHAT the code does.
