package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 unit tests for {@link EmployeeFormValidator}.
 */
class EmployeeFormValidatorTest {

    // ---------------------------------------------------------------
    // Name validation
    // ---------------------------------------------------------------

    @ParameterizedTest
    @DisplayName("বৈধ নাম গ্রহণ করা হবে (letters, spaces, apostrophes, hyphens)")
    @ValueSource(strings = {"John", "Anne-Marie", "O'Brien", "Mary Jane", "Al"})
    void validNames_areAccepted(String name) {
        assertTrue(EmployeeFormValidator.isValidName(name));
    }

    @ParameterizedTest
    @DisplayName("খালি, সংখ্যা বা অবৈধ অক্ষরযুক্ত নাম reject হবে")
    @ValueSource(strings = {"", "123John", "John123", "John_Doe", "John@Doe", " John"})
    void invalidNames_areRejected(String name) {
        assertFalse(EmployeeFormValidator.isValidName(name));
    }

    @Test
    @DisplayName("null নাম reject হবে")
    void nullName_isRejected() {
        assertFalse(EmployeeFormValidator.isValidName(null));
    }

    // ---------------------------------------------------------------
    // Phone validation
    // ---------------------------------------------------------------

    @ParameterizedTest
    @DisplayName("বৈধ ফোন নাম্বার গ্রহণ করা হবে (leading zero, +country code, dashes/spaces)")
    @ValueSource(strings = {"01712345678", "+8801712345678", "017-1234-5678", "1234567"})
    void validPhoneNumbers_areAccepted(String phone) {
        assertTrue(EmployeeFormValidator.isValidPhone(phone));
    }

    @ParameterizedTest
    @DisplayName("খুব ছোট, খুব বড়, বা অক্ষরযুক্ত ফোন নাম্বার reject হবে")
    @ValueSource(strings = {"", "123", "12345abc", "123456789012345678901", "+"})
    void invalidPhoneNumbers_areRejected(String phone) {
        assertFalse(EmployeeFormValidator.isValidPhone(phone));
    }

    @Test
    @DisplayName("null ফোন নাম্বার reject হবে")
    void nullPhone_isRejected() {
        assertFalse(EmployeeFormValidator.isValidPhone(null));
    }

    // ---------------------------------------------------------------
    // Account number validation
    // ---------------------------------------------------------------

    @ParameterizedTest
    @DisplayName("বৈধ অ্যাকাউন্ট নাম্বার (4-34 alphanumeric/hyphen, IBAN-style) গ্রহণ করা হবে")
    @ValueSource(strings = {"1234", "GB29-NWBK-6016-1331-9268-19", "ACC1234567890", "0001-0002"})
    void validAccountNumbers_areAccepted(String accountNo) {
        assertTrue(EmployeeFormValidator.isValidAccountNo(accountNo));
    }

    @ParameterizedTest
    @DisplayName("খুব ছোট, খুব বড়, বা অবৈধ চিহ্নযুক্ত অ্যাকাউন্ট নাম্বার reject হবে")
    @ValueSource(strings = {"", "123", "abc@123", "12345678901234567890123456789012345"})
    void invalidAccountNumbers_areRejected(String accountNo) {
        assertFalse(EmployeeFormValidator.isValidAccountNo(accountNo));
    }

    @Test
    @DisplayName("null অ্যাকাউন্ট নাম্বার reject হবে")
    void nullAccountNo_isRejected() {
        assertFalse(EmployeeFormValidator.isValidAccountNo(null));
    }

    // ---------------------------------------------------------------
    // Date conversion (MM/dd/yyyy -> yyyy-MM-dd)
    // ---------------------------------------------------------------

    @ParameterizedTest
    @DisplayName("MM/dd/yyyy ফরম্যাট সঠিকভাবে yyyy-MM-dd তে রূপান্তরিত হবে")
    @CsvSource({
            "11/15/2025, 2025-11-15",
            "01/01/2000, 2000-01-01",
            "12/31/1999, 1999-12-31"
    })
    void toSqlDate_convertsValidDates(String input, String expected) {
        assertEquals(expected, EmployeeFormValidator.toSqlDate(input));
    }

    @ParameterizedTest
    @DisplayName("অবৈধ ফরম্যাট বা মান DateTimeParseException ছুঁড়বে")
    @ValueSource(strings = {"2025-11-15", "13/01/2025", "11/31/2025", "not-a-date", ""})
    void toSqlDate_throwsForInvalidDates(String input) {
        assertThrows(java.time.format.DateTimeParseException.class,
                () -> EmployeeFormValidator.toSqlDate(input));
    }

    @Test
    @DisplayName("isValidInputDate বৈধ/অবৈধ তারিখের জন্য true/false রিটার্ন করবে")
    void isValidInputDate_returnsBooleanWithoutThrowing() {
        assertTrue(EmployeeFormValidator.isValidInputDate("11/15/2025"));
        assertFalse(EmployeeFormValidator.isValidInputDate("2025-11-15"));
        assertFalse(EmployeeFormValidator.isValidInputDate("13/40/2025"));
    }
}
