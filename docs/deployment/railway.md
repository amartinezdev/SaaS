# Railway Deployment

## Services

Each environment has:

- `backend`
- `postgres`

The backend service references the PostgreSQL service's `DATABASE_URL`.

Railway can deploy Spring Boot from GitHub and automatically build the service.

## Initial setup

1. Create a Railway project.
2. Create a `staging` environment.
3. Add PostgreSQL.
4. Add backend from GitHub.
5. Configure backend environment variables, including `GOOGLE_CLIENT_ID`/`GOOGLE_CLIENT_SECRET`.
6. Add the environment's redirect URI to the Google OAuth2 Client in Google Cloud Console:
   `https://api-<environment>.<domain>/login/oauth2/code/google`
   (see `.ai/context/authentication.md`). Login will fail until this is registered.
7. Set pre-deploy migration command once Flyway is implemented.
8. Generate/attach the staging backend domain.
9. Repeat for production with a separate PostgreSQL service.

## Important

Do not expose PostgreSQL publicly unless there is a concrete need.

The backend should access PostgreSQL through Railway's private/service networking.

## Deployment

GitHub push can trigger deployment.

For manual deployment, Railway supports:

```bash
railway link
railway up
```

Production deployments should be tied to reviewed commits.
