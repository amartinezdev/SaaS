# Generations

A generation records an AI operation.

Suggested fields:

- id
- userId
- projectId
- type
- provider
- model
- status
- promptVersion
- estimatedCost
- creditsUsed
- startedAt
- completedAt
- errorCode/errorMessage where safe

Statuses:

PENDING
PROCESSING
COMPLETED
FAILED
CANCELLED

A failed generation must not accidentally consume a permanent credit unless explicitly defined.

The initial synchronous text flow records PROCESSING, COMPLETED or FAILED. Completed rows
store token usage, estimated USD cost and one consumed credit. Failed rows store a safe error
code, usage/cost when available, and zero net credits after the idempotent refund.
