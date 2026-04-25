# Nova Events

A Spring Boot SSR web application for managing campus clubs and events.

## Tech Stack
- **Backend:** Spring Boot 3.5.10 (Java 17 / Kotlin 2.1.0)
- **Database:** MySQL 8.0 (Docker)
- **ORM:** Spring Data JPA with Hibernate
- **Frontend:** Thymeleaf & Bootstrap 5

## Prerequisites
- Docker Desktop installed and running

---

## Run with Database

### 1. Create and start the database (first time only)
```bash
docker run --name novaevents-db -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=novaevents -e MYSQL_USER=novaevents_user -e MYSQL_PASSWORD=novaevents123 -p 3306:3306 -d mysql:8.0
````

### 2. Start the database (next times)

```bash
docker start novaevents-db
```

> MySQL may take a few seconds to start. If the application fails to connect, wait a moment and try again.

---

## Run Application

```bash
$env:DB_USERNAME="novaevents_user"; $env:DB_PASSWORD="novaevents123"; $env:WEATHER_API_KEY="api_key"; mvn spring-boot:run
```

---

## Access

[http://localhost:8080/clubs](http://localhost:8080/clubs)

---

## Run Tests

```bash
mvn test
```

---

## Project Structure

```
.
├── AUTHORS.txt
├── pom.xml
└── src
    ├── main
    │   ├── kotlin
    │   │   └── pt/unl/fct/iadi/novaevents
    │   └── resources
    │       └── application.properties
    └── test
        ├── kotlin
        └── resources
```

---

## Configuration Notes

* Database credentials and API keys are **not hardcoded**
* They are injected using environment variables:

  * `DB_USERNAME`
  * `DB_PASSWORD`
  * `WEATHER_API_KEY`

Example:

```bash
$env:DB_USERNAME="novaevents_user"; $env:DB_PASSWORD="novaevents123"; $env:WEATHER_API_KEY="your_api_key"; mvn spring-boot:run
```

---

## Features (Dynamic Features Extension)

* Weather-aware event creation for Hiking & Outdoors Club
* REST API endpoint: `/api/weather`
* Content negotiation (JSON + HTML)
* Client-side weather check:

  * JavaScript (`fetch`)
  * HTMX (no custom JS)
* Server-side validation of weather conditions
* Automated tests with >70% coverage (JaCoCo)

```
```
