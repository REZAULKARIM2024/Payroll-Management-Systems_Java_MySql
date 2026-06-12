Feature: Login Authentication
  As the payroll system
  I want to validate username/password on the Login tab
  So that only authorized users can access the system

  Scenario Outline: Login attempts
    When I attempt to login with username "<username>" and password "<password>"
    Then the login should be <result>

    Examples:
      | username  | password  | result      |
      | admin     | admin123  | successful  |
      | admin     | wrongpass | unsuccessful|
      | wronguser | admin123  | unsuccessful|
      | wronguser | wrongpass | unsuccessful|
      | Admin     | admin123  | unsuccessful|
      |           |           | unsuccessful|
