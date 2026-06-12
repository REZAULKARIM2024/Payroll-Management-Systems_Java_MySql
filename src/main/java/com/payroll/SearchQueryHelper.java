package com.payroll;

/**
 * Pure helper logic extracted from {@link SearchEmployeeTab}.
 *
 * SearchEmployeeTab decides, based on the user's search keyword, whether to
 * search by exact numeric employee ID or by a case-insensitive partial match
 * on first name. That decision plus the SQL/parameter construction is pure
 * logic with no Swing/JDBC dependency, so it is extracted here to allow
 * unit/Cucumber testing (following the EmployeeFormValidator pattern).
 */
public final class SearchQueryHelper {

    private static final String SELECT_COLUMNS =
            "employee_id, first_name, last_name, designation, contact_no, email_id, department, pay_rate_hourly";

    private SearchQueryHelper() {
    }

    /** True if the trimmed keyword consists only of digits (treated as an Employee ID search). */
    public static boolean isNumericId(String keyword) {
        if (keyword == null) {
            return false;
        }
        return keyword.matches("\\d+");
    }

    /** SQL used when the keyword is numeric (search by employee_id). */
    public static String buildIdSearchSql() {
        return "SELECT " + SELECT_COLUMNS + " FROM employees WHERE employee_id = ? AND active = 1";
    }

    /** SQL used when the keyword is non-numeric (case-insensitive first-name search). */
    public static String buildNameSearchSql() {
        return "SELECT " + SELECT_COLUMNS + " FROM employees WHERE UPPER(first_name) LIKE ? AND active = 1";
    }

    /** Builds the LIKE pattern (e.g. "JOHN" -> "%JOHN%") used for the name search parameter. */
    public static String buildNamePattern(String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        return "%" + keyword.toUpperCase() + "%";
    }
}
