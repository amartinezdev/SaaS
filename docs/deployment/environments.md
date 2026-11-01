# Environments

## Local

Run:

```bash
docker compose up -d postgres s3mock
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Backend:

```bash
cd backend
mvn spring-boot:run
```

Windows PowerShell can use:

```powershell
mvn spring-boot:run
```

## Staging

- Railway Spring Boot application (includes static frontend)
- Railway PostgreSQL
- test-mode Stripe
- staging storage bucket

## Production

- Railway Spring Boot application (includes static frontend)
- dedicated Railway PostgreSQL
- live Stripe
- production storage bucket

Never point staging at production PostgreSQL or production storage.
