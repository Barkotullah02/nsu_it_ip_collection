# Records App (Spring Boot + PostgreSQL + Thymeleaf)

Starter project initialized with:
- Spring Boot 3
- PostgreSQL with Spring Data JPA
- Thymeleaf UI
- Spring Security (form login)
- CSRF protection enabled
- Role-based restrictions (`USER`, `ADMIN`)

## Authentication and Roles

On first run, two users are seeded:
- `admin` / `admin123` with roles `ADMIN`, `USER`
- `user` / `user123` with role `USER`

Access model:
- `/records/**`: `USER` and `ADMIN`
- `/admin/**`: `ADMIN` only
- Create/Edit/Delete record actions: `ADMIN` only

## Database Setup

1. Create PostgreSQL database:

```sql
CREATE DATABASE recordsdb;
```

2. Update credentials in `src/main/resources/application.properties` if needed:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

## Run the App

```bash
mvn spring-boot:run
```

Open: `http://localhost:8080`