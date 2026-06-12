package com.payroll.cucumber;

import com.payroll.SearchQueryHelper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for search_employee.feature.
 */
public class SearchQueryHelperSteps {

    private boolean isNumeric;
    private String sql;
    private String pattern;

    @When("I check whether the search keyword {string} is a numeric Employee ID")
    public void iCheckWhetherTheSearchKeywordIsNumeric(String keyword) {
        isNumeric = SearchQueryHelper.isNumericId(keyword);
    }

    @Then("it should be considered numeric")
    public void itShouldBeConsideredNumeric() {
        assertTrue(isNumeric);
    }

    @Then("it should be considered non-numeric")
    public void itShouldBeConsideredNonNumeric() {
        assertFalse(isNumeric);
    }

    @When("I build the ID search SQL")
    public void iBuildTheIdSearchSql() {
        sql = SearchQueryHelper.buildIdSearchSql();
    }

    @Then("the SQL should filter by employee_id and active employees")
    public void theSqlShouldFilterByEmployeeIdAndActiveEmployees() {
        assertTrue(sql.contains("employee_id = ?"));
        assertTrue(sql.contains("active = 1"));
    }

    @When("I build the name search SQL")
    public void iBuildTheNameSearchSql() {
        sql = SearchQueryHelper.buildNameSearchSql();
    }

    @Then("the SQL should filter by uppercase first name and active employees")
    public void theSqlShouldFilterByUppercaseFirstNameAndActiveEmployees() {
        assertTrue(sql.contains("UPPER(first_name) LIKE ?"));
        assertTrue(sql.contains("active = 1"));
    }

    @When("I build the name search pattern for keyword {string}")
    public void iBuildTheNameSearchPatternForKeyword(String keyword) {
        pattern = SearchQueryHelper.buildNamePattern(keyword);
    }

    @Then("the pattern should be {string}")
    public void thePatternShouldBe(String expected) {
        assertEquals(expected, pattern);
    }
}
