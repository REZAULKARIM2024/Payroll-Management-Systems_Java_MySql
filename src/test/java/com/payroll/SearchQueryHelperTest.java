package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 unit tests for {@link SearchQueryHelper} (extracted from SearchEmployeeTab).
 */
class SearchQueryHelperTest {

    @ParameterizedTest
    @DisplayName("পুরোপুরি সংখ্যাযুক্ত keyword Employee ID search হিসেবে গণ্য হবে")
    @ValueSource(strings = {"1", "42", "0007", "123456"})
    void numericKeywords_areTreatedAsId(String keyword) {
        assertTrue(SearchQueryHelper.isNumericId(keyword));
    }

    @ParameterizedTest
    @DisplayName("নাম, খালি স্ট্রিং, বা মিশ্র keyword ID search নয়")
    @ValueSource(strings = {"John", "12John", "John12", "", " ", "12.5", "-5"})
    void nonNumericKeywords_areNotTreatedAsId(String keyword) {
        assertFalse(SearchQueryHelper.isNumericId(keyword));
    }

    @Test
    @DisplayName("null keyword ID search নয়")
    void nullKeyword_isNotNumericId() {
        assertFalse(SearchQueryHelper.isNumericId(null));
    }

    @Test
    @DisplayName("ID search SQL-এ employee_id এবং active=1 থাকবে")
    void idSearchSql_hasExpectedShape() {
        String sql = SearchQueryHelper.buildIdSearchSql();
        assertTrue(sql.contains("employee_id = ?"));
        assertTrue(sql.contains("active = 1"));
    }

    @Test
    @DisplayName("Name search SQL-এ UPPER(first_name) LIKE এবং active=1 থাকবে")
    void nameSearchSql_hasExpectedShape() {
        String sql = SearchQueryHelper.buildNameSearchSql();
        assertTrue(sql.contains("UPPER(first_name) LIKE ?"));
        assertTrue(sql.contains("active = 1"));
    }

    @Test
    @DisplayName("Name pattern uppercase করে % wildcard দিয়ে wrap হবে")
    void namePattern_isUppercasedAndWrapped() {
        assertEquals("%JOHN%", SearchQueryHelper.buildNamePattern("john"));
        assertEquals("%%", SearchQueryHelper.buildNamePattern(""));
        assertEquals("%%", SearchQueryHelper.buildNamePattern(null));
    }
}
