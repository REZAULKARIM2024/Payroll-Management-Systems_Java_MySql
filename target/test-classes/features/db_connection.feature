Feature: Database Connectivity
  As the payroll system
  I want to establish a connection to the MySQL database
  So that employee, payroll and payslip data can be read and written

  @db
  Scenario: Application can connect to the configured MySQL database
    When I try to open a database connection
    Then the connection should be successful and open
