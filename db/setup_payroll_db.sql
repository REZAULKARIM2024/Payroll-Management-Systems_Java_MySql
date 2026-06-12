-- ============================================================================
-- Payroll Management System - local MySQL setup
--
-- Matches the connection details hardcoded in
-- src/main/java/com/payroll/DBConnection.java:
--   URL      = jdbc:mysql://localhost:3306/payroll_db
--   USER     = admin
--   PASSWORD = admin123
--
-- Run with:
--   mysql -u root -p < db/setup_payroll_db.sql
-- ============================================================================

CREATE DATABASE IF NOT EXISTS payroll_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Create the application user used by DBConnection.java.
-- '%'/'localhost' both covered for safety; localhost is what the app uses.
CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED BY 'admin123';
GRANT ALL PRIVILEGES ON payroll_db.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;

USE payroll_db;

-- ----------------------------------------------------------------------------
-- employees table
-- Columns reverse-engineered from AddEmployeeTab / UpdateEmployeeTab /
-- SearchEmployeeTab / SearchQueryHelper / PayslipTab / PayrollTabbedGUI /
-- EmployeeFormValidator.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS employees (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    employee_id         INT NOT NULL UNIQUE,
    first_name          VARCHAR(100)  NOT NULL,
    last_name           VARCHAR(100)  NOT NULL,
    ssn                 VARCHAR(20)   NULL,
    designation         VARCHAR(100)  NULL,
    contact_no          VARCHAR(20)   NULL,
    email_id            VARCHAR(150)  NULL,
    hire_date           DATE          NULL,
    department          VARCHAR(100)  NULL,
    customer            VARCHAR(100)  NULL,
    bank_name           VARCHAR(100)  NULL,
    account_no          VARCHAR(34)   NULL,

    -- hours / pay rate
    Total_hours         DOUBLE NOT NULL DEFAULT 0,
    pay_rate_hourly     DOUBLE NOT NULL DEFAULT 0,
    overtime_hours      DOUBLE NOT NULL DEFAULT 0,
    overtime_amount     DOUBLE NOT NULL DEFAULT 0,
    doubletime_hours    DOUBLE NOT NULL DEFAULT 0,
    doubletime_amount   DOUBLE NOT NULL DEFAULT 0,
    extended_hours      DOUBLE NOT NULL DEFAULT 0,
    extended_amount     DOUBLE NOT NULL DEFAULT 0,
    holidaypaid_hours   DOUBLE NOT NULL DEFAULT 0,
    holidaypaid_amount  DOUBLE NOT NULL DEFAULT 0,
    specialwork_hours   DOUBLE NOT NULL DEFAULT 0,
    specialwork_amount  DOUBLE NOT NULL DEFAULT 0,
    bonus_amount        DOUBLE NOT NULL DEFAULT 0,

    -- salary breakdown
    salary              DOUBLE NOT NULL DEFAULT 0,
    gross_salary        DOUBLE NOT NULL DEFAULT 0,
    ytd_gross           DOUBLE NOT NULL DEFAULT 0,
    tax_deduction       DOUBLE NOT NULL DEFAULT 0,
    medical_insurance   DOUBLE NOT NULL DEFAULT 0,
    total_deductions    DOUBLE NOT NULL DEFAULT 0,
    net_salary          DOUBLE NOT NULL DEFAULT 0,

    -- payslip
    pay_date            DATE   NULL,

    -- soft-delete flag used by SearchQueryHelper / DeleteEmployeeTab
    active              TINYINT(1) NOT NULL DEFAULT 1
);

-- ----------------------------------------------------------------------------
-- Optional: a couple of sample active employees so SearchEmployeeTab /
-- ViewEmployeeTab / PayslipTab have something to show right away.
-- Safe to remove or comment out.
-- ----------------------------------------------------------------------------
INSERT INTO employees
    (employee_id, first_name, last_name, designation, contact_no, email_id,
     hire_date, department, pay_rate_hourly, Total_hours, salary,
     gross_salary, net_salary, active)
VALUES
    (1, 'John', 'Doe', 'Software Engineer', '01711111111', 'john.doe@example.com',
     '2024-01-15', 'Engineering', 25.0, 40, 1000, 1000, 850, 1),
    (2, 'Jane', 'Smith', 'HR Manager', '01722222222', 'jane.smith@example.com',
     '2023-06-01', 'Human Resources', 30.0, 40, 1200, 1200, 1020, 1)
ON DUPLICATE KEY UPDATE employee_id = employee_id;
