Feature: Employees Data Model
  As the payroll system
  I want the Employees model to correctly store and retrieve employee data
  So that every screen (Add, Update, View, Search, Payslip) works with consistent data

  Scenario: Setting and getting all employee fields
    Given a new employee record
    When I set the employee id to 101
    And I set the first name to "John"
    And I set the last name to "Doe"
    And I set the designation to "Software Engineer"
    And I set the contact number to "01711111111"
    And I set the email id to "john.doe@example.com"
    And I set the department to "Engineering"
    And I set the salary to 50000.0
    Then the employee id should be 101
    And the first name should be "John"
    And the last name should be "Doe"
    And the designation should be "Software Engineer"
    And the contact number should be "01711111111"
    And the email id should be "john.doe@example.com"
    And the department should be "Engineering"
    And the salary should be 50000.0

  Scenario: A newly created employee record has default values
    Given a new employee record
    Then the employee id should be 0
    And the salary should be 0.0
    And the first name should be null

  Scenario Outline: Updating the salary field
    Given a new employee record
    When I set the salary to <salary>
    Then the salary should be <salary>

    Examples:
      | salary   |
      | 0.0      |
      | 1000.0   |
      | 99999.99 |
