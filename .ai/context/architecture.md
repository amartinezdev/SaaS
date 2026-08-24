# Architecture

## MVP architecture

Use a modular monolith.

```text
Next.js
   |
 HTTPS
   |
Spring Boot API
   |
   +-- PostgreSQL
   +-- Object Storage
   +-- AI Provider Adapters
   +-- Stripe Adapter
```

## Frontend

Next.js + TypeScript + React + Tailwind CSS.

The browser calls the Spring Boot API directly in the MVP.

Authentication uses a Spring Security session cookie.

See `.ai/context/authentication.md`.

## Backend

Java + Spring Boot.

Suggested modules:

```text
auth
user
project
listing
generation
credits
subscription
billing
ai
storage
common
```

Each module should use only the layers it actually needs.

Preferred layering:

```text
controller
application/service
domain
repository
infrastructure/adapter
```

Do not create layers solely for ceremony.

## External integrations

Use ports/adapters:

```text
application -> port -> adapter -> external provider
```

Providers:

- Gemini text/image
- free/open-source image generation
- Stripe
- object storage

## Async work

MVP can perform short operations synchronously where practical.

Image generation may become asynchronous if provider latency requires it.

Do not add a queue until the need is demonstrated.

## Storage

PostgreSQL stores metadata.

Object storage stores:

- original uploads
- generated images
- future STL files

## Deployment

MVP target:

- Next.js: Vercel
- Spring Boot: Railway
- PostgreSQL: Railway
- Storage: Cloudflare R2/S3-compatible storage
- Stripe: hosted billing
- AI: external API + optional GPU worker later

See `.ai/context/deployment.md`.

## Scalability direction

If usage grows:

```text
API
 |
job queue
 |
workers
 |
GPU / AI providers
```

This is future architecture, not MVP architecture.
