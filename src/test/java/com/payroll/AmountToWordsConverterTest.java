package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 unit tests for {@link AmountToWordsConverter}.
 * Validates the "Amount in words" / cheque text shown on the Payslip tab.
 */
class AmountToWordsConverterTest {

    @Test
    @DisplayName("শূন্য টাকা হলে 'Zero Dollars' আসবে")
    void zeroAmount() {
        assertEquals("Zero Dollars", AmountToWordsConverter.convert(0));
    }

    @Test
    @DisplayName("দশমিক ছাড়া পূর্ণ সংখ্যা ঠিকভাবে শব্দে রূপান্তর হবে")
    void wholeDollarAmount() {
        assertEquals("One Hundred Dollars", AmountToWordsConverter.convert(100));
    }

    @Test
    @DisplayName("দশমিক (পয়সা) সহ Amount-এ 'and ... Cents' যুক্ত হবে")
    void amountWithCents() {
        assertEquals("One Thousand Two Hundred Thirty Four Dollars and Fifty Six Cents",
                AmountToWordsConverter.convert(1234.56));
    }

    @Test
    @DisplayName("Hundred / Thousand / Million boundary গুলো সঠিকভাবে কাজ করছে কিনা")
    void boundaryGroups() {
        assertEquals("One Hundred Dollars", AmountToWordsConverter.convert(100));
        assertEquals("One Thousand Dollars", AmountToWordsConverter.convert(1000));
        assertEquals("One Million Dollars", AmountToWordsConverter.convert(1_000_000));
        // bug fix: billions were previously dropped
        assertEquals("One Billion Dollars", AmountToWordsConverter.convert(1_000_000_000));
    }

    @Test
    @DisplayName("Net Salary ঋণাত্মক হলে exception আসবে (payslip-এ কখনো negative amount দেখানো উচিত নয়)")
    void negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> AmountToWordsConverter.convert(-50));
    }

    @Test
    @DisplayName("Rounding edge case: 12.999 -> 'Thirteen Dollars'")
    void roundingEdgeCase_centsRoundUpToNextDollar() {
        assertEquals("Thirteen Dollars", AmountToWordsConverter.convert(12.999));
    }

    @Test
    @DisplayName("দুই অংকের সংখ্যা (Tens) সঠিকভাবে কাজ করছে")
    void tensNumbers() {
        assertEquals("Forty Two Dollars", AmountToWordsConverter.convert(42));
        assertEquals("Nineteen Dollars", AmountToWordsConverter.convert(19));
    }
}