# Environments

## Local

Run:

```bash
docker compose up -d postgres minio
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
./mvnw spring-boot:run
```

Windows PowerShell can use:

```powershell
.\mvnw.cmd spring-boot:run
```

## Staging

- Vercel frontend
- Railway Spring Boot backend
- Railway PostgreSQL
- test-mode Stripe
- staging storage bucket

## Production

- Vercel frontend
- Railway Spring Boot backend
- dedicated Railway PostgreSQL
- live Stripe
- production storage bucket

Never point staging at production PostgreSQL or production storage.
