# AI Architecture

AI is a product subsystem, not a collection of direct API calls.

## Pipeline

```text
Input
 ↓
Preprocessing
 ↓
Prompt/skill construction
 ↓
Provider selection
 ↓
Provider call
 ↓
Structured result
 ↓
Validation
 ↓
Business validation
 ↓
Persistence
 ↓
User response
```

## Goals

- predictable output;
- provider replaceability;
- cost visibility;
- prompt versioning;
- safe handling of untrusted input;
- plan-based provider selection.

## Provider tiers

Free:
- free/open-source image model once infrastructure is validated.

Creator:
- Gemini 2.5 image generation.

Pro:
- Gemini 3 Pro image generation.

Model IDs must be configurable.
