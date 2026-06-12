package com.payroll;

/**
 * Pure authentication-check logic extracted from {@link LoginTab}.
 *
 * Previously the username/password check ("admin" / "admin123") was hard
 * coded inside the button click listener of LoginTab, which made it
 * impossible to unit-test without spinning up a Swing UI. Extracting it
 * here follows the same pattern used for {@link EmployeeFormValidator}.
 */
public final class AuthService {

    // NOTE: kept identical to the previous hard-coded values in LoginTab.
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";

    private AuthService() {
    }

    /**
     * Returns true only if both username and password match the configured
     * credentials. Null inputs are treated as a non-match (never throw).
     */
    public static boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }
}
