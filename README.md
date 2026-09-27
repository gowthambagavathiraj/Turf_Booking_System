# Turf Booking & Management System

A full-stack sports turf management and slot booking system built with **Java Spring Boot**, **Spring Data JPA / Hibernate**, **Spring Security (JWT)**, **SQL (H2 / MySQL)**, and a **responsive HTML5 / CSS3 / JavaScript** frontend matching the reference design.

---

## 🌟 Key Features

### 👤 Customer (Player) Portal
- **User Registration & Login**: Real-time authentication with name, email, phone number, and password.
- **Browse Turfs & Real-time Slots**: View available turf pitches and dynamic 60-minute time slots (06:00 to 22:00).
- **Multi-slot Booking**: Select one or multiple green slots and confirm booking with instant price calculation.
- **User History**: View complete history of all booked matches with turf name, date, time slot, and price paid.
- **Profile Management**: Update user name, phone number, and email.

### 🛡️ Admin / Turf Owner Portal
- **Turf Details Management**: Edit turf name, location, pricing (with/without lights), and operating hours.
- **Slot Scheduling**: Bulk generate or schedule active slots across date ranges.
- **Customer Booking History**: Real-time table displaying who booked each slot with customer name, phone number, slot timing, price, and status.

---

## 🛠️ Technology Stack
- **Backend**: Java 21 / 22, Spring Boot 3.3.4, Spring Security, Spring Data JPA
- **Frontend**: HTML5, CSS3, Vanilla JavaScript (REST API integration)
- **Database**: H2 (In-memory/File-based default for zero-setup) & MySQL script included (`schema-mysql.sql`)
- **Build Tool**: Apache Maven

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 21 or higher
- Apache Maven 3.8+

### 1. Run with Maven
```bash
mvn clean package -DskipTests
java -jar target/turf-booking-system-1.0.0.jar
```

### 2. Access the Application
Open your browser and navigate to:
```
http://localhost:8080
```

### 3. Default Demo Accounts
| Role | Email | Password |
|------|-------|----------|
| **Admin / Owner** | `owner@turf.com` | `password123` |
| **Player / User** | `user@turf.com` | `password123` |
| **Super Admin** | `admin@turf.com` | `password123` |

*(You can also register your own real accounts via the Register tab!)*

---

## 🗄️ Database Setup (Optional MySQL)
To switch from H2 to MySQL:
1. Run `src/main/resources/schema-mysql.sql` in MySQL.
2. In `src/main/resources/application.properties`, uncomment the MySQL configuration lines and set your username/password.
