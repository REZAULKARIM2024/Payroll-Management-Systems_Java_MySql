package com.payroll.cucumber;

import com.payroll.AuthService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for login_auth.feature.
 */
public class AuthServiceSteps {

    private boolean loginResult;

    @When("I attempt to login with username {string} and password {string}")
    public void iAttemptToLoginWithUsernameAndPassword(String username, String password) {
        loginResult = AuthService.authenticate(username, password);
    }

    @Then("the login should be successful")
    public void theLoginShouldBeSuccessful() {
        assertTrue(loginResult);
    }

    @Then("the login should be unsuccessful")
    public void theLoginShouldBeUnsuccessful() {
        assertFalse(loginResult);
    }
}
