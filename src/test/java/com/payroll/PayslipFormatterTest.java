package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 unit tests for {@link PayslipFormatter} (extracted from PayslipTab).
 */
class PayslipFormatterTest {

    @ParameterizedTest
    @DisplayName("MM/dd/yyyy পে-ডেট সঠিকভাবে yyyy-MM-dd তে রূপান্তরিত হবে")
    @CsvSource({
            "10/11/2025, 2025-10-11",
            "01/01/2000, 2000-01-01",
            "12/31/1999, 1999-12-31"
    })
    void toSqlDate_convertsValidDates(String input, String expected) {
        assertEquals(expected, PayslipFormatter.toSqlDate(input));
    }

    @ParameterizedTest
    @DisplayName("অবৈধ পে-ডেট ফরম্যাট DateTimeParseException ছুঁড়বে")
    @ValueSource(strings = {"2025-10-11", "13/01/2025", "11/31/2025", "not-a-date", ""})
    void toSqlDate_throwsForInvalidDates(String input) {
        assertThrows(DateTimeParseException.class, () -> PayslipFormatter.toSqlDate(input));
    }

    @Test
    @DisplayName("isValidInputDate বৈধ/অবৈধ পে-ডেটের জন্য true/false রিটার্ন করবে, exception নয়")
    void isValidInputDate_returnsBooleanWithoutThrowing() {
        assertTrue(PayslipFormatter.isValidInputDate("10/11/2025"));
        assertFalse(PayslipFormatter.isValidInputDate("2025-10-11"));
        assertFalse(PayslipFormatter.isValidInputDate("13/40/2025"));
    }

    @ParameterizedTest
    @DisplayName("Amount-in-words থেকে cheque-words: DOLLARS বাদ, CENTS -> /100, uppercase")
    @CsvSource({
            "'one thousand two hundred dollars', 'ONE THOUSAND TWO HUNDRED'",
            "'one dollar and fifty cents', 'ONE DOLLAR AND FIFTY/100'",
            "'zero dollars', 'ZERO'"
    })
    void toChequeWords_formatsAsExpected(String input, String expected) {
        assertEquals(expected, PayslipFormatter.toChequeWords(input));
    }

    @Test
    @DisplayName("null amount-in-words হলে cheque-words খালি স্ট্রিং হবে, exception নয়")
    void toChequeWords_nullInput_returnsEmptyString() {
        assertEquals("", PayslipFormatter.toChequeWords(null));
    }
}
