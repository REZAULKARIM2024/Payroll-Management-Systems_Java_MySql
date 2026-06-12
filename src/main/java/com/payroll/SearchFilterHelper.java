package com.payroll;

import javax.swing.RowFilter;
import java.util.regex.PatternSyntaxException;

/**
 * Extracted from {@link PayrollTabbedGUI}'s live employee-search box.
 *
 * QA FIX: the original code called
 * {@code RowFilter.regexFilter("(?i)" + text, 1, 2)} directly inside a
 * {@code DocumentListener}, with no protection against
 * {@link PatternSyntaxException}. Typing an unbalanced regex like
 * {@code "("} or {@code "*"} into the search box would throw and (depending
 * on the Swing event dispatch thread's exception handler) could leave the
 * table filter in a broken state or print a stack trace to stderr.
 *
 * {@link #safeRegexFilter(String, int...)} reproduces the original
 * "(?i)" + text, columns) behaviour but degrades gracefully: an invalid
 * pattern simply returns {@code null} (no filter / show all rows) instead
 * of throwing.
 */
public final class SearchFilterHelper {

    private SearchFilterHelper() {
    }

    /**
     * @param text    the raw text typed by the user into the search box
     * @param columns the table column indexes to filter on
     * @return a case-insensitive regex {@link RowFilter} for {@code text}, or
     *         {@code null} if {@code text} is null/empty or is not a valid
     *         regular expression.
     */
    public static <M, I> RowFilter<M, I> safeRegexFilter(String text, int... columns) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return RowFilter.regexFilter("(?i)" + text, columns);
        } catch (PatternSyntaxException e) {
            return null;
        }
    }
}
