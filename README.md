# Tennis Club Reservations

Spring Boot REST API for managing tennis club courts, court surfaces, users and reservations.

The application uses an in-memory H2 database, Liquibase migrations and optional startup data initialization.

## Requirements

- Java 17
- Maven Wrapper included in the project (`./mvnw`)

You do not need to install Maven manually.

## Run The Application

```bash
./mvnw spring-boot:run
```

The API runs on:

```text
http://localhost:8080
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

After a successful Swagger login request, the returned access token is automatically used as the Bearer token for secured endpoints.

The OpenAPI JSON specification is available at:

```text
http://localhost:8080/v3/api-docs
```

## Build And Test

Compile and run tests:

```bash
./mvnw test
```

Run full verification, including the JaCoCo coverage check:

```bash
./mvnw verify
```

The current coverage threshold is configured in `pom.xml` as 90% instruction coverage.

## Database

The default database is in-memory H2:

```properties
spring.datasource.url=jdbc:h2:mem:tennis-club;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
```

Schema migrations are handled by Liquibase:

```properties
spring.liquibase.change-log=classpath:db/changelog/app-changelog.xml
```

Because the database is in-memory, data is recreated on every application start.

## Data Initialization

Startup data is controlled by:

```properties
data.init.enabled=true
```

When enabled, the application creates default court surfaces and courts if they do not already exist:

- surfaces: `Hard`, `Clay`
- courts: numbers `1` to `4`
- admin user from `data.init.admin.*` properties

To disable startup data, set:

```properties
data.init.enabled=false
```

## API

Base path:

```text
/api
```

The `/api/auth/**` endpoints are public. Other `/api/**` endpoints require a valid JWT Bearer token.

Role-based authorization:

- `USER` can call read endpoints and create reservations
- `ADMIN` can call all endpoints
- insufficient permissions return `403 Forbidden`

### Authentication

```text
POST /api/auth/login
POST /api/auth/refresh
```

Login uses a JSON request body. The username is the user's phone number.

Successful login and refresh return a JWT access token and refresh token in the response body:

```json
{
  "accessToken": "<access-token>",
  "refreshToken": "<refresh-token>"
}
```

Login example:

```bash
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber":"+421900000000","password":"admin"}'
```

Refresh example:

```bash
curl -i -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refresh-token>"}'
```

Refresh tokens are accepted only by `/api/auth/refresh`. Secured API endpoints require an access token.

### Error responses

Error responses use one JSON shape across validation, authentication, authorization and application errors:

```json
{
  "timestamp": "2026-06-10T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/reservations",
  "fieldErrors": {
    "courtId": "must not be null"
  }
}
```

JWT configuration:

```properties
security.jwt.secret=change-this-secret-before-production-123456
security.jwt.access-token-expiration=PT15M
security.jwt.refresh-token-expiration=P7D
```

For secured endpoint examples, replace `<access-token>` with the token returned by `/api/auth/login`.

### Surfaces

```text
GET    /api/surfaces
GET    /api/surfaces/{id}
POST   /api/surfaces
PUT    /api/surfaces
DELETE /api/surfaces
DELETE /api/surfaces/{id}
```

Create surface example:

```bash
curl -X POST http://localhost:8080/api/surfaces \
  -H "Authorization: Bearer <access-token>" \
  -H "Content-Type: application/json" \
  -d '{"minutePrice":0.24,"name":"Hard"}'
```

### Courts

```text
GET    /api/courts
GET    /api/courts/{id}
GET    /api/courts/{number}/reservations
POST   /api/courts
PUT    /api/courts
DELETE /api/courts
DELETE /api/courts/{id}
```

Create court example:

```bash
curl -X POST http://localhost:8080/api/courts \
  -H "Authorization: Bearer <access-token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Center Court","number":10,"surfaceId":1}'
```

### Reservations

```text
GET    /api/reservations
GET    /api/reservations/{id}
POST   /api/reservations
PUT    /api/reservations
DELETE /api/reservations/{id}
```

Creating a reservation returns the created reservation id and calculated price:

```json
{
  "reservationId": 1,
  "price": 14.40
}
```

Create reservation example:

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer <access-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "from":"2026-07-01T10:00:00",
    "to":"2026-07-01T11:00:00",
    "gameType":"SINGLES",
    "user":{
      "name":"John Doe",
      "phoneNumber":"+421901234567"
    },
    "courtId":1
  }'
```

Supported game types:

```text
SINGLES
DOUBLES
```

### Users

There are no public CRUD endpoints for users. Users are created as part of reservation creation when the phone number does not already exist.

```text
GET /api/users/{phoneNumber}/reservations
GET /api/users/{phoneNumber}/reservations?future=true
```

## Pagination

List endpoints support Spring pageable query parameters:

```text
GET /api/courts?page=0&size=10
GET /api/reservations?page=0&size=10&sort=creationDate,desc
```

Paginated responses use a simplified shape:

```json
{
  "content": [],
  "page": {
    "number": 0,
    "size": 10,
    "numberOfElements": 0,
    "totalElements": 0,
    "totalPages": 0
  }
}
```

## Project Structure

```text
src/main/java/com/tennisclub/reservations
+-- auth
|   +-- dto
+-- config
+-- controller
+-- exception
+-- mapper
+-- model
|   +-- dto
|   |   +-- create
|   |   +-- update
|   +-- entity
+-- repository
|   +-- impl
+-- security
|   +-- annotation
+-- service
|   +-- impl
+-- util
+-- validator
|   +-- annotation
```

Other relevant files:

```text
src/main/resources
+-- application.properties
+-- db/changelog
    +-- app-changelog.xml
    +-- init.sql

class_diagram.puml
use_case_diagram.puml
```

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
