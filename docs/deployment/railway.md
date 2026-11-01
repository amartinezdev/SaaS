# Railway Deployment

## Services

Each environment has:

- `backend`
- `postgres`

The application service includes the frontend static export and connects to PostgreSQL over
Railway's private network.

Railway builds the root Dockerfile from GitHub. Keep the build context at the repository root;
using `/backend` as the root directory would exclude the frontend build.

## Initial setup

1. Create a Railway project.
2. Create a `staging` environment.
3. Add PostgreSQL.
4. Add the application from GitHub using the root Dockerfile.
5. Configure application environment variables, including `GOOGLE_CLIENT_ID`/`GOOGLE_CLIENT_SECRET`,
   `SESSION_COOKIE_SECURE=true`, and `SESSION_COOKIE_SAME_SITE=Lax`. Do not allow a development
   origin in `ALLOWED_ORIGINS`.
   Use Railway references from the PostgreSQL service (replace `Postgres` with its actual name):

   ```text
   DATABASE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
   DATABASE_USERNAME=${{Postgres.PGUSER}}
   DATABASE_PASSWORD=${{Postgres.PGPASSWORD}}
   ```

   Do not pass Railway's `postgresql://` value directly as `DATABASE_URL`: Spring expects
   the `jdbc:postgresql://` format. Do not set `NEXT_PUBLIC_API_BASE_URL` in the build;
   the exported frontend uses relative API paths.
6. Add the environment's redirect URI to the Google OAuth2 Client in Google Cloud Console:
   `https://<environment>.<domain>/login/oauth2/code/google`
   (see `.ai/context/authentication.md`). Login will fail until this is registered.
7. Confirm Flyway migrations run when the application starts.
8. Generate/attach one staging application domain for the frontend and API.
9. Repeat for production with a separate PostgreSQL service.

## Important

Do not expose PostgreSQL publicly unless there is a concrete need.

The application should access PostgreSQL through Railway's private/service networking.

## Deployment

GitHub push can trigger deployment.

For manual deployment, Railway supports:

```bash
railway link
railway up
```

Production deployments should be tied to reviewed commits.
