Feature: Payslip Date and Cheque Formatting
  As the payroll system
  I want to validate the pay-date entered on the Payslip tab
  and format the cheque amount-in-words correctly

  Scenario Outline: Converting pay date to SQL format
    When I convert the pay date "<inputDate>" to SQL format
    Then the payslip SQL date should be "<sqlDate>"

    Examples:
      | inputDate  | sqlDate    |
      | 10/11/2025 | 2025-10-11 |
      | 01/01/2000 | 2000-01-01 |

  Scenario Outline: Rejecting invalid pay dates
    When I convert the pay date "<inputDate>" to SQL format
    Then a payslip date conversion error should occur

    Examples:
      | inputDate   |
      | 2025-10-11  |
      | 13/01/2025  |
      | not-a-date  |

  Scenario Outline: Formatting amount-in-words for the cheque
    When I format the cheque words for "<amountInWords>"
    Then the cheque words should be "<chequeWords>"

    Examples:
      | amountInWords                  | chequeWords                  |
      | one thousand two hundred dollars | ONE THOUSAND TWO HUNDRED    |
      | one dollar and fifty cents     | ONE DOLLAR AND FIFTY/100      |
      | zero dollars                   | ZERO                          |
