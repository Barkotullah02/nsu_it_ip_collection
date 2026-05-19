# Records App Spec

## Purpose
Spring Boot CRUD application for managing records, users, departments, and IP address inventory for the NSU IT workflow.

## Runtime
- Spring Boot 3.3.5
- Java 21 in `pom.xml`
- PostgreSQL on `localhost:5432`
- Database: `recordsdb`
- Local DB user used by the app: `barkotullah`
- Thymeleaf views with Spring Security 6 extras
- JPA/Hibernate with `ddl-auto=update`

## Authentication And Roles
Three built-in users are seeded on first run:
- `superadmin / super123` with `SUPER_ADMIN`
- `editor / editor123` with `EDITOR`
- `viewer / viewer123` with `VIEWER`

Role intent:
- `SUPER_ADMIN`: full access, including admin dashboards, department CRUD, IP admin CRUD, and user management
- `EDITOR`: record management access
- `VIEWER`: read-only record access

Security rules:
- `/admin/**` requires `SUPER_ADMIN`
- `/users/**` requires `SUPER_ADMIN`
- `/records/**` allows `SUPER_ADMIN`, `EDITOR`, `VIEWER`
- `/api/**` is public
- `/login`, `/error`, and static assets are public

## Main Controllers

### `AuthController`
- `GET /login`
- Returns `login`

### `AdminController`
- `GET /admin`
- Returns `admin`
- Dashboard aggregates:
  - all users
  - user count
  - department count
  - total/assigned/free IP counts

### `DepartmentController`
- Base path: `/admin/departments`
- `GET /admin/departments` list departments
- `GET /admin/departments/new` create form
- `POST /admin/departments` create department
- `GET /admin/departments/{id}/edit` edit form
- `POST /admin/departments/{id}/edit` update department
- `POST /admin/departments/{id}/delete` delete department

### `AdminIpAddressController`
- Base path: `/admin/ip-addresses`
- `GET /admin/ip-addresses` list IPs
- `GET /admin/ip-addresses/new` create form
- `POST /admin/ip-addresses` create IP
- `GET /admin/ip-addresses/{id}/edit` edit form
- `POST /admin/ip-addresses/{id}/edit` update IP
- `POST /admin/ip-addresses/{id}/delete` delete IP

### `UserController`
- Base path: `/users`
- `GET /users` admin user overview
- `GET /users/new` create user form
- `POST /users` create user
- `GET /users/{id}/edit` edit user form
- `POST /users/{id}/edit` update user
- `POST /users/{id}/toggle` toggle active status
- `POST /users/{id}/delete` delete user

### `IpAddressController`
- Base path: `/ip-addresses`
- Public-ish listing for authenticated users
- `GET /ip-addresses` list all IPs
- `GET /ip-addresses/new` super-admin create form
- `POST /ip-addresses` super-admin create
- `POST /ip-addresses/{id}/delete` super-admin delete

### `RecordController`
- Base path: `/records`
- Handles list, new, create, details, edit, delete, free, assign, history, and PDF export routes

### `ImportController`
- Handles CSV import workflow
- Routes include `/import`, `/import/template`, `/import/process`, `/import/progress`, `/import/results`

### `ApiController`
- `/api/ip-addresses`
- `/api/departments`
- `/api/free-ip-addresses`

## Core Services
- `UserService` wraps user CRUD and password handling
- `DepartmentService` wraps department CRUD and name uniqueness checks
- `IpAddressService` wraps IP CRUD and assigned/free counts
- `RecordService` handles records and IP allocation workflows

## Data Seeding
`DataInitializer` seeds only when tables are empty.

Seeded departments:
- Department of ECE
- Department of CSE
- Department of EEE
- Department of CE
- Department of ICE
- Department of IT
- Administration
- Library
- IT Department

Seeded IP pool:
- 15 assigned sample IPs
- 3 free sample IPs

Seeded sample records connect departments to IPs and staff metadata.

## Templates

### Main pages
- `src/main/resources/templates/login.html` login screen
- `src/main/resources/templates/admin.html` admin dashboard
- `src/main/resources/templates/import.html` import workflow
- `src/main/resources/templates/error.html` error page

### Department admin UI
- `src/main/resources/templates/admin/departments/list.html`
- `src/main/resources/templates/admin/departments/form.html`

### Admin IP UI
- `src/main/resources/templates/admin/ip-addresses/list.html`
- `src/main/resources/templates/admin/ip-addresses/form.html`

### General IP UI
- `src/main/resources/templates/ip-addresses/list.html`
- `src/main/resources/templates/ip-addresses/form.html`

### User UI
- `src/main/resources/templates/users/form.html`

### Record UI
- `src/main/resources/templates/records/*` for list, form, details, assign, history, and PDF views

### Shared fragment
- `src/main/resources/templates/fragments/admin-shell.html`
- Provides the reorganized admin header and shared navigation

### Layout note
- `src/main/resources/templates/layout.html` exists but is currently unused by the current template set

## Current UI State
- Admin dashboard and admin CRUD screens now use a shared left-rail workspace instead of a centered portal
- Department list shows edit and delete actions inline with each department row
- Department form uses a two-column editor with guidance text on the left and the form on the right
- Admin IP pages now use the same organized workspace and subdued styling

## Operational Notes
- The app now starts successfully against the local PostgreSQL instance using `barkotullah`
- `spring.jpa.database-platform` is redundant for modern Hibernate/PostgreSQL and can be removed later if desired
- The general `/ip-addresses` templates still keep the older visual language and can be aligned later if you want a fully unified design

## Quick Start
1. Start PostgreSQL locally.
2. Run `mvn spring-boot:run` from the project root.
3. Open `http://localhost:8080/login`.
4. Log in with `superadmin / super123` for full admin access.

## Admin Workflow Summary
- Dashboard: `/admin`
- Departments: `/admin/departments`
- Add department: `/admin/departments/new`
- Edit department: `/admin/departments/{id}/edit`
- Delete department: `/admin/departments/{id}/delete`
- IP admin: `/admin/ip-addresses`
- Add IP: `/admin/ip-addresses/new`
- Users: `/users`
- Records: `/records`

## Layout Notes
- The left navigation rail is defined in `src/main/resources/templates/fragments/admin-shell.html`
- Admin content panels are intentionally wide and left-aligned to avoid the centered-card feel
- Department and IP action buttons remain visible in their tables to keep CRUD discoverable
