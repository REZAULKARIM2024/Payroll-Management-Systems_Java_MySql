package com.payroll.cucumber;

import com.payroll.PayrollCalculator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Step definitions for payroll_calculator.feature
 */
public class PayrollCalculatorSteps {

    private static final double DELTA = 0.001;

    private double payRate = 0.0;
    private double totalHours = 0.0;
    private double overtimeHours = 0.0;
    private double doubletimeHours = 0.0;
    private double extendedHours = 0.0;
    private double holidayPaidHours = 0.0;
    private double specialWorkHours = 0.0;
    private double bonusAmount = 0.0;

    private PayrollCalculator.PayResult result;
    private Exception thrownException;

    @Given("a pay rate of {double} per hour")
    public void aPayRateOfPerHour(double payRate) {
        this.payRate = payRate;
    }

    // Allows "a pay rate of -1" without the trailing "per hour" (used in Scenario Outlines)
    @Given("a pay rate of {double}")
    public void aPayRateOf(double payRate) {
        this.payRate = payRate;
    }

    @Given("the total hours worked is {double}")
    public void theTotalHoursWorkedIs(double totalHours) {
        this.totalHours = totalHours;
    }

    @And("overtime hours of {double}")
    public void overtimeHoursOf(double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    @And("doubletime hours of {double}")
    public void doubletimeHoursOf(double doubletimeHours) {
        this.doubletimeHours = doubletimeHours;
    }

    @And("extended hours of {double}")
    public void extendedHoursOf(double extendedHours) {
        this.extendedHours = extendedHours;
    }

    @And("holiday paid hours of {double}")
    public void holidayPaidHoursOf(double holidayPaidHours) {
        this.holidayPaidHours = holidayPaidHours;
    }

    @And("special work hours of {double}")
    public void specialWorkHoursOf(double specialWorkHours) {
        this.specialWorkHours = specialWorkHours;
    }

    @And("a bonus amount of {double}")
    public void aBonusAmountOf(double bonusAmount) {
        this.bonusAmount = bonusAmount;
    }

    @When("I calculate the payroll")
    public void iCalculateThePayroll() {
        try {
            result = PayrollCalculator.calculate(
                    payRate, totalHours, overtimeHours, doubletimeHours,
                    extendedHours, holidayPaidHours, specialWorkHours, bonusAmount);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the normal salary should be {double}")
    public void theNormalSalaryShouldBe(double expected) {
        assertEquals(expected, result.normalSalary, DELTA);
    }

    @Then("the overtime salary should be {double}")
    public void theOvertimeSalaryShouldBe(double expected) {
        assertEquals(expected, result.overtimeSalary, DELTA);
    }

    @Then("the doubletime salary should be {double}")
    public void theDoubletimeSalaryShouldBe(double expected) {
        assertEquals(expected, result.doubletimeSalary, DELTA);
    }

    @Then("the extended salary should be {double}")
    public void theExtendedSalaryShouldBe(double expected) {
        assertEquals(expected, result.extendedSalary, DELTA);
    }

    @Then("the holiday paid salary should be {double}")
    public void theHolidayPaidSalaryShouldBe(double expected) {
        assertEquals(expected, result.holidayPaidSalary, DELTA);
    }

    @Then("the special work salary should be {double}")
    public void theSpecialWorkSalaryShouldBe(double expected) {
        assertEquals(expected, result.specialWorkSalary, DELTA);
    }

    @Then("the gross salary should be {double}")
    public void theGrossSalaryShouldBe(double expected) {
        assertEquals(expected, result.grossSalary, DELTA);
    }

    @Then("the tax deduction should be {double}")
    public void theTaxDeductionShouldBe(double expected) {
        assertEquals(expected, result.taxDeduction, DELTA);
    }

    @Then("the medical insurance should be {double}")
    public void theMedicalInsuranceShouldBe(double expected) {
        assertEquals(expected, result.medicalInsurance, DELTA);
    }

    @Then("the net salary should be {double}")
    public void theNetSalaryShouldBe(double expected) {
        assertEquals(expected, result.netSalary, DELTA);
    }

    @Then("a payroll IllegalArgumentException should be thrown")
    public void anIllegalArgumentExceptionShouldBeThrown() {
        assertThrows(IllegalArgumentException.class, () -> {
            if (thrownException != null) {
                throw thrownException;
            }
            // If no exception was thrown during "When", force calculate() again
            // so assertThrows can capture it directly.
            PayrollCalculator.calculate(
                    payRate, totalHours, overtimeHours, doubletimeHours,
                    extendedHours, holidayPaidHours, specialWorkHours, bonusAmount);
        });
    }
}
