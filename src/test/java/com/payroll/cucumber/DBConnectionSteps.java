package com.payroll.cucumber;

import com.payroll.DBConnection;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Step definitions for db_connection.feature
 *
 * NOTE: If MySQL is not running / credentials in DBConnection are not
 * configured for this machine, this scenario is SKIPPED (not failed) via
 * org.junit.jupiter.api.Assumptions, so it won't break `mvn test` on
 * machines without a database set up.
 */
public class DBConnectionSteps {

    private Connection connection;
    private SQLException connectionError;

    @When("I try to open a database connection")
    public void iTryToOpenADatabaseConnection() {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            connectionError = e;
        }
    }

    @Then("the connection should be successful and open")
    public void theConnectionShouldBeSuccessfulAndOpen() throws SQLException {
        assumeTrue(connectionError == null,
                "Skipping: could not connect to MySQL (" +
                        (connectionError != null ? connectionError.getMessage() : "") +
                        "). Configure DBConnection / start MySQL to run this scenario.");

        assertNotNull(connection);
        assertFalse(connection.isClosed());
        connection.close();
    }
}
