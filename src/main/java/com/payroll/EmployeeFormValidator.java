package com.payroll;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * Centralized, testable validation/formatting logic for employee form data.
 *
 * EXTRACTED FROM: AddEmployeeTab (originally private fields/methods on that
 * class). UpdateEmployeeTab, SearchEmployeeTab, etc. should also use this
 * class so that name/phone/account-number/date validation rules are
 * guaranteed identical everywhere - the same reasoning that led to
 * PayrollCalculator and AmountToWordsConverter being extracted.
 *
 * This class has NO Swing/AWT/JDBC dependencies, so it is 100% unit and
 * Cucumber testable.
 */
public final class EmployeeFormValidator {

    /**
     * Input date format used on the Add/Update Employee screens (MM/dd/yyyy).
     *
     * QA FIX: uses ResolverStyle.STRICT so that impossible calendar dates
     * such as 11/31/2025 (November has only 30 days) are REJECTED with a
     * DateTimeParseException instead of being silently clamped to 11/30/2025
     * by the default SMART resolver style.
     */
    // NOTE: STRICT resolver style requires "u" (year) instead of "y"
    // (year-of-era), otherwise even valid dates fail with
    // "Unable to obtain LocalDate from TemporalAccessor" because the
    // year-of-era field cannot resolve without an era in STRICT mode.
    public static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Date format expected by the MySQL DATE column (yyyy-MM-dd). */
    public static final DateTimeFormatter SQL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // QA FIX (Bug #5): allow letters, spaces, apostrophes and hyphens so
    // names like "O'Brien" or "Anne-Marie" are accepted.
    private static final Pattern NAME_VALIDATOR = Pattern.compile("^[a-zA-Z][a-zA-Z'\\-\\s]*$");

    // QA FIX (Bug #6): contact numbers and bank account numbers are stored
    // as TEXT, so they are validated as text, not parsed as Long. This
    // allows leading zeros (e.g. 01712345678), an optional "+" country
    // code, and dashes/spaces. Account numbers may also be alphanumeric
    // (IBAN style).
    private static final Pattern PHONE_VALIDATOR = Pattern.compile("^\\+?[0-9][0-9\\-\\s]{6,19}$");
    private static final Pattern ACCOUNT_NO_VALIDATOR = Pattern.compile("^[A-Za-z0-9\\-]{4,34}$");

    private EmployeeFormValidator() {
        // utility class - no instances
    }

    /**
     * Validates a person's name: must be non-empty, and contain only
     * letters, spaces, apostrophes or hyphens (must start with a letter).
     */
    public static boolean isValidName(String name) {
        return name != null && !name.isEmpty() && NAME_VALIDATOR.matcher(name).matches();
    }

    /**
     * Validates a contact/phone number as TEXT: digits, optional leading
     * "+", spaces or hyphens, 7-20 characters total.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_VALIDATOR.matcher(phone).matches();
    }

    /**
     * Validates a bank account number as TEXT: 4-34 letters/digits/hyphens
     * (IBAN-style accounts may be alphanumeric).
     */
    public static boolean isValidAccountNo(String accountNo) {
        return accountNo != null && ACCOUNT_NO_VALIDATOR.matcher(accountNo).matches();
    }

    /**
     * Converts a date string in MM/dd/yyyy format (as entered on the
     * Add/Update Employee screens) to yyyy-MM-dd (as required by the MySQL
     * DATE column).
     *
     * @throws DateTimeParseException if {@code inputDate} is not a valid
     *         date in MM/dd/yyyy format.
     */
    public static String toSqlDate(String inputDate) {
        LocalDate date = LocalDate.parse(inputDate, INPUT_DATE_FORMAT);
        return date.format(SQL_DATE_FORMAT);
    }

    /**
     * Same as {@link #toSqlDate(String)} but returns {@code true}/{@code false}
     * instead of throwing, for simple validity checks.
     */
    public static boolean isValidInputDate(String inputDate) {
        try {
            LocalDate.parse(inputDate, INPUT_DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
