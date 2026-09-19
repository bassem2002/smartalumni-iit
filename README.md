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
