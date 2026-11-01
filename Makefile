.PHONY: dev infra-down test

dev:
	docker compose up -d postgres s3mock

infra-down:
	docker compose down

test:
	cd backend && mvn test
