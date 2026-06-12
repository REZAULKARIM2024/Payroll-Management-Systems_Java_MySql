package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.JLabel;
import java.awt.Component;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Plain JUnit5 tests for {@link ViewEmployeeTab}. No Robot/display needed -
 * constructing a JPanel and reading its components works headless.
 */
class ViewEmployeeTabTest {

    @Test
    @DisplayName("Smoke: constructing with null mainGui does not throw and shows the confirmation label")
    void constructor_withNullMainGui_doesNotThrowAndAddsConfirmationLabel() {
        ViewEmployeeTab tab = assertDoesNotThrow(() -> new ViewEmployeeTab(null));

        boolean foundLabel = false;
        for (Component c : tab.getComponents()) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getText() != null && label.getText().contains("Employee Data View")) {
                    foundLabel = true;
                }
            }
        }
        assertTrue(foundLabel, "Expected ViewEmployeeTab to contain the 'Employee Data View' confirmation label");
    }

    @Test
    @DisplayName("Positive/Sanity: reloadEmployees() with null mainGui does not throw (no-op)")
    void reloadEmployees_withNullMainGui_doesNotThrow() {
        ViewEmployeeTab tab = new ViewEmployeeTab(null);

        assertDoesNotThrow(tab::reloadEmployees);
    }

    @Test
    @DisplayName("Negative/Regression: reloadEmployees() called multiple times with null mainGui remains safe")
    void reloadEmployees_calledRepeatedly_remainsSafe() {
        ViewEmployeeTab tab = new ViewEmployeeTab(null);

        assertDoesNotThrow(() -> {
            tab.reloadEmployees();
            tab.reloadEmployees();
            tab.reloadEmployees();
        });
    }
}
