# Database Rules

PostgreSQL is the source of truth for persistent application state.

## Rules

- Use migrations.
- Add indexes based on real query needs.
- Add foreign keys where ownership/integrity requires them.
- Use unique constraints for idempotency keys where appropriate.
- Do not store binary images/STLs in ordinary relational columns.
- Store timestamps consistently.
- Do not delete financial/audit records merely to simplify UI.

## Core entities

User
Subscription
Plan
Project
Listing
Generation
CreditTransaction
GeneratedImage
File
