# EVE Healthcare Booking API

A backend service for diagnostic test bookings and simulated payments, built as part of the **EVE Healthcare SDE Intern - Backend Engineering Assignment**.

## Project Overview

The application provides REST APIs for:

- User signup and login
- JWT-based authentication
- Diagnostic centre management
- Diagnostic test management
- Centre-test pricing
- Authenticated diagnostic test bookings
- Simulated payment processing
- Payment webhook processing
- Idempotent webhook handling
- Request validation
- Authorization and ownership checks
- Consistent API error responses

## Tech Stack

| Technology                  | Purpose                          |
| --------------------------- | -------------------------------- |
| Java 21                     | Backend programming language     |
| Spring Boot 4.1.1           | Backend framework                |
| Spring Web MVC              | REST APIs                        |
| Spring Data JPA / Hibernate | Persistence                      |
| Spring Security             | Authentication and authorization |
| JWT                         | Stateless authentication         |
| PostgreSQL                  | Relational database              |
| Maven                       | Build and dependency management  |
| Jakarta Bean Validation     | Request validation               |
| JUnit                       | Automated testing                |
| Mockito                     | Unit-test mocking                |

## Architecture

The application follows a layered backend architecture:

```text
Client / Postman
       |
       v
REST Controllers
       |
       v
Service Layer
       |
       v
Repository Layer
       |
       v
PostgreSQL Database
```

### Authentication Flow

```text

Client
  |
  | Login
  v
AuthController
  |
  v
AuthService
  |
  v
JWT Token
  |
  v
Protected API
  |
  v
JwtAuthenticationFilter
```

### Payment Flow

```text

Booking
   |
   v
Simulated Payment
   |
   +---- SUCCESS ---> Payment SUCCESS
   |                   Booking CONFIRMED
   |
   +---- FAILED ----> Payment FAILED
                       Booking FAILED
```

### Webhook Flow

```text

Payment Webhook
       |
       v
Check eventId
       |
       +---- Already processed ---> Return existing payment
       |
       +---- New event -----------> Process payment
```

## Database Design

The application uses PostgreSQL.

### Main Tables

```text

users
diagnostic_centre
diagnostic_test
centre_test
bookings
payments
```

### Entity Relationships

```text

User
 |
 | 1:N
 v
Booking
 |       \
 |        \
 v         v
Centre    DiagnosticTest

DiagnosticCentre
      |
      | 1:N
      v
CentreTest
      ^
      |
      | N:1
DiagnosticTest

Booking
   |
   | 1:1
   v
Payment
```

### User

Stores:

- ID
- Name
- Email
- Password
- Created timestamp

Passwords are stored using BCrypt hashing rather than plain text.

### Diagnostic Centre

Stores:

- Centre ID
- Centre name
- Location

### Diagnostic Test

Stores:

- Test ID
- Test name

### CentreTest

`CentreTest` represents the relationship between a diagnostic centre and a diagnostic test and stores the price for that combination.

This allows the same diagnostic test to have different prices at different centres.

### Booking

A booking stores:

- User
- Diagnostic centre
- Diagnostic test
- Appointment date/time
- Amount
- Booking status

Supported booking statuses:

```text
PENDING
CONFIRMED
FAILED
CANCELLED
```

### Payment

A payment stores:

- Booking
- Amount
- Payment status
- Transaction ID
- Webhook event ID
- Creation timestamp

Supported payment statuses:

```text
SUCCESS
FAILED
```

The database enforces one payment per booking through a unique booking relationship.

## Authentication

The application uses JWT-based authentication.

### Signup

```http
POST /api/auth/signup
```

Example request:

```json
{
  "name": "Test User",
  "email": "testuser@gmail.com",
  "password": "Test@123"
}
```

### Login

```http
POST /api/auth/login
```

Example request:

```json
{
  "email": "testuser@gmail.com",
  "password": "Test@123"
}
```

The login response contains a JWT token.

Protected endpoints require:

```http
Authorization: Bearer <JWT_TOKEN>
```

## API Endpoints

### Authentication

| Method | Endpoint           | Authentication |
| ------ | ------------------ | -------------- |
| POST   | `/api/auth/signup` | Public         |
| POST   | `/api/auth/login`  | Public         |

