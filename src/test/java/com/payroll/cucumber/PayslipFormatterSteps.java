package com.payroll.cucumber;

import com.payroll.PayslipFormatter;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Step definitions for payslip_formatting.feature.
 */
public class PayslipFormatterSteps {

    private String inputDate;
    private String sqlDate;
    private Exception dateError;
    private String chequeWords;

    @When("I convert the pay date {string} to SQL format")
    public void iConvertThePayDateToSqlFormat(String inputDate) {
        this.inputDate = inputDate;
        dateError = null;
        try {
            sqlDate = PayslipFormatter.toSqlDate(inputDate);
        } catch (DateTimeParseException e) {
            dateError = e;
        }
    }

    @Then("the payslip SQL date should be {string}")
    public void thePayslipSqlDateShouldBe(String expected) {
        assertEquals(expected, sqlDate);
    }

    @Then("a payslip date conversion error should occur")
    public void aPayslipDateConversionErrorShouldOccur() {
        assertNotNull(dateError, "Expected a DateTimeParseException for input: " + inputDate);
    }

    @When("I format the cheque words for {string}")
    public void iFormatTheChequeWordsFor(String amountInWords) {
        chequeWords = PayslipFormatter.toChequeWords(amountInWords);
    }

    @Then("the cheque words should be {string}")
    public void theChequeWordsShouldBe(String expected) {
        assertEquals(expected, chequeWords);
    }
}
