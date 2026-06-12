package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DeleteEmployeeHelper}, extracted from
 * {@link DeleteEmployeeTab#deleteEmployee()} for testability without a
 * Swing UI or database.
 */
class DeleteEmployeeHelperTest {

    @Test
    @DisplayName("Negative: null Employee ID is blank")
    void isBlank_null_returnsTrue() {
        assertTrue(DeleteEmployeeHelper.isBlank(null));
    }

    @Test
    @DisplayName("Negative: empty string Employee ID is blank")
    void isBlank_emptyString_returnsTrue() {
        assertTrue(DeleteEmployeeHelper.isBlank(""));
    }

    @Test
    @DisplayName("Negative: whitespace-only Employee ID is blank")
    void isBlank_whitespaceOnly_returnsTrue() {
        assertTrue(DeleteEmployeeHelper.isBlank("   "));
    }

    @Test
    @DisplayName("Positive: a non-empty numeric string is not blank")
    void isBlank_numericString_returnsFalse() {
        assertFalse(DeleteEmployeeHelper.isBlank("123"));
    }

    @Test
    @DisplayName("Positive: valid numeric Employee ID parses correctly")
    void parseEmployeeId_validNumber_returnsInt() {
        assertEquals(123, DeleteEmployeeHelper.parseEmployeeId("123"));
    }

    @Test
    @DisplayName("Positive: numeric Employee ID with surrounding whitespace parses correctly")
    void parseEmployeeId_numberWithWhitespace_returnsInt() {
        assertEquals(42, DeleteEmployeeHelper.parseEmployeeId("  42  "));
    }

    @Test
    @DisplayName("Negative: non-numeric Employee ID throws NumberFormatException")
    void parseEmployeeId_nonNumeric_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class,
                () -> DeleteEmployeeHelper.parseEmployeeId("abc"));
    }

    @Test
    @DisplayName("Negative: decimal Employee ID throws NumberFormatException")
    void parseEmployeeId_decimalValue_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class,
                () -> DeleteEmployeeHelper.parseEmployeeId("12.5"));
    }

    @Test
    @DisplayName("Sanity: DELETE_SQL targets employees table by employee_id")
    void deleteSql_isExpectedStatement() {
        assertEquals("DELETE FROM employees WHERE employee_id=?", DeleteEmployeeHelper.DELETE_SQL);
    }
}
