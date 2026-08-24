# Skill: Generate Image

## Purpose

Implement or modify AI promotional image generation.

## Read first

- `.ai/ai/overview.md`
- `.ai/ai/image-generation.md`
- `.ai/ai/providers.md`
- `.ai/ai/costs.md`
- `.ai/domain/images.md`
- `.ai/domain/generations.md`
- `.ai/domain/credits.md`
- `.ai/rules/security.md`

## Procedure

1. Determine plan/provider.
2. Validate entitlement and credits.
3. Create generation record.
4. Build versioned prompt.
5. Call provider through adapter.
6. Validate provider result.
7. Store image in object storage.
8. Persist metadata.
9. Finalize/refund credits according to policy.
10. Test failure and retry behavior.

## Cost

Record provider/model and estimated cost.
