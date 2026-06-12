Feature: Employee Form Validation
  As the payroll system
  I want to validate names, phone numbers, bank account numbers and dates
  entered on the Add/Update Employee screens
  So that only well-formed data reaches the database

  # ---------------------------------------------------------------
  # Name validation
  # ---------------------------------------------------------------
  Scenario Outline: Validating employee names
    When I validate the name "<name>"
    Then the name should be considered <validity>

    Examples:
      | name        | validity |
      | John        | valid    |
      | Anne-Marie  | valid    |
      | O'Brien     | valid    |
      | Mary Jane   | valid    |
      |             | invalid  |
      | 123John     | invalid  |
      | John123     | invalid  |
      | John_Doe    | invalid  |
      | John@Doe    | invalid  |

  # ---------------------------------------------------------------
  # Phone number validation
  # ---------------------------------------------------------------
  Scenario Outline: Validating contact numbers
    When I validate the contact number "<phone>"
    Then the contact number should be considered <validity>

    Examples:
      | phone           | validity |
      | 01712345678     | valid    |
      | +8801712345678  | valid    |
      | 017-1234-5678   | valid    |
      | 1234567         | valid    |
      |                 | invalid  |
      | 123             | invalid  |
      | 12345abc        | invalid  |

  # ---------------------------------------------------------------
  # Account number validation
  # ---------------------------------------------------------------
  Scenario Outline: Validating bank account numbers
    When I validate the account number "<accountNo>"
    Then the account number should be considered <validity>

    Examples:
      | accountNo                     | validity |
      | 1234                          | valid    |
      | GB29-NWBK-6016-1331-9268-19   | valid    |
      | ACC1234567890                 | valid    |
      |                               | invalid  |
      | 123                           | invalid  |
      | abc@123                       | invalid  |

  # ---------------------------------------------------------------
  # Date conversion: MM/dd/yyyy -> yyyy-MM-dd
  # ---------------------------------------------------------------
  Scenario Outline: Converting input dates to SQL date format
    When I convert the input date "<inputDate>" to SQL format
    Then the SQL date should be "<sqlDate>"

    Examples:
      | inputDate  | sqlDate    |
      | 11/15/2025 | 2025-11-15 |
      | 01/01/2000 | 2000-01-01 |
      | 12/31/1999 | 1999-12-31 |

  Scenario Outline: Converting an invalid input date raises an error
    When I convert the input date "<inputDate>" to SQL format
    Then a date conversion error should occur

    Examples:
      | inputDate   |
      | 2025-11-15  |
      | 13/01/2025  |
      | 11/31/2025  |
      | not-a-date  |
