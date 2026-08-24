.PHONY: dev infra-down test

dev:
	docker compose up -d postgres minio

infra-down:
	docker compose down

test:
	cd backend && ./mvnw test
