# Payroll Management System

![Java](https://img.shields.io/badge/Java-8+-orange?logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![JDBC](https://img.shields.io/badge/JDBC-Driver-green)
![Swing](https://img.shields.io/badge/Java-Swing%20%2F%20AWT-red)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

**Author:** Rezaul Karim & Ranajit B. Chowdhury
SoftWare Programmer & QA Automation Engineer
**Email:** rknyc2021@gmail.com
**GitHub:** [@rezaulkarim](https://github.com/REZAULKARIM2024)

A comprehensive desktop-based payroll automation solution built with **Java Swing/AWT** and **MySQL**. This application delivers end-to-end payroll management for small to medium-sized organizations — covering secure authentication, employee management, attendance tracking, and automated salary calculations.

---

## 📋 Quick Overview

| Component | Technology |
|-----------|-----------|
| **Frontend** | Java Swing / AWT GUI |
| **Backend Logic** | Core Java (OOP) |
| **Database** | MySQL 8.0 |
| **Connectivity** | JDBC (Java Database Connectivity) |
| **IDEs Supported** | IntelliJ IDEA / Eclipse / NetBeans |
| **Tools** | MySQL Workbench, MySQL Connector/J |

---

## ✨ Key Features

### 🔐 Secure Authentication
- User login and signup with credential validation
- Role-based session management
- Secure access control for sensitive payroll data

### 👨‍💼 Employee Management
- Full CRUD — Add, Update, Search, and Delete employee records
- Structured employee database with complete profile storage
- Quick employee lookup and profile management

### 💰 Automated Salary Calculation
Intelligent payroll engine calculating:
- **HRA** — House Rent Allowance
- **DA** — Dearness Allowance
- **PF** — Provident Fund deduction
- **Gross Salary** and **Net Salary** generation

### 📅 Attendance Tracking
- Daily attendance marking (Present / Absent / Leave)
- Leave management and tracking
- Monthly attendance reports and history

### 🗄️ Database Integration
- MySQL-based persistent storage
- Efficient data retrieval using JDBC
- Reliable transaction management

### 🧾 Pay Slip Generation
- Professional salary slip generation per employee
- Printable payroll reports
- Detailed payment and deduction breakdowns

---

## 🏗️ System Architecture

```
┌──────────────────────────────────────┐
│         Login / Signup Module        │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│            Main Dashboard            │
└──────────────────┬───────────────────┘
                   │
       ┌───────────┼───────────┐
       │           │           │
       ▼           ▼           ▼
┌──────────┐ ┌──────────┐ ┌──────────────┐
│ Employee │ │Attendance│ │    Salary    │
│Management│ │Management│ │ Calculation  │
└────┬─────┘ └────┬─────┘ └──────┬───────┘
     │            │              │
     ▼            ▼              ▼
┌──────────┐ ┌──────────┐ ┌──────────────┐
│ Employee │ │Attendance│ │   Payroll    │
│  Table   │ │  Table   │ │   Records    │
└────┬─────┘ └────┬─────┘ └──────┬───────┘
     │            │              │
     └────────────┴──────────────┘
                  │
                  ▼
          ┌──────────────┐
          │   Pay Slip   │
          │    Output    │
          └──────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| UI | Java Swing / AWT | Desktop GUI components |
| Logic | Core Java (OOP) | Business rules, calculations |
| Data Access | JDBC | Database communication |
| Database | MySQL 8.0 | Persistent data storage |
| Driver | MySQL Connector/J | JDBC–MySQL bridge |

---

## ✅ Prerequisites

Before setting up the project, ensure you have installed:

- **JDK 8 or higher** — configured in PATH
- **MySQL Server** — installed and running
- **MySQL Workbench** — for database management
- **MySQL Connector/J** — JDBC driver `.jar` file
- **Git** — for version control

---

## 🚀 Installation & Setup

### Step 1 — Create Database

Open MySQL Workbench and execute:

```sql
CREATE DATABASE payroll_db;
USE payroll_db;
```

### Step 2 — Create Tables

```sql
-- Employee Table
CREATE TABLE Employee (
    emp_id       INT PRIMARY KEY AUTO_INCREMENT,
    emp_name     VARCHAR(100),
    designation  VARCHAR(50),
    department   VARCHAR(50),
    salary       DECIMAL(10,2),
    email        VARCHAR(100)
);

-- Login Table
CREATE TABLE Login (
    user_id   INT PRIMARY KEY AUTO_INCREMENT,
    username  VARCHAR(50) UNIQUE,
    password  VARCHAR(100),
    emp_id    INT,
    FOREIGN KEY (emp_id) REFERENCES Employee(emp_id)
);

-- Attendance Table
CREATE TABLE Attendance (
    attendance_id   INT PRIMARY KEY AUTO_INCREMENT,
    emp_id          INT,
    attendance_date DATE,
    status          VARCHAR(20),
    FOREIGN KEY (emp_id) REFERENCES Employee(emp_id)
);

-- Salary Table
CREATE TABLE Salary (
    salary_id    INT PRIMARY KEY AUTO_INCREMENT,
    emp_id       INT,
    basic        DECIMAL(10,2),
    hra          DECIMAL(10,2),
    da           DECIMAL(10,2),
    pf           DECIMAL(10,2),
    gross_salary DECIMAL(10,2),
    net_salary   DECIMAL(10,2),
    month_year   VARCHAR(10),
    FOREIGN KEY (emp_id) REFERENCES Employee(emp_id)
);
```

### Step 3 — Clone the Repository

```bash
git clone https://github.com/ranajitchowdhury/PayrollManagementSystem.git
cd PayrollManagementSystem
```

### Step 4 — Add JDBC Driver

1. Download **MySQL Connector/J** from [mysql.com/downloads/connector/j](https://dev.mysql.com/downloads/connector/j/)
2. Extract the `.jar` file
3. Add to your project's Build Path:

| IDE | Steps |
|-----|-------|
| **IntelliJ IDEA** | File → Project Structure → Libraries → + → Add JAR |
| **Eclipse** | Right-click Project → Build Path → Add External Archives |
| **NetBeans** | Right-click Project → Properties → Libraries → Add JAR |

### Step 5 — Configure Database Connection

Update `Conn.java` with your MySQL credentials:

```java
package com.payroll.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conn {

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/payroll_db",
                "root",
                "YOUR_PASSWORD"   // ← Replace with your MySQL password
            );
            System.out.println("✅ Database connection established.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Connection failed!");
            e.printStackTrace();
        }
        return connection;
    }
}
```

---

## ▶️ How to Run

1. Open the project in your IDE
2. Run the main entry point:
   - `Splash.java` → shows splash screen on startup
   - `Login.java` → opens the login interface directly
3. **Sign Up** to create a new account, or **Login** with existing credentials
4. From the **Dashboard**, you can:
   - Manage employee records
   - Track daily attendance
   - Calculate and process payroll
   - Generate and view pay slips

---

## 📁 Project Structure

```
PayrollManagementSystem/
├── src/
│   └── com/payroll/
│       ├── database/
│       │   └── Conn.java               ← JDBC connection manager
│       ├── models/
│       │   ├── Employee.java
│       │   ├── Salary.java
│       │   └── Attendance.java
│       ├── views/
│       │   ├── Splash.java
│       │   ├── Login.java
│       │   ├── Dashboard.java
│       │   └── EmployeePanel.java
│       └── controllers/
│           └── PayrollController.java
├── lib/
│   └── mysql-connector-java-x.x.x.jar
├── README.md
└── .gitignore
```

---

## 🌟 Why This Project Stands Out

| Highlight | Detail |
|-----------|--------|
| ✅ Desktop GUI | Full-featured Swing/AWT interface |
| ✅ Complete CRUD | Create, Read, Update, Delete with MySQL |
| ✅ Secure Auth | Login with credential validation |
| ✅ Business Logic | Accurate payroll with deductions and allowances |
| ✅ JDBC Integration | Efficient DB connectivity and transactions |
| ✅ Clean Architecture | Separate layers: UI, Logic, Database |
| ✅ OOP Principles | Classes, inheritance, encapsulation applied |

---

## 🔮 Future Enhancements

- 🔒 Role-based access control (Admin / HR / Employee)
- 📄 Export pay slips as PDF
- 📊 Advanced reporting dashboard with charts
- 🌐 Web version using Spring Boot + React
- 🔔 Email notifications for salary processing
- 📱 Mobile companion application
- 🔗 REST API for third-party integration

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create your branch: `git checkout -b feature/improvement`
3. Commit your changes: `git commit -m 'Add improvement'`
4. Push to the branch: `git push origin feature/improvement`
5. Open a Pull Request

---

## 📄 License

This project is open-source under the **MIT License**. See [LICENSE](LICENSE) for details.

---

## 💬 Support & Feedback

If you found this project helpful:

- ⭐ **Star** the repository
- 🍴 **Fork** it for your own use
- 💬 **Open an issue** for bugs or suggestions
- 🤝 **Contribute** to improve the project

---

## ⚠️ Disclaimer

This project is designed for educational and small-scale organizational use. For enterprise-level payroll processing, consult payroll specialists and compliance experts to ensure adherence to local tax laws and regulations.

---

**Version:** 1.0.0
**Last Updated:** May 2026
