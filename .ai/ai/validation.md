# AI Output Validation

Validation occurs in layers.

## Schema

Required fields and types.

## Business

- title limits
- tag count
- duplicate tags
- forbidden terms
- language
- unsupported claims

## Platform

Apply platform-specific constraints.

## Correction

If invalid:

```text
generate
 ↓
validate
 ↓
correct
 ↓
validate
```

Maximum retry count must be finite.

If validation remains unsuccessful, return a controlled error rather than silently producing bad data.
