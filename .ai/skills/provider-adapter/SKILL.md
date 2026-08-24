# Skill: External Provider Adapter

## Purpose

Add or replace an external provider without leaking SDK details into the domain.

## Procedure

1. Define/confirm application port.
2. Keep provider SDK types inside adapter.
3. Map provider errors to internal error types.
4. Add timeout/retry policy.
5. Record provider/model metadata.
6. Add mocked tests.
7. Add opt-in integration test if necessary.
8. Document pricing/licensing assumptions.
