package com.payroll.cucumber;

import com.payroll.EmployeeFormValidator;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for employee_form_validation.feature
 */
public class EmployeeFormValidatorSteps {

    private boolean nameValid;
    private boolean phoneValid;
    private boolean accountNoValid;

    private String inputDate;
    private String sqlDate;
    private Exception dateConversionError;

    @When("I validate the name {string}")
    public void iValidateTheName(String name) {
        nameValid = EmployeeFormValidator.isValidName(name);
    }

    @Then("the name should be considered valid")
    public void theNameShouldBeConsideredValid() {
        assertTrue(nameValid);
    }

    @Then("the name should be considered invalid")
    public void theNameShouldBeConsideredInvalid() {
        assertFalse(nameValid);
    }

    @When("I validate the contact number {string}")
    public void iValidateTheContactNumber(String phone) {
        phoneValid = EmployeeFormValidator.isValidPhone(phone);
    }

    @Then("the contact number should be considered valid")
    public void theContactNumberShouldBeConsideredValid() {
        assertTrue(phoneValid);
    }

    @Then("the contact number should be considered invalid")
    public void theContactNumberShouldBeConsideredInvalid() {
        assertFalse(phoneValid);
    }

    @When("I validate the account number {string}")
    public void iValidateTheAccountNumber(String accountNo) {
        accountNoValid = EmployeeFormValidator.isValidAccountNo(accountNo);
    }

    @Then("the account number should be considered valid")
    public void theAccountNumberShouldBeConsideredValid() {
        assertTrue(accountNoValid);
    }

    @Then("the account number should be considered invalid")
    public void theAccountNumberShouldBeConsideredInvalid() {
        assertFalse(accountNoValid);
    }

    @When("I convert the input date {string} to SQL format")
    public void iConvertTheInputDateToSqlFormat(String inputDate) {
        this.inputDate = inputDate;
        try {
            sqlDate = EmployeeFormValidator.toSqlDate(inputDate);
        } catch (DateTimeParseException e) {
            dateConversionError = e;
        }
    }

    @Then("the SQL date should be {string}")
    public void theSqlDateShouldBe(String expected) {
        assertEquals(expected, sqlDate);
    }

    @Then("a date conversion error should occur")
    public void aDateConversionErrorShouldOccur() {
        assertNotNull(dateConversionError, "Expected a DateTimeParseException for input: " + inputDate);
    }
}
