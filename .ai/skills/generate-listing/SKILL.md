# Skill: Generate Listing

## Purpose

Implement or modify the AI listing-generation workflow.

## Read first

- `.ai/ai/overview.md`
- `.ai/ai/text-generation.md`
- `.ai/ai/validation.md`
- `.ai/domain/listings.md`
- `.ai/domain/generations.md`
- `.ai/domain/credits.md`
- `.ai/rules/backend.md`
- `.ai/rules/api.md`

## Procedure

1. Inspect existing listing-generation code.
2. Identify input/output DTOs.
3. Check provider abstraction.
4. Check prompt versioning.
5. Implement provider-independent application logic.
6. Validate structured output.
7. Apply business/platform rules.
8. Record generation metadata.
9. Test success/failure paths.
10. Update documentation if behavior changes.

## Do not

- call Gemini from controllers;
- parse arbitrary prose when structured output is available;
- bypass credit rules;
- invent model facts.
