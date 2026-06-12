package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SearchFilterHelper}, which fixes the
 * PatternSyntaxException bug in PayrollTabbedGUI's live employee search box
 * (invalid regex like "(" used to throw uncaught).
 *
 * Plain JUnit5, headless-safe: DefaultTableModel/RowFilter need no display.
 */
class SearchFilterHelperTest {

    private DefaultTableModel sampleModel() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "First Name", "Last Name"}, 0);
        model.addRow(new Object[]{1, "John", "Doe"});
        model.addRow(new Object[]{2, "Jane", "Smith"});
        return model;
    }

    @Test
    @DisplayName("Positive: empty search text returns null (no filter / show all rows)")
    void emptyText_returnsNullFilter() {
        assertNull(SearchFilterHelper.safeRegexFilter("", 1, 2));
    }

    @Test
    @DisplayName("Positive: null search text returns null (no filter)")
    void nullText_returnsNullFilter() {
        assertNull(SearchFilterHelper.safeRegexFilter(null, 1, 2));
    }

    @Test
    @DisplayName("Positive: valid search text returns a working case-insensitive filter")
    void validText_returnsFilterThatMatchesCaseInsensitively() throws Exception {
        RowFilter<Object, Object> filter = SearchFilterHelper.safeRegexFilter("john", 1, 2);
        assertNotNull(filter);

        DefaultTableModel model = sampleModel();
        // Row 0 = John Doe -> should match "john" (case-insensitive)
        RowFilter.Entry<? extends Object, ? extends Object> entry = entryFor(model, 0);
        assertTrue(filter.include(entry));

        // Row 1 = Jane Smith -> should not match "john"
        RowFilter.Entry<? extends Object, ? extends Object> entry2 = entryFor(model, 1);
        assertFalse(filter.include(entry2));
    }

    @Test
    @DisplayName("Negative: invalid regex '(' returns null instead of throwing PatternSyntaxException")
    void invalidRegex_unbalancedParen_returnsNullAndDoesNotThrow() {
        assertDoesNotThrow(() -> {
            RowFilter<Object, Object> filter = SearchFilterHelper.safeRegexFilter("(", 1, 2);
            assertNull(filter);
        });
    }

    @Test
    @DisplayName("Negative: invalid regex '*' returns null instead of throwing PatternSyntaxException")
    void invalidRegex_danglingStar_returnsNullAndDoesNotThrow() {
        assertDoesNotThrow(() -> {
            RowFilter<Object, Object> filter = SearchFilterHelper.safeRegexFilter("*", 1, 2);
            assertNull(filter);
        });
    }

    /** Builds a RowFilter.Entry view over a single model row, for use with RowFilter.include(). */
    @SuppressWarnings("unchecked")
    private RowFilter.Entry<Object, Object> entryFor(DefaultTableModel model, int rowIndex) {
        return new RowFilter.Entry<Object, Object>() {
            @Override
            public Object getModel() {
                return model;
            }

            @Override
            public int getValueCount() {
                return model.getColumnCount();
            }

            @Override
            public Object getValue(int index) {
                return model.getValueAt(rowIndex, index);
            }

            @Override
            public Object getIdentifier() {
                return rowIndex;
            }
        };
    }
}
