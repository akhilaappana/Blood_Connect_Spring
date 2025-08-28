
---

# 🩸 Blood Connect

**Blood Connect** is a **Spring Boot + Thymeleaf** web application that connects **blood donors** with people in need of blood.
It ensures **secure authentication** with **BCrypt password hashing, OTP verification, and forgot password functionality**, making it a reliable and safe platform.

---

## 🚀 Features

* 🔐 **User Authentication** with **Spring Security**
* 🔑 **Password Encryption** using **BCrypt**
* 📩 **OTP Verification** during signup/login (via email)
* 🔄 **Forgot Password & Reset via Email OTP**
* 🩸 **Donor Registration** with details (name, blood group, location, availability)
* 🧾 **Blood Requests** (submit & track requests)
* 📍 **Search Donors** by **blood group** and **location**
* 🗂️ **Admin Dashboard** to manage users, donors, and requests
* 📊 **Statistics View** for blood group availability

---

## 🛠️ Tech Stack

* **Backend & Frontend**: Spring Boot (Spring MVC, Thymeleaf)
* **Database**: MySQL (can use PostgreSQL/H2 in dev)
* **ORM**: Hibernate / Spring Data JPA
* **Security**: Spring Security + BCrypt password hashing
* **Email Service**: JavaMailSender (for OTP & password reset emails)
* **Build Tool**: Maven

---


## 📌 Main Endpoints (UI Pages)

| URL                | Description                          |
| ------------------ | ------------------------------------ |
| `/`                | Home Page                            |
| `/login`           | User Login                           |
| `/register`        | Register with OTP verification       |
| `/otp-verify`      | OTP Verification Page                |
| `/forgot-password` | Request password reset via email OTP |
| `/reset-password`  | Reset password page                  |
| `/donors`          | View/Search Donors                   |
| `/requests`        | View/Create Blood Requests           |
| `/admin/dashboard` | Admin Panel                          |

---

## 👥 Roles

* **Donor**: Register, verify via OTP, update availability
* **Recipient**: Search donors & request blood, reset password if forgotten
* **Admin**: Manage all users, donors, requests

---

## 🔐 Security Highlights

* **BCrypt Password Encryption** → All passwords stored securely (hashed + salted).
* **OTP Verification** → During signup/login to prevent fake accounts.
* **Forgot Password with OTP** → Secure recovery flow with email-based OTP.
* **Role-Based Access** → Different permissions for **Admin, Donor, Recipient**.

---



