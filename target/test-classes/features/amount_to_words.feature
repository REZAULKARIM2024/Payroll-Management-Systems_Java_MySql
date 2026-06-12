Feature: Amount to Words Conversion
  As a payroll system
  I want to convert a monetary amount into English words
  So that the Payslip and Cheque show the correct "Pay to the order of ... Dollars" text

  Scenario: Zero amount converts to "Zero Dollars"
    When I convert the amount 0 to words
    Then the result should be "Zero Dollars"

  Scenario: Whole dollar amount converts without cents
    When I convert the amount 100 to words
    Then the result should be "One Hundred Dollars"

  Scenario: Amount with cents includes "and ... Cents"
    When I convert the amount 1234.56 to words
    Then the result should be "One Thousand Two Hundred Thirty Four Dollars and Fifty Six Cents"

  Scenario Outline: Hundred, Thousand, Million and Billion boundaries
    When I convert the amount <amount> to words
    Then the result should be "<words>"

    Examples:
      | amount     | words               |
      | 100        | One Hundred Dollars |
      | 1000       | One Thousand Dollars |
      | 1000000    | One Million Dollars |
      | 1000000000 | One Billion Dollars |

  Scenario: Negative amount throws an exception
    When I convert the amount -50 to words
    Then an amount conversion IllegalArgumentException should be thrown

  Scenario Outline: Two-digit numbers convert correctly
    When I convert the amount <amount> to words
    Then the result should be "<words>"

    Examples:
      | amount | words              |
      | 42     | Forty Two Dollars  |
      | 19     | Nineteen Dollars   |

  Scenario: Rounding edge case rounds cents up to the next dollar
    When I convert the amount 12.999 to words
    Then the result should be "Thirteen Dollars"
