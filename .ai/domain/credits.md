# Credits

Credits represent entitlement to cost-bearing operations.

## Rules

- Backend is authoritative.
- Client cannot choose how many credits to deduct.
- Every debit must be auditable.
- Failed operations follow an explicit refund policy.
- Avoid race conditions during debit.

## Suggested model

Maintain a ledger:

```text
CreditTransaction
- id
- userId
- generationId
- type
- amount
- reason
- idempotencyKey
- createdAt
```

Types:

GRANT
DEBIT
REFUND
ADJUSTMENT

Balance can be derived or maintained with transactional safeguards.
