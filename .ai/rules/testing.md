# Testing Rules

## Required

Important business logic must have tests.

Priorities:

1. credits
2. plan entitlements
3. generation lifecycle
4. ownership/security
5. billing webhook idempotency
6. AI output validation

## Test layers

- unit tests for pure/domain logic;
- service/application tests;
- repository integration tests;
- controller/API tests;
- security tests.

## External providers

Use test doubles for most unit tests.

Do not spend real AI credits in ordinary CI.

Have explicit opt-in integration tests for provider APIs.
