# Subscriptions

Plans:

- FREE
- CREATOR
- PRO

Subscription state is synchronized from Stripe webhook events.

The frontend may display plan state but cannot establish entitlement.

Suggested subscription states:

- ACTIVE
- TRIALING
- PAST_DUE
- CANCELED
- INCOMPLETE

Plan entitlements must be centralized.
