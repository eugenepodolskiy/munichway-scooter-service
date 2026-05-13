# MunichWay

A backend service for an electric scooter rental platform in Munich. Built with Spring Boot, PostGIS, and JWT authentication.

## Features

- **Scooter discovery** — find available scooters within 500m of your GPS coordinates using PostGIS spatial queries
- **Rental lifecycle** — rent and return scooters with automatic trip cost calculation (unlock fee + per-minute rate)
- **Secure API** — stateless JWT authentication with role-based access control (`ROLE_USER`, `ROLE_ADMIN`)
- **Real-time billing** — a scheduled job deducts balance every minute and force-ends trips when users exceed their debt limit
- **Scooter maintenance** — a scheduled job auto-recharges available scooters with low battery
- **Soft deletes** — users and scooters are never hard-deleted from the database
- **Paginated scooter listing** — browse all scooters with page/size query parameters

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 23 |
| Framework | Spring Boot 4.0.2 |
| Database | PostgreSQL 15.8 + PostGIS 3.4 |
| ORM | Spring Data JPA / Hibernate Spatial 7.2 |
| Auth | Spring Security + JWT (jjwt 0.11.5) |
| Migrations | Flyway |
| Mapping | MapStruct 1.5.5 |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Infrastructure | Docker & Docker Compose |
| Testing | JUnit 5, Mockito, AssertJ |

## Getting Started

### Prerequisites

- Java 23
- Docker & Docker Compose
- Maven (or use the included `./mvnw` wrapper)

### Run the database

```bash
docker compose up -d
```

This starts a PostGIS-enabled PostgreSQL instance on port `5432`. Flyway will apply all migrations automatically on app startup.

### Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui.html`

### Run the tests

```bash
./mvnw test
```

## API Overview

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/users/register` | Public | Register and receive a JWT |
| `POST` | `/api/users/login` | Public | Login and receive a JWT |

### Users

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/users/me/trips` | User | Get your trip history |
| `POST` | `/api/users/me/top-up` | User | Top up your balance |
| `DELETE` | `/api/users/{id}` | Admin | Soft-delete a user |

### Scooters

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/scooters` | User | List all scooters (paginated) |
| `GET` | `/api/scooters/available?lat=&lon=` | User | Find available scooters within 500m |
| `POST` | `/api/scooters/{id}/rent` | User | Rent a scooter |
| `POST` | `/api/scooters/{id}/return` | User | Return a scooter with new GPS coords and battery level |
| `POST` | `/api/scooters/create` | Admin | Add a new scooter |

## Billing

| Setting | Default |
|---|---|
| Unlock fee | €1.00 |
| Per-minute rate | €0.10 |
| Minimum balance to rent | €5.00 |
| Max debt limit (auto-end trip) | −€5.00 |

New users start with a **€100 balance**.

## Seed Data

Two users are seeded via Flyway for local development:

| Email | Password | Role |
|---|---|---|
| `admin@munichway.com` | `admin123` | ROLE_ADMIN |
| `user@test.com` | `password` | ROLE_USER |

Several scooters are also seeded with real Munich GPS coordinates.

## Database Migrations

Migrations live in `src/main/resources/db/migration/` and run in order:

- `V1` — initial schema (users, scooters, trips)
- `V2` — soft-delete column on scooters
- `V3` — seed users and scooters
- `V4` — drop text location, add PostGIS `geometry(Point, 4326)` column
- `V5` — seed scooters with real GPS coordinates

## Configuration

All settings are in `src/main/resources/application.yml`. The JWT secret and billing rates can be overridden via environment variables in production.
