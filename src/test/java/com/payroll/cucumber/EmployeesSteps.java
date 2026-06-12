package com.payroll.cucumber;

import com.payroll.Employees;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Step definitions for employees_model.feature
 */
public class EmployeesSteps {

    private Employees employee;

    @Given("a new employee record")
    public void aNewEmployeeRecord() {
        employee = new Employees();
    }

    @When("I set the employee id to {int}")
    public void iSetTheEmployeeIdTo(int id) {
        employee.setEmployeeId(id);
    }

    @And("I set the first name to {string}")
    public void iSetTheFirstNameTo(String firstName) {
        employee.setFirstName(firstName);
    }

    @And("I set the last name to {string}")
    public void iSetTheLastNameTo(String lastName) {
        employee.setLastName(lastName);
    }

    @And("I set the designation to {string}")
    public void iSetTheDesignationTo(String designation) {
        employee.setDesignation(designation);
    }

    @And("I set the contact number to {string}")
    public void iSetTheContactNumberTo(String contactNo) {
        employee.setContactNo(contactNo);
    }

    @And("I set the email id to {string}")
    public void iSetTheEmailIdTo(String emailId) {
        employee.setEmailId(emailId);
    }

    @And("I set the department to {string}")
    public void iSetTheDepartmentTo(String department) {
        employee.setDepartment(department);
    }

    @And("I set the salary to {double}")
    public void iSetTheSalaryTo(double salary) {
        employee.setSalary(salary);
    }

    @Then("the employee id should be {int}")
    public void theEmployeeIdShouldBe(int expected) {
        assertEquals(expected, employee.getEmployeeId());
    }

    @Then("the first name should be {string}")
    public void theFirstNameShouldBe(String expected) {
        assertEquals(expected, employee.getFirstName());
    }

    @Then("the first name should be null")
    public void theFirstNameShouldBeNull() {
        assertNull(employee.getFirstName());
    }

    @Then("the last name should be {string}")
    public void theLastNameShouldBe(String expected) {
        assertEquals(expected, employee.getLastName());
    }

    @Then("the designation should be {string}")
    public void theDesignationShouldBe(String expected) {
        assertEquals(expected, employee.getDesignation());
    }

    @Then("the contact number should be {string}")
    public void theContactNumberShouldBe(String expected) {
        assertEquals(expected, employee.getContactNo());
    }

    @Then("the email id should be {string}")
    public void theEmailIdShouldBe(String expected) {
        assertEquals(expected, employee.getEmailId());
    }

    @Then("the department should be {string}")
    public void theDepartmentShouldBe(String expected) {
        assertEquals(expected, employee.getDepartment());
    }

    @Then("the salary should be {double}")
    public void theSalaryShouldBe(double expected) {
        assertEquals(expected, employee.getSalary(), 0.001);
    }
}