### Diagnostic Centres

#### Create Centre

```http
POST /api/centres
```

Example request:

```json
{
  "name": "EVE Diagnostic Centre",
  "location": "Gurugram"
}
```

#### Get Centres

```http
GET /api/centres
```

Requires authentication.

### Diagnostic Tests

#### Create Test

```http
POST /api/tests
```

Example request:

```json
{
  "name": "Complete Blood Count"
}
```

#### Get Tests

```http
GET /api/tests
```

Requires authentication.

### Centre-Test Pricing

#### Add Test to Centre

```http
POST /api/centre-tests?centreId=1&testId=1&price=500
```

This associates a diagnostic test with a centre and defines its price.

### Bookings

#### Create Booking

```http
POST /api/bookings
```

Requires JWT authentication.

Example request:

```json
{
  "centreId": 1,
  "testId": 1,
  "appointmentDateTime": "2026-10-05T10:30:00"
}
```

A new booking starts with:

```text
PENDING
```

The booking amount is taken from the configured centre-test price.

### Payments

#### Process Simulated Payment

```http
POST /api/payments?bookingId=1&success=true
```

Requires JWT authentication.

Successful payment:

```text
success=true
```

Results in:

```text
Payment: SUCCESS
Booking: CONFIRMED
```

Failed payment:

```text
success=false
```

Results in:

```text
Payment: FAILED
Booking: FAILED
```

Duplicate payments for the same booking are rejected.

### Payment Webhook

```http
POST /api/payments/webhook
```

Example request:

```json
{
  "eventId": "evt_final_001",
  "bookingId": 6,
  "status": "SUCCESS"
}
```

The webhook endpoint is used to simulate a payment provider callback.

#### Webhook Idempotency

Webhook events are identified using `eventId`.

If the same event is received multiple times:

```text
First Request
     |
     v
Payment Processed
     |
     v
eventId Stored

Same Event Again
     |
     v
Existing Payment Returned
     |
     v
No Duplicate Payment Created
```

A processed payment is not overwritten by another webhook event for the same booking.

## Validation and Error Handling

The application uses Jakarta Bean Validation for request validation.

Examples include:

- Required fields
- Valid email format
- Future appointment date/time
- Required webhook event ID
- Required booking ID
- Required payment status

Validation errors return HTTP `400 Bad Request`.

Example validation error:

```json
{
  "timestamp": "2026-09-29T07:05:21",
  "message": "name: must not be blank",
  "error": "Validation Error",
  "status": 400
}
```

Application errors are handled through a global exception handler.

Example:

```json
{
  "timestamp": "2026-09-29T07:06:21",
  "message": "Booking not found",
  "error": "Bad Request",
  "status": 400
}
```

## Authorization

Payment operations verify that the authenticated user owns the booking.

If a user attempts to access another user's booking through the payment endpoint, the request is rejected.

Example:

```json
{
  "status": 400,
  "message": "You are not authorized to access this booking",
  "error": "Bad Request"
}
```

## Edge Cases Covered

The backend handles:

- Invalid request payloads
- Missing required fields
- Invalid email format
- Appointment dates in the past
- Invalid booking IDs
- Failed payments
- Duplicate payments
- Unauthorized payment access
- Repeated webhook events
- Duplicate webhook processing
- Payment state updates through webhooks
- Missing diagnostic centre
- Missing diagnostic test
- Test unavailable at selected centre

## Testing

The project includes unit tests using JUnit and Mockito.

Current automated tests cover:

1. Successful payment confirms the booking.
2. Failed payment marks the booking as failed.
3. Duplicate payment attempts are rejected.
4. Unauthorized payment attempts are rejected.
5. Duplicate webhook events return the existing payment.

Run all tests with:

```bash
mvn clean test
```

Expected result:

```text
BUILD SUCCESS
```

## Running Locally

### Prerequisites

Install:

- Java 21
- Maven
- PostgreSQL

### 1. Clone the Repository

```bash
git clone <https://github.com/farhan2804/eve_healthcare_booking_api>
cd eve-healthcare-booking-api
```

