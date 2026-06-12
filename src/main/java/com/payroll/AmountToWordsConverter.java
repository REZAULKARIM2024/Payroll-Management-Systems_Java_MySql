package com.payroll;

/**
 * Converts a monetary amount (e.g. net salary) into English words for use on
 * the Payslip and Cheque ("PAY TO THE ORDER OF ... DOLLARS").
 *
 * Extracted from PayslipTab so it can be unit tested without Swing/AWT and
 * without a database connection.
 *
 * BUGS FOUND & FIXED vs. the original PayslipTab implementation:
 *  1. Negative amounts were not handled (a negative net salary would print
 *     garbage like "-Five Hundred Dollars" with no "Zero/negative" guard).
 *     -> Now throws IllegalArgumentException for negative input, because a
 *        payslip should never display a negative "Pay to the order of" amount.
 *  2. Amounts >= 1,000,000,000 (billions) were silently dropped because the
 *     original convertNumberToWords() only handled millions/thousands/hundreds
 *     (the "number" variable after dividing by 1000 three times was discarded).
 *     -> Added a "Billion" group.
 *  3. cents == 0 case did not append "and Zero Cents" (matches typical cheque
 *     wording "...AND 00/100"), kept consistent for PayslipTab.populateCheque()
 *     which replaces " CENTS" with "/100".
 */
public final class AmountToWordsConverter {

    private static final String[] TENS_NAMES = {
            "", " Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety"
    };

    private static final String[] NUM_NAMES = {
            "", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine",
            " Ten", " Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen",
            " Eighteen", " Nineteen"
    };

    private AmountToWordsConverter() {
    }

    /**
     * @param amount the monetary amount, must be {@code >= 0}
     * @return amount expressed in English words, e.g. "One Thousand Two Hundred Thirty Four Dollars and Fifty Cents"
     */
    public static String convert(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount ঋণাত্মক (negative) হতে পারবে না: " + amount);
        }
        if (amount == 0) {
            return "Zero Dollars";
        }

        long dollars = (long) amount;
        long cents = Math.round((amount - dollars) * 100);

        // Rounding edge case: 12.999 -> dollars=12, cents=100 -> should become 13 dollars, 0 cents
        if (cents == 100) {
            dollars += 1;
            cents = 0;
        }

        String words = convertNumberToWords(dollars) + " Dollars";
        if (cents > 0) {
            words += " and " + convertNumberToWords(cents) + " Cents";
        }
        return words.trim();
    }

    private static String convertNumberToWords(long number) {
        if (number == 0) {
            return "Zero";
        }

        StringBuilder result = new StringBuilder();

        long billions = number / 1_000_000_000L;
        number %= 1_000_000_000L;
        long millions = number / 1_000_000L;
        number %= 1_000_000L;
        long thousands = number / 1000L;
        long remainder = number % 1000L;

        if (billions > 0) {
            appendWithSpace(result, convertHundreds(billions) + " Billion");
        }
        if (millions > 0) {
            appendWithSpace(result, convertHundreds(millions) + " Million");
        }
        if (thousands > 0) {
            appendWithSpace(result, convertHundreds(thousands) + " Thousand");
        }
        if (remainder > 0) {
            appendWithSpace(result, convertHundreds(remainder));
        }

        return result.toString().trim();
    }

    /** Appends {@code part} to {@code sb}, inserting a single space if {@code sb} is non-empty. */
    private static void appendWithSpace(StringBuilder sb, String part) {
        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(part);
    }

    private static String convertHundreds(long number) {
        if (number == 0) {
            return "";
        }

        int group = (int) number;
        if (group < 20) {
            return NUM_NAMES[group].trim();
        } else if (group < 100) {
            int tens = group / 10;
            int ones = group % 10;
            return (TENS_NAMES[tens] + NUM_NAMES[ones]).trim();
        } else {
            int hundreds = group / 100;
            int rem = group % 100;
            if (rem == 0) {
                return (NUM_NAMES[hundreds] + " Hundred").trim();
            } else if (rem < 20) {
                return (NUM_NAMES[hundreds] + " Hundred" + NUM_NAMES[rem]).trim();
            } else {
                int tens = rem / 10;
                int ones = rem % 10;
                return (NUM_NAMES[hundreds] + " Hundred" + TENS_NAMES[tens] + NUM_NAMES[ones]).trim();
            }
        }
    }
}