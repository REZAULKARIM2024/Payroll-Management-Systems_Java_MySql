Feature: Search Employee Query Logic
  As the payroll system
  I want to decide whether a search keyword is an Employee ID or a name
  and build the correct SQL/pattern for the Search Employee tab

  Scenario Outline: Detecting numeric Employee ID vs name keyword
    When I check whether the search keyword "<keyword>" is a numeric Employee ID
    Then it should be considered <result>

    Examples:
      | keyword | result    |
      | 1       | numeric   |
      | 0007    | numeric   |
      | John    | non-numeric |
      |         | non-numeric |
      | 12John  | non-numeric |

  Scenario: ID search SQL shape
    When I build the ID search SQL
    Then the SQL should filter by employee_id and active employees

  Scenario: Name search SQL shape
    When I build the name search SQL
    Then the SQL should filter by uppercase first name and active employees

  Scenario Outline: Building the name search pattern
    When I build the name search pattern for keyword "<keyword>"
    Then the pattern should be "<pattern>"

    Examples:
      | keyword | pattern |
      | john    | %JOHN%  |
      |         | %%      |
