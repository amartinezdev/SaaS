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
