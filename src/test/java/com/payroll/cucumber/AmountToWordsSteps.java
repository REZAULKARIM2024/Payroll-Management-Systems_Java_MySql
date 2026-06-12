package com.payroll.cucumber;

import com.payroll.AmountToWordsConverter;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Step definitions for amount_to_words.feature
 */
public class AmountToWordsSteps {

    private double amount;
    private String result;

    @When("I convert the amount {double} to words")
    public void iConvertTheAmountToWords(double amount) {
        this.amount = amount;
        try {
            this.result = AmountToWordsConverter.convert(amount);
        } catch (IllegalArgumentException e) {
            this.result = null;
        }
    }

    @Then("the result should be {string}")
    public void theResultShouldBe(String expected) {
        assertEquals(expected, result);
    }

    @Then("an amount conversion IllegalArgumentException should be thrown")
    public void anIllegalArgumentExceptionShouldBeThrown() {
        assertThrows(IllegalArgumentException.class, () -> AmountToWordsConverter.convert(amount));
    }
}
