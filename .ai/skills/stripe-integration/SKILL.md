# Skill: Stripe Integration

## Read first

- `.ai/domain/subscriptions.md`
- `.ai/domain/credits.md`
- `.ai/rules/security.md`
- `.ai/rules/api.md`

## Procedure

1. Define the entitlement model.
2. Create checkout session through backend.
3. Never trust frontend payment state.
4. Verify webhook signatures.
5. Make webhook handling idempotent.
6. Update subscription state transactionally.
7. Test duplicate/out-of-order webhook scenarios.

## Do not

- put Stripe secret keys in frontend code;
- activate paid entitlements based only on redirect success;
- process the same webhook twice.
