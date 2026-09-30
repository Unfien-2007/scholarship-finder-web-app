# Scholarship Finder

A layered Spring Boot web app for students searching and matching scholarships, with a browser UI, JWT authentication, student applications, and administrator CRUD operations.

## How to Run

### Prerequisites

- Java 17 or newer
- Maven 3.9 or newer

Check the tools from PowerShell:

```powershell
java -version
mvn -version
```

If `mvn` is not recognized, close and reopen the VS Code integrated terminal after adding Maven's `bin` directory to `PATH`. On Windows, the directory looks like `C:\apache-maven\apache-maven-3.9.16\bin`.

### Start the application

From the project root:

```powershell
mvn spring-boot:run
```

Open the web app at [http://localhost:8080](http://localhost:8080). The application serves the browser UI and REST API from the same Spring Boot process. Students can open `/profile.html` after signing in to edit their details, review matches, and track applications.

To stop it, press `Ctrl+C` in the terminal.

If startup reports a missing class such as `ScholarshipRepository`, rebuild the generated classes once:

```powershell
mvn clean spring-boot:run
```

This clears stale files under `target/` before Spring Boot starts.

### Run tests

```powershell
mvn test
```

The default profile uses an in-memory H2 database, so seeded data resets whenever the application restarts.

## Demo Accounts

- Student: `student@example.com` / `student123`
- Admin: `admin@example.com` / `admin123`

Use the student account to search, apply, and try profile matching. New students can use **Create a student account** in the browser; signup collects their education level, GWA percentage, location, and income bracket, then signs them in automatically.

## API Examples

Public search:

```powershell
curl "http://localhost:8080/api/scholarships?educationLevel=UNDERGRADUATE&location=Manila"
```

Get an admin token:

```powershell
curl -X POST http://localhost:8080/api/auth/login `
	-H "Content-Type: application/json" `
	-d '{"email":"admin@example.com","password":"admin123"}'
```

Send the returned token as `Authorization: Bearer <token>` for `/api/admin/**` and `/api/me`.

## Main Endpoints

- `GET /api/scholarships?educationLevel=UNDERGRADUATE&location=Manila`
- `GET /api/scholarships/{id}`
- `POST /api/scholarships/matches` with a student profile
- `GET /api/scholarships/matches/me` (student's saved profile)
- `GET /api/me` and `PUT /api/me` (student profile)
- `POST /api/auth/signup` (creates a student account and returns a JWT)
- `POST /api/admin/scholarships` (admin only)
- `PUT /api/admin/scholarships/{id}` (admin only)
- `DELETE /api/admin/scholarships/{id}` (admin only)
- `POST /api/applications/{scholarshipId}` (student only)
- `GET /api/applications/me` (student only)
- `PATCH /api/applications/{applicationId}/status` (student owner only; `WITHDRAWN` or `RESULT_RECEIVED`)

The student profile page groups applications into available matches, pending applications, and received results. Students can withdraw pending applications or record a received result for their own applications; they cannot change provider/admin decisions represented by `APPROVED` and `REJECTED`.

## Database Configuration

H2 is the local default. To use PostgreSQL or MySQL, update the datasource properties in `src/main/resources/application.yml`, add the matching JDBC driver to `pom.xml`, and set `spring.jpa.hibernate.ddl-auto` according to your migration strategy.

## Philippines Scholarship Database Plan

The current MVP uses three relational tables:

- `users`: student/admin accounts and matching profile data such as GWA percentage, location, and income bracket.
- `scholarships`: provider, title, PHP summary amount, deadline, application period, education level, availability location, minimum GWA percentage, income bracket, full benefits text, and full requirements text.
- `applications`: the student-to-scholarship mapping with `PENDING`, `APPROVED`, or `REJECTED` status.

For Philippine coverage, store `location` as a controlled value such as `Nationwide`, a region, province, or city rather than embedding it in the scholarship title. The next production migration should normalize this into `locations` and `scholarship_locations` so one scholarship can cover multiple provinces or regions. A follow-up `scholarship_requirements` table can hold one requirement per row with a type such as `DOCUMENT`, `ACADEMIC`, `RESIDENCY`, or `FINANCIAL`; the current text field is intentionally simpler for the MVP and is shown from the clickable scholarship details view.

Use `source_url`, `application_url`, `last_verified_at`, and `active` before importing a large Philippine catalog. Those fields make it possible to link applicants to the official application page and hide expired or unverified listings without deleting historical applications.

## Architecture

`controller` handles HTTP and DTOs, `service` owns business rules and matching, `repository` owns persistence, and `model` contains the relational entities. Admin endpoints are protected by Spring Security and require a JWT with `ROLE_ADMIN`.
