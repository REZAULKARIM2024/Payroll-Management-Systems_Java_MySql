package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Plain JUnit5 tests for {@link UIHelper}. Runs fine in headless mode since
 * it only touches {@link UIManager} defaults, no Robot/window needed.
 */
class UIHelperTest {

    @Test
    @DisplayName("setUIFont() সব FontUIResource এন্ট্রি দেওয়া ফন্টে পরিবর্তন করে")
    void setUIFont_replacesAllFontUIResourceEntriesWithGivenFont() {
        FontUIResource newFont = new FontUIResource("Segoe UI", Font.PLAIN, 14);

        UIHelper.setUIFont(newFont);

        Enumeration<Object> keys = UIManager.getDefaults().keys();
        boolean foundAtLeastOneFontEntry = false;
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                foundAtLeastOneFontEntry = true;
                assertEquals(newFont, value, "UIManager entry '" + key + "' was not updated");
            }
        }
        assertTrue(foundAtLeastOneFontEntry, "Expected at least one FontUIResource entry in UIManager defaults");
    }

    @Test
    @DisplayName("setUIFont() একাধিকবার কল করলেও exception ছুঁড়বে না")
    void setUIFont_doesNotThrow_whenCalledMultipleTimes() {
        FontUIResource first = new FontUIResource("Arial", Font.BOLD, 12);
        FontUIResource second = new FontUIResource("Tahoma", Font.ITALIC, 16);

        assertDoesNotThrow(() -> {
            UIHelper.setUIFont(first);
            UIHelper.setUIFont(second);
        });

        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                assertEquals(second, value, "Last setUIFont() call should win for entry '" + key + "'");
            }
        }
    }
}
