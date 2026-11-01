# Tech Stack

## Frontend

- Next.js
- static export, bundled into the Spring Boot application for deployment
- TypeScript
- React
- Tailwind CSS

## Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Maven

## Database

- PostgreSQL

## Storage

- Cloudflare R2 or S3-compatible storage
- S3Mock locally

## Billing

- Stripe

## AI

- Gemini text/image providers
- free/open-source image provider for free tier, subject to licensing and infrastructure validation

## Local infrastructure

- Docker Compose
- PostgreSQL
- S3Mock

## CI/CD

Future target:

- GitHub Actions
- automated tests
- build
- deployment

The production build uses a root Dockerfile with Node.js and Maven build stages. Only the
Spring Boot runtime is deployed; there is no separate Next.js server.

## Rule

Do not add technology unless it solves a demonstrated problem.
