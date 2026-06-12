package com.payroll;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Pure formatting/parsing logic extracted from {@link PayslipTab}.
 *
 * Covers: (1) parsing/validating the MM/dd/yyyy pay-date entered by the
 * user and converting it to the yyyy-MM-dd format used in SQL, and (2) the
 * "amount in words" -> cheque-words transformation (uppercasing, replacing
 * "DOLLARS"/"AND"/"CENTS"). Extracting these lets us unit/Cucumber test the
 * exact same logic PayslipTab uses, without needing Swing or a database.
 */
public final class PayslipFormatter {

    // QA FIX: STRICT resolver style rejects impossible calendar dates such
    // as 11/31/2025 instead of silently clamping them to 11/30/2025.
    // NOTE: STRICT resolver style requires "u" (year) instead of "y"
    // (year-of-era), otherwise even valid dates fail with
    // "Unable to obtain LocalDate from TemporalAccessor" because the
    // year-of-era field cannot resolve without an era in STRICT mode.
    public static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter SQL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private PayslipFormatter() {
    }

    /**
     * Converts a "MM/dd/yyyy" pay-date string to "yyyy-MM-dd" for SQL.
     *
     * @throws DateTimeParseException if {@code inputDate} is not a valid MM/dd/yyyy date.
     */
    public static String toSqlDate(String inputDate) {
        LocalDate parsed = LocalDate.parse(inputDate, INPUT_DATE_FORMAT);
        return parsed.format(SQL_DATE_FORMAT);
    }

    /** True if {@code inputDate} can be parsed as a valid MM/dd/yyyy date. */
    public static boolean isValidInputDate(String inputDate) {
        try {
            toSqlDate(inputDate);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Converts the English "amount in words" (e.g. from
     * {@link AmountToWordsConverter#convert(double)}) into the wording used
     * on the printed cheque: uppercased, with "DOLLARS" removed and "CENTS"
     * replaced by "/100".
     */
    public static String toChequeWords(String amountInWords) {
        if (amountInWords == null) {
            return "";
        }
        return amountInWords.toUpperCase()
                .replace(" DOLLARS", "")
                .replace(" AND ", " AND ")
                .replace(" CENTS", "/100");
    }
}
