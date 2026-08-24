# Backend Rules

## Spring Boot

Use Spring Boot as a modular monolith.

Controllers:

- validate/request-bind;
- authorize where appropriate;
- delegate;
- map responses.

Controllers must not contain complex business logic.

Services/application use cases:

- orchestrate domain behavior;
- enforce business rules;
- call ports.

Repositories:

- persistence concerns only.

Adapters:

- external provider integrations.

## Errors

Use consistent API error responses.

Do not leak provider secrets or internal stack traces.

## Transactions

Use transactions around operations that atomically change:

- credits;
- subscriptions;
- generation records;
- ownership.

## External calls

Do not make expensive external calls while holding database locks longer than necessary.
