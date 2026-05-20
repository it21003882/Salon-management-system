# Salon-management-system
# 💅 Beauty Salon Management System

A full-stack web application for managing a beauty salon's customers, services, and appointments. Built with **Java Spring Boot**, **Spring Data JPA**, **Hibernate**, and **MySQL**
---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Contributors](#contributors)

---

## Overview

The Beauty Salon Management System provides two distinct user experiences:

- **Customers** can register, log in, browse available services, and manage their appointments (book, reschedule, cancel).
- **Admins** can manage the service catalogue and view all appointments across the system.

Authentication is handled manually via email/password login. The system uses role-based access control (`ADMIN` / `USER`) enforced at the service layer.

---

## Features

### Customer (User) Features
- Register a new account with profile image upload (base64)
- Log in with email and password
- View all available salon services
- Book an appointment for a specific service and date
- View personal appointment history
- Reschedule an existing appointment
- Cancel an appointment

### Admin Features
- Add new salon services
- Update existing services (name, price)
- Delete services
- View all appointments across all customers

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot |
| Web / MVC | Spring MVC, Thymeleaf |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL |
| Build tool | Maven (Maven Wrapper included) |
| Frontend | HTML, CSS (static assets) |

---

## Project Structure

```
src/
└── main/
    ├── java/com/salon/beautysalon/
    │   ├── BeautySalonApplication.java       # Spring Boot entry point
    │   ├── controller/
    │   │   ├── AppointmentController.java    # Appointment REST endpoints
    │   │   ├── PageController.java           # Thymeleaf page routing
    │   │   ├── ServiceController.java        # Service catalogue endpoints
    │   │   └── UserController.java           # Auth & user endpoints
    │   ├── dto/
    │   │   ├── request/
    │   │   │   ├── AppointmentCreateRequest.java
    │   │   │   ├── AppointmentRescheduleRequest.java
    │   │   │   ├── AuthRequest.java
    │   │   │   ├── ServiceRequest.java
    │   │   │   ├── UserRegisterRequest.java
    │   │   │   └── UserUpdateRequest.java
    │   │   └── response/
    │   │       ├── AppointmentResponse.java
    │   │       ├── AuthResponse.java
    │   │       ├── ServiceResponse.java
    │   │       └── UserResponse.java
    │   ├── model/
    │   │   ├── Appointment.java              # @Entity — appointments table
    │   │   ├── Service.java                  # @Entity — services table
    │   │   └── User.java                     # @Entity — users table
    │   ├── repository/
    │   │   ├── AppointmentRepository.java
    │   │   ├── ServiceRepository.java
    │   │   └── UserRepository.java
    │   └── service/
    │       ├── AppointmentService.java
    │       ├── ServiceService.java
    │       └── UserService.java
    └── resources/
        ├── application.properties
        ├── static/
        │   ├── admin.css
        │   ├── customer.css
        │   ├── login.css
        │   ├── register.css
        │   └── styles.css
        └── templates/
            ├── admin.html
            ├── customer.html
            ├── login.html
            └── register.html
```

---

## Getting Started

### Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 17 or higher |
| Maven | 3.8+ (or use included `mvnw`) |
| MySQL | 8.0+ |

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/beauty-salon.git
cd beauty-salon
```

### 2. Set up the database

Log into MySQL and create the database:

```sql
CREATE DATABASE beauty_salon;
```

> Tables are created automatically on first run via `spring.jpa.hibernate.ddl-auto=update`.

### 3. Configure the application

Edit `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

See the [Configuration](#configuration) section for all properties.

### 4. Run the application

**Using Maven Wrapper (recommended):**

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows PowerShell
.\mvnw.cmd spring-boot:run
```

**Using system Maven:**

```bash
mvn spring-boot:run
```

### 5. Open in browser

```
http://localhost:8080
```

You will be redirected to the login page.

---

## Configuration

`src/main/resources/application.properties`

```properties
spring.application.name=beauty-salon

# Server
server.port=8080

# MySQL Datasource
spring.datasource.url=jdbc:mysql://localhost:3306/beauty_salon
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

```

## Contributors

| Student ID | Name |
|---|---|
| IT21039522 | *(User branch developer)* |
| IT21003882 | *(Admin branch developer)* |

---

