# Prompt System

Prompts are product assets and must be versioned.

Recommended structure:

```text
.ai/ai/prompts/
  listing/
    v1.md
  image/
    hero/
      v1.md
    lifestyle/
      v1.md
```

Every persisted generation should record:

- prompt version
- provider
- model

Do not put API keys or secrets in prompts.

Prompt changes that materially change output should receive a new version.
