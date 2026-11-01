# Local Setup

## Requirements

- JDK 21
- Node.js 22+
- Maven 3.9+
- Docker Desktop
- Git

## 1. Environment

Copy:

```text
.env.example -> .env
```

Keep `.env` local and uncommitted.

## 2. Start the application

```bash
docker compose up -d --build
```

Open `http://localhost:8080`. Check `docker compose ps` for the `app`, `postgres`, and
`s3mock` services. If the page is unavailable, run `docker compose logs app --tail 80`.
The public health check is `http://localhost:8080/api/health`.

PostgreSQL:

```text
localhost:5433
database: listing_ai
user: listing_ai
password: change-me
```

S3Mock:

```text
API: http://localhost:9090
```

The `listing-ai` bucket is created automatically. S3Mock is for local development only.
The local PostgreSQL host port is 5433 to avoid conflicts with an existing PostgreSQL
installation on the default port; the container still listens on 5432 internally.

## 3. Google OAuth2 credentials

Login only works through Google — see `.ai/context/authentication.md`.

1. Create (or select) a project in the [Google Auth Platform](https://console.cloud.google.com/auth/overview).
2. Complete the app's branding/consent setup. Under **Audience**, choose **External** and
   add the Google account you will use under **Test users** while the app is in testing.
3. Under **Clients**, create an OAuth client of type **Web application**.
4. Add this exact **Authorized redirect URI** (including the path):
   ```text
   http://localhost:8080/login/oauth2/code/google
   ```
5. Copy the client ID and secret into the repository-root `.env` as
   `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`. Keep the file private.
6. Recreate the app container so it reads the changed values:
   ```bash
   docker compose up -d --force-recreate app
   ```

Without these values, the home page still loads with local preview credentials, but Google
login will fail. There is no password fallback.

## 4. Alternative: run backend outside Docker

```bash
cd backend
mvn spring-boot:run
```

Backend:

```text
http://localhost:8080
```

## 5. Start the optional frontend development server

Copy `frontend/.env.example` to `frontend/.env.local`. The separate development server
needs `ALLOWED_ORIGINS=http://localhost:3000` in the backend environment. The `.env` file
is a template; load it into the backend process environment using your usual local tool.

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
the backend root URL (`http://localhost:8080/`) with a session cookie set.

For an integrated local build, use `docker build` from the repository root when Docker
Desktop is running, then start the image with PostgreSQL reachable from the container.

The frontend must send subsequent API requests with credentials included:

```ts
fetch(url, {
  credentials: "include"
});
```

CSRF bootstrap (`GET /api/auth/csrf`) is required before state-changing requests — see
`.ai/context/authentication.md`.

## 7. Generate a listing (optional)

Create a [Gemini API key](https://ai.google.dev/gemini-api/docs/api-key) in Google AI Studio.
This key is separate from the Google OAuth client ID and secret used for sign-in. Add it to
the repository-root `.env` as `GEMINI_API_KEY`, then run
`docker compose up -d --force-recreate app`. The server uses the configurable
`AI_MODEL_TEXT` (default `gemini-3.5-flash-lite`). Upload a project image and select
**Generate listing** in the dashboard. The API key is never sent to the browser.