### 2. Create PostgreSQL Database

Create a PostgreSQL database named:

```text
eve_healthcare_db
```

### 3. Configure Database

Update:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/eve_healthcare_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_POSTGRES_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Do not commit real database passwords or secrets to the repository.

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## Example End-to-End Flow

```text
1. Signup
      ↓
2. Login
      ↓
3. Copy JWT
      ↓
4. Create diagnostic centre
      ↓
5. Create diagnostic test
      ↓
6. Add test to centre with price
      ↓
7. Create booking
      ↓
8. Process simulated payment
      ↓
9. Process payment webhook
      ↓
10. Send duplicate webhook
      ↓
11. Verify no duplicate payment is created
```

## Design Decisions

### Centre-Specific Pricing

The price is stored in `CentreTest` rather than directly in `DiagnosticTest`.

Reason:

A diagnostic test may have different prices at different centres.

Example:

```text
CBC
 |
 +---- Centre A ---> ₹500
 |
 +---- Centre B ---> ₹650
```

### Booking Amount Snapshot

The booking stores the amount at the time of booking.

This prevents a future price change from modifying the amount of an existing booking.

### Webhook Idempotency

`webhookEventId` is stored in the payment table and is unique.

When a webhook arrives, the service first checks whether the event has already been processed.

This prevents duplicate payment creation when the same webhook is delivered multiple times.

### One Payment Per Booking

A unique relationship between `Payment` and `Booking` prevents multiple payment records from being created for the same booking.

## Assumptions

- PostgreSQL is used as the primary database.
- A diagnostic test can have different prices at different diagnostic centres.
- Booking amount is captured at booking time.
- Each booking has at most one payment.
- Payment processing is simulated and does not use a real payment gateway.
- `eventId` is used as the webhook idempotency key.
- A processed payment is not overwritten by a later webhook event for the same booking.
- The webhook endpoint is publicly accessible in this simulated environment because it represents an external payment-provider callback.
- JWT authentication is used for protected application APIs.

## What I Would Improve With More Time

With additional development time:

- Redis for caching
- Background processing
- Docker and Docker Compose
- Swagger / OpenAPI documentation
- Pagination for list APIs
- Rate limiting
- More comprehensive integration tests
- Better structured/custom exception types
- Stronger webhook authentication/signature verification
- Production-ready environment and secret management
- More detailed logging and monitoring
- Additional database indexes and constraints
- More robust retry handling for webhook processing

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/evehealthcare/eve_healthcare_booking_api/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/evehealthcare/eve_healthcare_booking_api/
            └── service/
                └── PaymentServiceTest.java
```

## Project Status

The core backend requirements for the assignment have been implemented, including:

- JWT authentication
- Diagnostic centres and tests
- Centre-specific test pricing
- Diagnostic test booking
- Simulated payments
- Payment status handling
- Idempotent payment webhooks
- Request validation
- Error handling
- Authorization checks
- Automated backend tests

## Submission Checklist

- [x] Source code included
- [x] `pom.xml` included
- [x] `README.md` included
- [x] Unit tests included
- [x] JWT authentication implemented
- [x] Diagnostic centres implemented
- [x] Diagnostic tests implemented
- [x] Centre-test pricing implemented
- [x] Booking API implemented
- [x] Simulated payment implemented
- [x] Payment webhook implemented
- [x] Webhook idempotency implemented
- [x] Validation implemented
- [x] Authorization checks implemented
- [x] Error handling implemented
- [x] Tests passing with `mvn clean test`

## Bonus Engineering Features

The following additional engineering features were implemented:

- **Swagger/OpenAPI** — Interactive API documentation and endpoint testing through Swagger UI.
- **Structured Logging** — Added structured INFO/WARN logs for authentication, bookings, centre-test operations, payments, and webhook processing.
- **Integration Tests** — Added API-level integration tests covering successful webhooks, webhook idempotency, and invalid booking handling.

### Swagger UI

After starting the application, Swagger UI is available at:

`http://localhost:8080/swagger-ui/index.html`

## Author

**Farhan Mahmood**

Built for the **EVE Healthcare SDE Intern - Backend Engineering Assignment**
