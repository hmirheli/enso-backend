# Enso Backend

REST API for **Enso**, an online shop for handmade ceramics and lamps.
Built with Java 21 and Spring Boot 3.

## Features

- **Passwordless login**: the customer enters a phone number, receives a 6-digit code by SMS
  ([Kavenegar](https://kavenegar.com)) and gets a JWT after verifying it.
  Codes are stored in Redis and expire after 5 minutes.
- **Catalog**: products and categories with images, slugs, search and soft delete.
- **Cart and orders**: one cart per user, orders created from the cart.
- **Online payment** with [Zarinpal](https://www.zarinpal.com). Payment providers sit behind a
  `PaymentGateway` interface, so another provider can be added without touching the order flow.
- **Image storage** in MinIO (S3-compatible).
- Role-based access, consistent error responses, request validation, OpenAPI documentation.

## Tech stack

| Area | Technology |
|---|---|
| Language / framework | Java 21, Spring Boot 3.2, Spring Web, Spring Security |
| Persistence | Spring Data JPA / Hibernate, PostgreSQL |
| Cache / short-lived data | Redis |
| File storage | MinIO |
| Auth | JWT (jjwt), OTP by SMS |
| Mapping / boilerplate | MapStruct, Lombok |
| API docs | springdoc-openapi (Swagger UI) |
| Build | Maven, Docker |

## Architecture

```
Controller  ->  Service  ->  Repository  ->  PostgreSQL
   |              |
   |              +--> RedisService          (OTP codes)
   |              +--> SmsService            (Kavenegar)
   |              +--> ImageStorageService   (MinIO)
   |              +--> PaymentGateway        (Zarinpal, chosen by PaymentGatewayResolver)
   |
   +-- DTOs (request / response), mapped with MapStruct
   +-- GlobalExceptionHandler  -> one error format for the whole API
```

Packages: `controller`, `service` (+ `impl`), `repository`, `entity`, `dto`, `mapper`,
`security`, `payment`, `config`, `exception`.

## Run with Docker

Requirements: Docker with Compose.

    cp .env.example .env     # then fill in the values
    docker compose up --build

The API is at http://localhost:8080 and the Swagger UI at
http://localhost:8080/swagger-ui.html. The MinIO console is at
http://localhost:9001.

Notes:

- Kavenegar and Zarinpal need your own credentials in `.env`.
- The HTTP client loads a PKCS12 truststore from the classpath (`TRUSTSTORE_PATH`).
  The file is git-ignored, so place it in `src/main/resources/certs/` before building.
- The official `minio/minio` image is no longer published on Docker Hub, so the compose
  file uses a community build (`coollabsio/minio`). Replace it before any production use.

## Run locally

Requirements: JDK 21, Maven, and running PostgreSQL, Redis and MinIO instances.

Create `src/main/resources/application.properties` (or use environment variables) with the
properties below, then run `mvn spring-boot:run`.

| Property | Meaning |
|---|---|
| `spring.datasource.url / username / password` | PostgreSQL connection |
| `spring.data.redis.host / port` | Redis connection |
| `app.jwt.secret` | HMAC key, at least 32 characters |
| `app.jwt.expiration` | Token lifetime in milliseconds |
| `minio.endpoint / access-key / secret-key / bucket-name` | MinIO connection and bucket |
| `kavenegar.api-key / otp-template` | SMS provider settings |
| `zarinpal.merchant-id / request-url / verify-url / payment-url / callback-url` | Payment gateway settings |
| `enso.frontend-url` | Where the customer is redirected after paying |

Swagger UI: <http://localhost:8080/swagger-ui.html>

## Design decisions

- **OTP in Redis, not in the database.** Expiry is built in and no cleanup job is needed.
- **Strategy pattern for payments.** `PaymentGatewayResolver` picks the gateway by `PaymentMethod`.
- **Idempotent payment verification.** A second callback for an already successful payment is a no-op.
- **MinIO cleanup after commit.** Removed images are deleted from storage only after the database
  transaction commits, so a rollback never leaves a product pointing at a missing file.

## Roadmap

- Atomic stock update to avoid overselling under concurrent orders
- Restore stock when an order is cancelled
- OTP rate limiting (resend cooldown, attempt limit)
- Unit tests (JUnit 5, Mockito) and integration tests with Testcontainers
- GitHub Actions pipeline
- Pagination for product lists
- Cancel unpaid orders automatically after a timeout
