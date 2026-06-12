# Payroll Management System

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Maven](https://img.shields.io/badge/Build-Maven-blue?logo=apachemaven)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![JDBC](https://img.shields.io/badge/JDBC-Connector%2FJ-green)
![Swing](https://img.shields.io/badge/Java-Swing%20%2F%20AWT-red)
![JUnit5](https://img.shields.io/badge/Tests-JUnit5%20%2B%20Cucumber-25A162?logo=cucumber)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

**Author:** RezaulmKarim & Ranajit Baran Chowdhury — Software Programmer & QA Automation Engineer
**Email:** rknyc2021@gmail.com
**GitHub:** [@rezaulkarim](https://github.com/REZAULKARIM2024)

A desktop payroll management application built with **Java Swing** and **MySQL**, packaged and tested with **Maven**. It covers employee record management, payroll/salary calculation, payslip generation (with amount-in-words cheque text), and login authentication — backed by a JUnit5 + Cucumber automated test suite (unit, BDD, and Swing UI tests).

---

## 📋 Quick Overview

| Component | Technology |
|-----------|-----------|
| **Frontend** | Java Swing / AWT (tabbed GUI) |
| **Backend Logic** | Core Java (OOP), pure helper/validator classes |
| **Database** | MySQL 8.0 |
| **Connectivity** | JDBC via `mysql-connector-j` |
| **Build** | Maven (`pom.xml`, Java 21) |
| **Testing** | JUnit 5, Cucumber (`cucumber-java`, `junit-platform-suite`), AssertJ-Swing for UI tests |
| **Reporting** | `maven-cucumber-reporting` (HTML test reports) |
| **Document Output** | iText7 (PDF), Apache POI (Excel/Office) |

---

## ✨ Key Features

### 🔐 Login / Authentication
- Username/password login screen (`LoginTab`)
- Authentication logic extracted into `AuthService` for unit testing
- On success, navigates into the main tabbed dashboard (`PayrollTabbedGUI`)

### 👨‍💼 Employee Management
- **Add Employee** (`AddEmployeeTab`) — form validated via `EmployeeFormValidator`
- **View Employees** (`ViewEmployeeTab`) — live table of all employees, refreshed from the database
- **Update Employee** (`UpdateEmployeeTab`) — edit existing employee records
- **Delete Employee** (`DeleteEmployeeTab`) — delete by Employee ID, with validation via `DeleteEmployeeHelper`
- **Search Employee** (`SearchEmployeeTab`) — search by Employee ID or name (`SearchQueryHelper`); live table search box is protected from invalid-regex input by `SearchFilterHelper`
- **Refresh** (`RefreshTab`) — reloads the employee table and clears the update form

### 💰 Payroll & Salary Calculation
- `PayrollCalculator` computes gross/net salary, overtime, double-time, holiday pay, special-work pay, bonus, tax, and medical insurance deductions
- `Employees` model class represents an employee record across the app

### 🧾 Payslip Generation
- **Payslip** tab (`PayslipTab`) generates a payslip and cheque for a given Employee ID + pay date
- `PayslipFormatter` validates/parses the pay date and formats it for SQL/display
- `AmountToWordsConverter` converts the net salary amount into cheque-style words (e.g. "One Thousand Dollars and 00/100")

### 🗄️ Database Integration
- `DBConnection` provides the JDBC connection to MySQL (`payroll_db`)
- Setup script: [`db/setup_payroll_db.sql`](db/setup_payroll_db.sql) — creates the database, app user, `employees` table, and sample rows

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| UI | Java Swing / AWT | Tabbed desktop GUI (`PayrollTabbedGUI`) |
| Logic | Core Java (OOP) | Validators, helpers, calculators (no Swing/JDBC dependency — independently unit-tested) |
| Data Access | JDBC (`mysql-connector-j`) | Communication with MySQL |
| Database | MySQL 8.0 | Persistent storage (`employees` table) |
| Documents | iText7, Apache POI | PDF / Office document generation |
| Testing | JUnit 5, Cucumber, AssertJ-Swing | Unit, BDD/feature, and UI automation tests |
| Reporting | maven-cucumber-reporting | HTML Cucumber test reports |

---

## ✅ Prerequisites

- **JDK 21** (configured in `PATH`/`JAVA_HOME`)
- **Maven 3.8+**
- **MySQL Server 8.0** — installed and running
- **Git** — for version control

---

## 🚀 Installation & Setup

### Step 1 — Clone the repository

```bash
git clone https://github.com/ranajitchowdhury/PayrollManagementSystem.git
cd PayrollManagementSystem
```

### Step 2 — Set up the MySQL database

Run the included setup script against your MySQL server:

```bash
mysql -u root -p < db/setup_payroll_db.sql
```

This creates:
- the `payroll_db` database
- an application user `admin` / `admin123` (used by `DBConnection.java`)
- the `employees` table with the full schema used by the app
- two sample employee rows (John Doe, Jane Smith)

> If you use different credentials, update `URL`, `USER`, and `PASSWORD` in
> `src/main/java/com/payroll/DBConnection.java` to match.

### Step 3 — Build the project

```bash
mvn clean install
```

Maven will download `mysql-connector-j`, JUnit 5, Cucumber, AssertJ-Swing,
iText7, and Apache POI automatically (see `pom.xml`).

---

## ▶️ How to Run

Run the application's main class, `com.payroll.PayrollTabbedGUI`:

```bash
mvn exec:java -Dexec.mainClass="com.payroll.PayrollTabbedGUI"
```

or run the packaged jar after `mvn clean package`:

```bash
java -jar target/payroll-system-1.0.0.jar
```

1. The **Login** screen appears — log in to access the dashboard.
2. From the tabbed dashboard you can:
   - Add, view, update, search, and delete employee records
   - Refresh the employee table
   - Generate payslips and cheques for a given Employee ID and pay date

---

## 🧪 Testing

This project has an automated test suite covering unit, BDD/Cucumber, and Swing UI layers.

```bash
mvn clean test      # run all unit + Cucumber tests
mvn clean verify     # run tests and generate the Cucumber HTML report
```

| Test type | Examples |
|-----------|----------|
| **Unit (JUnit 5)** | `EmployeeFormValidatorTest`, `PayslipFormatterTest`, `PayrollCalculatorTest`, `AmountToWordsConverterTest`, `AuthServiceTest`, `SearchQueryHelperTest`, `DeleteEmployeeHelperTest`, `SearchFilterHelperTest`, `SecurityTest`, `UIHelperTest` |
| **Cucumber (BDD)** | `login_auth.feature`, `search_employee.feature`, `payslip_formatting.feature`, `employee_form_validation.feature`, `payroll_calculator.feature`, `amount_to_words.feature`, `employees_model.feature`, `db_connection.feature` |
| **Swing UI (AssertJ-Swing, `@Tag("ui")`)** | `WelcomeTabTest`, `RefreshTabTest` — drive real `java.awt.Robot` clicks, require an on-screen display |

Test highlights:
- **Smoke** — main tabs/labels/buttons render correctly on startup
- **Sanity/Regression** — positive and negative cases for date parsing, login credentials, search, and employee deletion
- **Security** — SQL-injection-style inputs are rejected/parameterized (`SecurityTest`), and the live search box no longer throws `PatternSyntaxException` on malformed regex (`SearchFilterHelper`)

Detailed manual + automated test plan for the Swing tabs: [`TEST_PLAN_GUI_TABS.md`](TEST_PLAN_GUI_TABS.md).

---

## 📁 Project Structure

```
PayrollManagementSystem/
├── pom.xml
├── db/
│   └── setup_payroll_db.sql              ← MySQL schema + sample data
├── src/
│   ├── main/java/com/payroll/
│   │   ├── PayrollTabbedGUI.java          ← main entry point / tabbed window
│   │   ├── LoginTab.java                  ← login screen
│   │   ├── AuthService.java               ← login credential check (extracted, tested)
│   │   ├── AddEmployeeTab.java
│   │   ├── ViewEmployeeTab.java
│   │   ├── UpdateEmployeeTab.java
│   │   ├── DeleteEmployeeTab.java
│   │   ├── DeleteEmployeeHelper.java      ← delete validation (extracted, tested)
│   │   ├── SearchEmployeeTab.java
│   │   ├── SearchQueryHelper.java         ← search SQL/pattern logic (extracted, tested)
│   │   ├── SearchFilterHelper.java        ← safe live-search regex filter (extracted, tested)
│   │   ├── RefreshTab.java
│   │   ├── WelcomeTab.java
│   │   ├── PayslipTab.java
│   │   ├── PayslipFormatter.java          ← payslip date/SQL formatting (extracted, tested)
│   │   ├── AmountToWordsConverter.java    ← cheque amount-in-words (extracted, tested)
│   │   ├── PayrollCalculator.java         ← salary/deductions calculation (extracted, tested)
│   │   ├── EmployeeFormValidator.java     ← Add/Update form validation (extracted, tested)
│   │   ├── Employees.java                 ← employee model
│   │   ├── UIHelper.java                  ← UI font/look-and-feel helper
│   │   └── DBConnection.java              ← JDBC connection (payroll_db)
│   └── test/
│       ├── java/com/payroll/              ← JUnit5 + AssertJ-Swing tests
│       └── java/com/payroll/cucumber/     ← Cucumber step definitions + runner
│       └── resources/features/            ← Gherkin .feature files
├── TEST_PLAN_GUI_TABS.md
└── README.md
```

---

## 🌟 Highlights

| Highlight | Detail |
|-----------|--------|
| ✅ Desktop GUI | Java Swing tabbed interface for all payroll operations |
| ✅ Complete CRUD | Add, view, update, search, delete employees in MySQL |
| ✅ Payroll Engine | Gross/net salary, overtime, holiday pay, bonus, deductions |
| ✅ Payslip & Cheque | Auto-generated payslip with amount-in-words cheque text |
| ✅ Clean Architecture | Business logic extracted from Swing tabs into standalone, testable classes |
| ✅ Automated Testing | JUnit5 + Cucumber + AssertJ-Swing, with smoke/sanity/regression/security coverage |
| ✅ JDBC Integration | MySQL persistence via `mysql-connector-j` |

---

## 🔮 Future Enhancements

- 🔒 Move hardcoded DB/login credentials to environment variables or a config file
- 📄 Export payslips directly as PDF (iText7 already a dependency)
- 📊 Excel/Office payroll reports via Apache POI
- 🌐 Web version using Spring Boot + React
- 🔗 REST API for third-party integration
- 🧪 Headless/Xvfb setup for running AssertJ-Swing tests in CI

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
**Last Updated:** June 2026
