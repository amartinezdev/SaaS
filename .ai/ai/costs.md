# AI Cost Management

Track:

- provider
- model
- operation
- input usage
- output usage
- image resolution
- estimated cost
- user plan
- credits consumed

## Principle

Do not optimize for zero AI cost. Optimize for healthy gross margin and reliability.

## Free plan

Use a low-cost/free provider only after confirming:

- commercial license;
- hosting feasibility;
- GPU cost;
- quality;
- rate limits;
- abuse resistance.

## Paid plans

Creator and Pro use paid image providers according to plan entitlements.

Provider model IDs and pricing assumptions must be configurable and revalidated against official
provider documentation before production pricing changes.

The MVP listing flow reserves one credit before calling Gemini and refunds it if generation
fails. It limits each user to five attempts per UTC day by default (`AI_LISTING_DAILY_LIMIT`).
Each generation stores the provider, model, prompt version, reported input/output tokens,
and an estimated USD cost. The default estimate uses the standard paid-tier rates for
Gemini 3.5 Flash-Lite on 2026-11-01: $0.30 per million input tokens and $2.50 per million
output tokens. Configure `AI_TEXT_INPUT_USD_PER_MILLION` and
`AI_TEXT_OUTPUT_USD_PER_MILLION` if rates change. These are estimates, not invoices.
Source: https://ai.google.dev/gemini-api/docs/pricing
