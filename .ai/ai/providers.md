# AI Providers

## Ports

```text
TextGenerationProvider
ImageGenerationProvider
VisionProvider
```

## Adapters

Examples:

```text
GeminiTextAdapter
GeminiCreatorImageAdapter
GeminiProImageAdapter
FreeImageAdapter
```

## Rules

- Keep SDK-specific code inside adapters.
- Do not leak provider-specific response types into domain code.
- Store provider/model metadata on generations.
- Make timeouts explicit.
- Handle provider rate limits.
- Handle provider content/policy errors.
- Never retry blindly on non-transient errors.
