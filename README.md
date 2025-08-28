
##Blood Connect

Blood Connect is a Spring Boot + Thymeleaf web application that connects blood donors with people in need of blood.
It provides a simple and effective platform where donors can register, recipients can request blood, and admins can manage blood bank records efficiently.
---
##Features
***User Authentication(Login & Signup for Donors/Recipients/Admins)
***Donor Registration with personal details, blood group & availability
-Blood Requests (submit & track requests)
-Search Donors by blood group and location
Admin Dashboard to manage users, donors, and requests
Statistics View(blood availability by type)
Features
User Authentication with Spring Security
Password Encryption using BCrypt
OTP Verification during signup/login (via email)
Forgot Password & Reset via Email OTP
---
Tech Stack
Backend & Frontend: Spring Boot (Spring MVC, Thymeleaf)
Database: MySQL (can be replaced with PostgreSQL/H2 for dev)
Build Tool: Maven
---
 Main Endpoints (UI Pages)

| URL                | Description                           |
| ------------------ | ------------------------------------- |
| `/`                | Home Page                             |
| `/login`           | User Login                            |
| `/register`        | Donor/Recipient Registration          |
| `/donors`          | View/Search Donors                    |
| `/requests`        | View/Create Blood Requests            |
| `/admin/dashboard` | Admin Panel (users, donors, requests) |
---
Roles
Donor: Register & update availability
Recipient: Search donors & request blood
Admin: Manage users, donors, and requests
---



