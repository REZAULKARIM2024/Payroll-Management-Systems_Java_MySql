package com.payroll;

/**
 * Pure validation/parsing logic extracted from
 * {@link DeleteEmployeeTab#deleteEmployee()} so it can be unit tested
 * without a Swing UI or a database connection.
 */
public final class DeleteEmployeeHelper {

    /** SQL used by {@link DeleteEmployeeTab} to delete an employee by ID. */
    public static final String DELETE_SQL = "DELETE FROM employees WHERE employee_id=?";

    private DeleteEmployeeHelper() {
    }

    /**
     * @return true if the given Employee ID text field value is null/blank
     *         (whitespace-only counts as blank).
     */
    public static boolean isBlank(String idText) {
        return idText == null || idText.trim().isEmpty();
    }

    /**
     * Parses the Employee ID text into an int.
     *
     * @throws NumberFormatException if {@code idText} is not a valid integer
     *         (callers should check {@link #isBlank(String)} first).
     */
    public static int parseEmployeeId(String idText) {
        return Integer.parseInt(idText.trim());
    }
}
