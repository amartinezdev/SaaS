# Frontend

Next.js static export + TypeScript + React. Production files are bundled into the Spring Boot
JAR by the root Dockerfile; there is no production Next.js server.

For local development, copy `.env.example` to `.env.local` and run `npm run dev` alongside
Spring Boot. The API base URL is needed only for this separate development origin.

Suggested structure:

```text
app/
components/
features/
lib/
hooks/
types/
```

Keep API access centralized and do not expose provider secrets.
