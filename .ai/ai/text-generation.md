# Text Generation

Text generation produces:

- title
- description
- tags
- keywords
- category

Use structured JSON/schema output where supported.

Prompt layers:

1. system/global rules
2. platform rules
3. creator style
4. task
5. observed model information
6. output schema

The model must not invent technical facts.

Current MVP flow: an authenticated user generates a general listing draft from a project's
private image. The provider receives the image and versioned prompt v1; the response uses a
structured schema and is validated before persistence. Supported languages are English and
Spanish. Platform-specific limits and publishing fields are future work.

The default model is `gemini-3.5-flash-lite`, configurable with `AI_MODEL_TEXT`. The
`GEMINI_API_KEY` belongs only on the server. The provider call has explicit timeouts and no
automatic retry. The output is a draft that the creator must review for factual accuracy.
