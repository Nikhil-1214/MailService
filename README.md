# DSA Concepts Mail Service - Microservices Architecture

A comprehensive microservices-based application for managing Data Structures (DSA) content and sending email notifications to users. This project demonstrates a modern distributed system architecture using Spring Boot, RabbitMQ, and MySQL.

## 📋 Table of Contents

- [What You're Building](#what-youre-building)
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Services](#services)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)

## 🎓 What I have Building

This is an **educational platform for learning Data Structures and Algorithms** with intelligent email automation. Here's the vision:

### The Problem
Many developers struggle to master Data Structures and Algorithms. Traditional learning requires:
- Manual content discovery across multiple platforms
- Inconsistent practice schedules
- No personalized guidance or reminders

### The Solution
Your platform automates the learning journey:

1. **User Registration & Authentication**
   - Users sign up with their email
   - Can log in via traditional email/password or Google OAuth2
   - Profiles store their learning preferences and progress

2. **Content Management**
   - A centralized database of DSA concepts organized by difficulty and topic
   - Each concept includes explanations, code examples, and practice problems
   - Content is curated and searchable

3. **Smart Email Notifications**
   - **Welcome Email**: New users receive a personalized welcome email
   - **Daily DSA Newsletter**: Every day at 8 AM, users get:
     - A new DSA concept to study
     - Code examples and explanations
     - Links to practice problems
     - Progress tracking tips

4. **Event-Driven Architecture**
   - When a user registers → Automatic welcome email is queued and sent
   - Every morning → Scheduled newsletter is generated and distributed to all subscribers
   - All communication is asynchronous, so the main app never waits for email delivery

### Real-World Use Case
Imagine a user named Nikhil:
1. **Day 1**: Nikhil registers on the platform with Google login
2. **Immediately**: A welcome email is automatically sent to his inbox
3. **Every Morning at 8 AM**: Nikhil receives an email with a new DSA concept (Monday: Arrays, Tuesday: Linked Lists, etc.)
4. **Result**: Nikhil learns one concept per day without having to log into the platform

## 🎯 Project Overview

This is a distributed microservices application designed to:

- **Manage Users**: Handle user registration, authentication, and profiles with OAuth2/Google login support
- **Store Content**: Manage and serve DSA (Data Structures and Algorithms) learning content with categorization and metadata
- **Send Emails**: Asynchronously send personalized emails to users including welcome emails and daily newsletters
- **Async Communication**: Use RabbitMQ for decoupled, event-driven communication between services ensuring reliability and scalability

## 🏗️ Architecture

```
┌─────────────────┐
│  User Service   │
│   (Port 8080)   │
└────────┬────────┘
         │ (publishes: user.registered)
         ↓
    ┌─────────────┐
    │  RabbitMQ   │
    │   (AMQP)    │
    └──────┬──────┘
           ↓
┌─────────────────────┐     ┌──────────────────┐
│  Mail Service       │     │ Content Service  │
│   (Port 8082)       │     │  (Port 8081)     │
│ (Sends Emails)      │     │ (DSA Content DB) │
└─────────────────────┘     └──────────────────┘
         ↓                           ↓
    ┌─────────────┐            ┌─────────────┐
    │  Gmail SMTP │            │ MySQL-DB    │
    │  (Port 587) │            │ (Port 3309) │
    └─────────────┘            └─────────────┘

    ┌──────────────────────┐
    │   MySQL-User DB      │
    │    (Port 3310)       │
    └──────────────────────┘
```

### Key Components

1. **User Service**: Manages user authentication and registration
2. **Content Service**: Stores and retrieves DSA content
3. **Mail Service**: Handles email dispatch
4. **RabbitMQ**: Message broker for asynchronous communication
5. **MySQL Databases**: Persistent data storage
6. **Docker Compose**: Orchestration and deployment

## 💾 Technologies Used

### Core Framework
- **Spring Boot**: 4.1.0
- **Java**: 21
- **Apache Maven**: Build automation

### Data & Persistence
- **MySQL**: 8.0 (Database)
- **Spring Data JPA**: ORM framework
- **Hibernate**: JPA implementation
- **JDBC**: Database connectivity

### Messaging
- **RabbitMQ**: 3 (Message broker)
- **Spring AMQP**: AMQP support
- **STOMP**: Message protocol

### Security & Authentication
- **Spring Security**: Authentication/Authorization
- **OAuth2 Client**: Google login integration
- **JWT**: (if implemented in User Service)

### Communication
- **Spring Web MVC**: REST API framework
- **Spring Boot Mail**: Email sending
- **Thymeleaf**: Email template engine
- **Jackson**: JSON processing

### Development Tools
- **Lombok**: Reduce boilerplate code
- **Docker**: Containerization
- **Docker Compose**: Orchestration

### Testing
- **JUnit**: Unit testing framework
- **Mockito**: Mocking framework
- **Spring Boot Test**: Integration testing

## 🚀 Services

### 1. User Service
- **Port**: 8080
- **Database**: MySQL (Port 3310)
- **Purpose**: User registration, authentication, and profile management
- **Features**:
  - User registration and login
  - OAuth2 integration with Google
  - Spring Security for authentication
  - JPA/Hibernate ORM

### 2. Content Service
- **Port**: 8081
- **Database**: MySQL (Port 3309)
- **Purpose**: Manage DSA concepts and learning materials
- **Features**:
  - CRUD operations for DSA content
  - Content categorization
  - Data persistence with JPA

### 3. Mail Service
- **Port**: 8082
- **Message Queue**: RabbitMQ
- **Purpose**: Send transactional and promotional emails
- **Features**:
  - Welcome emails for new users
  - Daily DSA newsletter (cron-based: 8 AM daily)
  - HTML email templates using Thymeleaf
  - RabbitMQ message consumption
  - Gmail SMTP integration

## 📡 API Endpoints

### User Service (Port 8080)

```
POST   /api/users/register      - Register new user
POST   /api/users/login         - User login
GET    /api/users/{id}          - Get user profile
PUT    /api/users/{id}          - Update user profile
DELETE /api/users/{id}          - Delete user
GET    /api/users               - List all users
```

### Content Service (Port 8081)

```
GET    /api/content             - Get all DSA content
GET    /api/content/{id}        - Get specific content
POST   /api/content             - Create new content (admin)
PUT    /api/content/{id}        - Update content (admin)
DELETE /api/content/{id}        - Delete content (admin)
```

### Mail Service (Port 8082)

```
POST   /api/mail/send           - Send email
GET    /api/mail/status         - Check service health
```

## 🛠️ Project Structure

```
Ds_concepts_MailService/
├── user_service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/nikhil/...
│   │   │   └── resources/application.yaml
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── content_service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/nikhil/...
│   │   │   └── resources/application.yaml
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
├── mail-service/
│   ├── mail-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/nikhil/...
│   │   │   │   └── resources/
│   │   │   │       ├── application.yaml
│   │   │   │       └── templates/
│   │   │   └── test/
│   │   ├── pom.xml
│   │   └── Dockerfile
│   └── Dockerfile
├── docker-compose.yml
├── init-content.sql
└── README.md
```



















