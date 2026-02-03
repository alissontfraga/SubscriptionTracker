# Subscription Tracker

## 📌 Project Overview
**SubscriptionTracker** is a Spring Boot application for tracking user subscriptions.  
It provides authentication, role-based access (USER / ADMIN), subscription management, and database migrations using Flyway.

The project focuses on clean backend architecture, security, and real-world Spring Boot practices.

---

## 🚀 Features

### 👤 User Management
- Stateless authentication and authorization using **JWT (Bearer tokens)**
- Role-based access control (**USER / ADMIN**)
- User registration and login
- Secure access to protected resources
- User profile management


### 📦 Subscriptions
- Full CRUD operations
- Support for:
  - Prices
  - Currencies
  - Categories
  - Status
  - Billing frequency
- Users can only manage their own subscriptions

### 🛠️ Admin Tools
- Admin-only endpoints
- User and system data management
- Role-based authorization

### 🗄️ Database
- PostgreSQL
- Flyway migrations
- Automatic schema versioning on startup

---

## 🧰 Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot
- **Security:** Spring Security + JWT
- **Build Tool:** Maven 
- **Database:** PostgreSQL
- **Migrations:** Flyway
- **Testing:** JUnit, Mockito, MockMvc
- **Containerization:** Docker
- **API Documentation:** OpenAPI / Swagger


---

## ⚙️ Prerequisites

- **Docker** 
- **Docker Compose** 

---

## 🧪 Testing

The project includes both unit and integration tests to ensure reliability and correctness.

- **Unit tests** written with **JUnit** and **Mockito**, focusing on business logic and service layer isolation
- **Integration tests** wrriten with **MockMvc** covering controllers, security configuration, and persistence layer
- Validation of authorization rules (USER / ADMIN) and access to protected endpoints

--- 

🔒 **Authorization:**  
All routes (except auth) requires Bearer token

## 📌 API Documentation

### 🔐 Authentication

| Method | Route               | Description        |
|-------:|---------------------|--------------------|
| POST   | /api/auth/register  | Register user      |
| POST   | /api/auth/login     | Login and get JWT  |

---

### 📦 Subscriptions

| Method | Route                          | Description                    |
|-------:|--------------------------------|--------------------------------|
| POST   | /api/subscriptions             | Create subscription            |
| GET    | /api/subscriptions             | List user subscriptions        |
| PATCH  | /api/subscriptions/{id}        | Update subscription (partial)  |
| DELETE | /api/subscriptions/{id}        | Delete subscription            |

---

### 👤 Users

| Method | Route               | Description            |
|-------:|---------------------|------------------------|
| GET    | /api/users/me      | Get current user data  |

---

### 🛠️ Admin

| Method | Route                      | Description           |
|-------:|----------------------------|-----------------------|
| GET    | /api/admin/create-admin    | Create admin user     |
| DELETE | /api/admin/users/{username}| Delete user           |

---

## 🌱 Environment Configuration

Create a .env file in the project root (same directory as docker-compose.yml):

```env
# Database
DB_NAME=subscriptiontracker
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION_MS=86400000

# Frontend
FRONTEND_ORIGIN=http://localhost:3000

```
---

## 🐳 Running with Docker Compose
### ▶️ Build and Start Containers
***docker compose up --build***

📌 What this does:

🏗️ Builds the Spring Boot API image

🐘 Starts the PostgreSQL database container

🗄️ Executes Flyway migrations automatically on startup

🌐 Exposes the API on port 8080

### ⛔ Stop Containers
***docker compose down***

### 🧹 Stop and remove containers + database data:
***docker compose down -v***
