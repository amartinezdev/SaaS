# Credits

Credits represent entitlement to cost-bearing operations.

## Rules

- Backend is authoritative.
- Client cannot choose how many credits to deduct.
- Every debit must be auditable.
- Failed operations follow an explicit refund policy.
- Avoid race conditions during debit.

## Implemented model

Maintain a ledger:

```text
CreditTransaction
- id
- userId
- type
- amount (signed ledger delta)
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

The balance is derived from committed ledger rows. A per-user database lock serializes credit
changes before checking the balance, so concurrent debits cannot overspend. The pair of user ID
and idempotency key is unique; reusing a key with different operation details is rejected.
Grants and refunds are positive; debits are negative; adjustments may have either sign.
The balance cannot become negative.

New FREE accounts receive one initial trial credit by default, configurable with
`INITIAL_FREE_CREDITS`. Existing accounts at migration V4 receive one credit. Paid plan
allocations and renewal rules are pending the pricing and Stripe work; no paid credits are
granted yet. The API exposes the current plan and balance without allowing client-side changes.

An MVP text listing generation costs one credit. The debit is committed before the provider
call, with an idempotency key tied to the request. A failed generation writes a refund with
its own stable key. Failed attempts still count toward the daily safety limit because a
provider may charge for an attempted call.
