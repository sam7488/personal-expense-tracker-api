# Personal Expense Tracker API

A REST API built with Spring Boot for managing personal expenses, users, and roles.

This is a practice project where I applied what I learned about Spring Boot, JPA, Spring Security, JWT authentication, validation, exception handling, and testing.

## Features

- User registration and login
- JWT-based authentication
- Role-based authorization (`USER` and `ADMIN`)
- Create, read, update, and delete expenses
- Each user can only access their own expenses
- Filter expenses by category and date range
- Calculate total expenses
- Generate expense summaries by category
- User and role management
- Validation and global exception handling

## Project Structure

```text
config/         → Security configuration and data initialization
controller/     → REST API endpoints
dto/            → Request and response objects
entity/         → JPA entities and security user details
exception/      → Custom exceptions and global exception handling
mapper/         → Entity ↔ DTO mapping
repository/     → Database access
service/        → Business logic and authentication
```

## Main API

```text
POST   /auth/login

POST   /api/users/signup

GET    /api/expenses
POST   /api/expenses
GET    /api/expenses/{id}
PUT    /api/expenses
DELETE /api/expenses

GET    /api/expenses/total
GET    /api/expenses/summary

GET    /api/roles
POST   /api/roles
```

## Authentication

After logging in, the API returns a JWT access token.

The token is sent with protected requests:

```text
Authorization: Bearer <access-token>
```

Normal users can manage their own expenses, while admin users have access to user and role management.