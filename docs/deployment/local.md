# Local Setup

## Requirements

- JDK 21
- Node.js 22+
- Docker Desktop
- Git

## 1. Environment

Copy:

```text
.env.example -> .env
```

Keep `.env` local and uncommitted.

## 2. Start infrastructure

```bash
docker compose up -d postgres minio
```

PostgreSQL:

```text
localhost:5432
database: listing_ai
user: listing_ai
password: change-me
```

MinIO:

```text
API:     http://localhost:9000
Console: http://localhost:9001
```

Create the `listing-ai` bucket through the MinIO console during the first setup.

## 3. Google OAuth2 credentials

Login only works through Google — see `.ai/context/authentication.md`.

1. Create (or reuse) a project in the [Google Cloud Console](https://console.cloud.google.com/apis/credentials).
2. Create an OAuth 2.0 Client ID (Web application).
3. Add this authorized redirect URI:
   ```text
   http://localhost:8080/login/oauth2/code/google
   ```
4. Copy the client ID/secret into `.env` as `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET`.

Without this, `/oauth2/authorization/google` will fail — there is no password fallback.

## 4. Start backend

```bash
cd backend
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend:

```text
http://localhost:8080
```

## 5. Start frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:3000
```

## 6. Log in

Open `http://localhost:8080/oauth2/authorization/google` directly in the browser (this is a
redirect flow, not a `fetch` call) to sign in. Spring Security then redirects back to
`FRONTEND_BASE_URL` with a session cookie set.

The frontend must send subsequent API requests with credentials included:

```ts
fetch(url, {
  credentials: "include"
});
```

CSRF bootstrap (`GET /api/auth/csrf`) is required before state-changing requests — see
`.ai/context/authentication.md`.
