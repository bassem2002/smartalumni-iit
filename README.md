# SmartAlumni IIT 🎓

> Alumni–Student mentoring platform with Full-Stack development and AI-assisted CV analysis.

SmartAlumni IIT is a web platform designed to connect IIT students with alumni mentors and facilitate professional mentoring.

The platform provides dedicated experiences for **Students, Alumni/Mentors and Administrators**, including mentor discovery, mentorship requests, messaging, profile management and intelligent CV processing.

---

## 🎯 Project Objectives

SmartAlumni aims to:

- Connect students with suitable alumni mentors
- Facilitate mentorship requests and follow-up
- Provide direct communication between students and mentors
- Centralize alumni and student profiles
- Help users enrich their profiles from their CV
- Provide administrators with platform supervision and statistics

---

## 🚀 Key Features

### 👤 Authentication & Profiles

- JWT-based authentication
- Student / Alumni / Administrator roles
- User profile management
- Professional and academic information

### 🔎 Mentor Discovery

- Search alumni mentors
- Filter by keywords, sector and country
- View mentor profiles
- Send mentorship requests

### 🤝 Mentorship Management

- Send and manage mentorship requests
- Accept or reject requests
- Track request status
- Mentor availability management

### 💬 Messaging

- Conversations between students and mentors
- Message history
- Communication after mentorship approval

### 📄 Intelligent CV Processing

- CV upload
- Text extraction using Apache Tika
- AI-assisted CV analysis
- DeepSeek API integration
- Local Regex / keyword fallback
- Manual review of extracted information
- Mapping validated CV data to the user profile

### 🛡️ Administration

- User management
- Mentor management
- Mentorship request supervision
- Platform statistics
- Alumni distribution visualization
- AI-assisted statistics interface

---

## 🛠️ Tech Stack

### Backend

`Java 21` • `Spring Boot` • `Spring Security` • `Spring Data JPA` • `PostgreSQL`

`JWT` • `MapStruct` • `Apache Tika` • `Swagger / OpenAPI`

### Frontend

`Angular 19` • `TypeScript` • `RxJS`

### AI & Data Processing

`DeepSeek API` • `Apache Tika` • `Regex / Keyword Analysis`

### Development Tools

`Git` • `GitHub` • `Postman` • `Maven`

---

## 🏗️ Architecture

```text
┌─────────────────────────────┐
│        Angular 19           │
│          Frontend           │
└──────────────┬──────────────┘
               │ REST API
               ▼
┌─────────────────────────────┐
│      Spring Boot / Java     │
│          Backend            │
├─────────────────────────────┤
│ Authentication & Security   │
│ Profiles                    │
│ Mentorship                  │
│ Messaging                   │
│ CV Processing               │
│ Administration              │
└───────┬─────────────┬───────┘
        │             │
        ▼             ▼
┌──────────────┐  ┌──────────────┐
│ PostgreSQL   │  │ DeepSeek API │
└──────────────┘  └──────────────┘
```

---
## 🚀 Installation

### Prerequisites

- JDK 21
- Node.js 20 or later and npm
- PostgreSQL 15 or later
- A DeepSeek API key only if AI features are enabled

### 1. Clone the repository

```bash
git clone https://github.com/bassem2002/smartalumni-iit.git
cd smartalumni-iit
```

### 2. Prepare PostgreSQL

Create an empty database:

```sql
CREATE DATABASE mentorat_platform;
```

Hibernate uses `ddl-auto: update`, so the backend creates or updates the schema when it starts.

### 3. Configure and start the backend

Set the required environment variables before running Spring Boot:

```bash
cd backend
export SERVER_PORT=8081
export DB_URL=jdbc:postgresql://localhost:5432/mentorat_platform
export DB_USERNAME=postgres
export DB_PASSWORD=your_database_password
export JWT_SECRET=replace-with-a-long-random-secret
export DEEPSEEK_ENABLED=false
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend
$env:SERVER_PORT = "8081"
$env:DB_URL = "jdbc:postgresql://localhost:5432/mentorat_platform"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "your_database_password"
$env:JWT_SECRET = "replace-with-a-long-random-secret"
$env:DEEPSEEK_ENABLED = "false"
.\mvnw.cmd spring-boot:run
```

To enable AI features, set `DEEPSEEK_ENABLED=true` and provide `DEEPSEEK_API_KEY`. The API and Swagger UI are available at:

- API: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

### 4. Start the Angular frontend

