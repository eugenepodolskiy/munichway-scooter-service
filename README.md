# MunichWay Scooter Service 

This is a personal learning project created to both solidify my foundational knowledge of backend development and gain hands-on experience with advanced concepts. My primary goal was to thoroughly refresh core Java and Spring Boot principles while diving deep into spatial databases, secure REST APIs, and infrastructure containerization.

## Project Overview
The project provides a backend system for an electric scooter rental service in Munich. It enables real-time search for available scooters within a specific geographic radius. The system securely handles the entire lifecycle of a scooter trip: from checking user balances and locking the scooter to calculating the final trip cost based on time and updating the scooter's GPS coordinates upon return.

## Technologies Used
Java 23

Spring Boot 4.0.2 (Embedded Tomcat 11.0.15)

PostgreSQL 15.8 / PostGIS (Spatial data handling)

Spring Data JPA / Hibernate ORM 7.2.1.Final (with Hibernate Spatial)

Spring Security & JWT (JSON Web Tokens)

Flyway (Database Migrations)

Docker & Docker Compose

JUnit 5, Mockito, AssertJ (BDD Testing)

Spring Boot Actuator & Scheduled Tasks (Background processing)

## Learning Goals & Insights
Refreshing Core Fundamentals: Solidifying essential principles of Object-Oriented Programming (OOP), RESTful API design, and relational database modeling.

Spatial Data: Integrating PostGIS to perform geographic radius queries and model real-world coordinate data using org.locationtech.jts.geom.Point.

Automated Testing: Writing business-critical unit tests using Behavior-Driven Development (BDD) principles and modern assertion libraries (AssertJ).

Infrastructure as Code: Containerizing the database infrastructure and managing reproducible schema changes via Flyway.

Security & Architecture: Implementing stateless token authentication (JWT) and structuring a modern REST API.

Background Jobs: Utilizing Spring's @Scheduled annotation to run asynchronous billing processes and scooter maintenance checks.
