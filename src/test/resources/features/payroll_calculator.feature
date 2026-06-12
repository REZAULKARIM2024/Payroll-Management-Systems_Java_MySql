Feature: Payroll Calculation
  As a payroll system
  I want to calculate gross salary, deductions and net salary correctly
  So that employee pay is accurate and consistent across Add/Update/Payslip screens

  Background:
    Given a pay rate of 10.0 per hour

  Scenario: Normal hours under 40 produce only normal salary
    Given the total hours worked is 35
    When I calculate the payroll
    Then the normal salary should be 350.0
    And the gross salary should be 350.0

  Scenario: Exactly 40 hours is the boundary for normal salary
    Given a pay rate of 15.0 per hour
    And the total hours worked is 40
    When I calculate the payroll
    Then the normal salary should be 600.0
    And the overtime salary should be 0.0

  Scenario: Total hours above 40 are capped at 40 for normal salary
    Given the total hours worked is 50
    When I calculate the payroll
    Then the normal salary should be 400.0

  Scenario: Overtime hours are paid at 1.5x the pay rate
    Given the total hours worked is 40
    And overtime hours of 5
    When I calculate the payroll
    Then the overtime salary should be 75.0
    And the gross salary should be 475.0

  Scenario: Doubletime hours are paid at 2x the pay rate
    Given the total hours worked is 40
    And doubletime hours of 4
    When I calculate the payroll
    Then the doubletime salary should be 80.0
    And the gross salary should be 480.0

  Scenario: Extended, holiday paid and special work hours use the normal pay rate
    Given extended hours of 2
    And holiday paid hours of 3
    And special work hours of 4
    When I calculate the payroll
    Then the extended salary should be 20.0
    And the holiday paid salary should be 30.0
    And the special work salary should be 40.0
    And the gross salary should be 90.0

  Scenario: Bonus amount is added to gross salary and is taxed
    Given the total hours worked is 40
    And a bonus amount of 100
    When I calculate the payroll
    Then the gross salary should be 500.0
    And the tax deduction should be 100.0
    And the medical insurance should be 50.0
    And the net salary should be 350.0

  Scenario: All zero inputs result in all zero outputs
    Given the total hours worked is 0
    When I calculate the payroll
    Then the gross salary should be 0.0
    And the tax deduction should be 0.0
    And the medical insurance should be 0.0
    And the net salary should be 0.0

  Scenario Outline: Negative inputs are rejected
    Given a pay rate of <payRate>
    And the total hours worked is <totalHours>
    And overtime hours of <overtimeHours>
    And doubletime hours of <doubletimeHours>
    And extended hours of <extendedHours>
    And holiday paid hours of <holidayHours>
    And special work hours of <specialHours>
    And a bonus amount of <bonus>
    When I calculate the payroll
    Then a payroll IllegalArgumentException should be thrown

    Examples:
      | payRate | totalHours | overtimeHours | doubletimeHours | extendedHours | holidayHours | specialHours | bonus |
      | -1      | 0          | 0             | 0               | 0             | 0            | 0            | 0     |
      | 10      | -1         | 0             | 0               | 0             | 0            | 0            | 0     |
      | 10      | 0          | -1            | 0               | 0             | 0            | 0            | 0     |
      | 10      | 0          | 0             | -1              | 0             | 0            | 0            | 0     |
      | 10      | 0          | 0             | 0               | -1            | 0            | 0            | 0     |
      | 10      | 0          | 0             | 0               | 0             | -1           | 0            | 0     |
      | 10      | 0          | 0             | 0               | 0             | 0            | -1           | 0     |
      | 10      | 0          | 0             | 0               | 0             | 0            | 0            | -1    |