In a second terminal:

```bash
cd frontend
npm install
npm start
```

Open `http://localhost:4200`. The current frontend services expect the backend at `http://localhost:8081`.

### 5. Verify the build

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd ../frontend
npm run build
```

Never commit database passwords, JWT secrets, or API keys.

## 🎥 Demo Video

A complete demonstration of SmartAlumni IIT, including the different user roles and the main platform workflows.

▶️ **[Watch the SmartAlumni IIT Demo](https://drive.google.com/file/d/1j74wpRJeC8yzun8rzkfKVTZCarL3RZ1l/view?usp=sharing)**

---

## 👥 Project Context

SmartAlumni IIT was officially developed as a 3-student academic project at the Institut International de Technologie (IIT), Sfax.

In practice, I carried out most of the technical implementation of the platform, including the backend, frontend integration, CV processing workflow, AI integration, and overall system integration.

---

## 👨‍💻 My Contribution

My main contributions included:

- Design and implementation of the Spring Boot backend
- Development and integration of REST APIs
- Angular frontend integration
- Authentication and role-based access
- CV upload and processing workflow
- CV text extraction using Apache Tika
- DeepSeek API integration for AI-assisted CV analysis
- Local Regex / keyword fallback analysis
- CV-to-profile data mapping
- Mentorship workflow integration
- Messaging and profile integration
- Testing, debugging and full system integration

## 🖼️ Application Preview

### 🛡️ Administrator Dashboard

The administrator can monitor platform activity, users, mentors, mentorship requests, and platform statistics.

![Admin Dashboard](images/admin-overview.jpg)

### 🤖 AI Assistant

The administration dashboard includes an AI assistant for querying platform statistics and insights.

![AI Assistant](images/admin-ai-assistant.jpg)

### 👥 User Management

Administrators can browse and manage platform users.

![User Management](images/admin-users.jpg)

### 🔎 Mentor Discovery

Users can search for mentors by keywords, business sector, and country.

![Mentor Search](images/alumni-mentor-search.jpg)

### 📄 AI-Assisted CV Analysis

Users can upload a CV, analyze its content, review the extracted information, and validate it before updating their profile.

![CV Analysis](images/alumni-cv-analysis.jpg)

### 💬 Messaging

Students and mentors can communicate directly after a mentorship request is accepted.

![Messaging](images/alumni-messaging.jpg)

### 🎓 Student Experience

Students can track their mentorship requests and search for suitable mentors.

![Student Requests](images/student-requests.png)

![Student Mentor Search](images/student-mentor-search.png)

---

## 📸 More Screenshots

<details>
<summary><b>🛡️ Administrator Interface — View screenshots</b></summary>

<br>

### Dashboard Overview

![Admin Overview](images/admin-overview.jpg)

### AI Assistant

![AI Assistant](images/admin-ai-assistant.jpg)

### User Management

![User Management](images/admin-users.jpg)

### Mentor Management

![Mentor Management](images/admin-mentors.jpg)

### Mentorship Requests

![Mentorship Requests](images/admin-requests.jpg)

</details>

<details>
<summary><b>🎓 Alumni Interface — View screenshots</b></summary>

<br>

### Mentor Search

![Mentor Search](images/alumni-mentor-search.jpg)

### Mentorship Requests

![Mentorship Requests](images/alumni-requests.jpg)

### AI-Assisted CV Analysis

![CV Analysis](images/alumni-cv-analysis.jpg)

### Messaging

![Messaging](images/alumni-messaging.jpg)

### Profile

![Profile](images/alumni-profile.jpg)

</details>

<details>
<summary><b>🤝 Mentor Interface — View screenshots</b></summary>

<br>

### Mentorship Requests

![Mentorship Requests](images/mentor-requests.jpg)

### Mentor Search

![Mentor Search](images/mentor-search.jpg)

### AI-Assisted CV Analysis

![CV Analysis](images/mentor-cv-analysis.jpg)

### Messaging

![Messaging](images/mentor-messaging.jpg)

### Profile

![Profile](images/mentor-profile.jpg)

</details>

<details>
<summary><b>🎓 Student Interface — View screenshots</b></summary>

<br>

### Mentorship Requests

![Student Requests](images/student-requests.png)

### Messaging

![Student Messaging](images/student-messaging.png)

### Profile

![Student Profile](images/student-profile.png)

### Find a Mentor

![Student Mentor Search](images/student-mentor-search.png)

</details>
