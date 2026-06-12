package com.payroll;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.JLabel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AssertJ-Swing UI-automation tests for {@link RefreshTab}.
 *
 * NOTE: drives a real java.awt.Robot against an on-screen window, so this is
 * tagged {@code ui} (same as WelcomeTabTest) and needs a display.
 *
 * No mocking library is configured in this project, so instead of Mockito we
 * use small subclasses of {@link ViewEmployeeTab} / {@link UpdateEmployeeTab}
 * that override the methods RefreshTab calls and record whether they ran.
 */
@Tag("ui")
class RefreshTabTest {

    private FrameFixture window;

    /** Records whether reloadEmployees() was invoked, without touching the DB/mainGui. */
    private static class TrackingViewEmployeeTab extends ViewEmployeeTab {
        boolean reloaded = false;

        TrackingViewEmployeeTab() {
            super(null);
        }

        @Override
        public void reloadEmployees() {
            reloaded = true;
        }
    }

    /** Records whether clearFields() was invoked, without touching its real fields/DB. */
    private static class TrackingUpdateEmployeeTab extends UpdateEmployeeTab {
        boolean cleared = false;

        @Override
        public void clearFields() {
            cleared = true;
        }
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
            window = null;
        }
    }

    private FrameFixture showRefreshTab(ViewEmployeeTab viewTab, UpdateEmployeeTab updateTab) {
        RefreshTab refreshTab = GuiActionRunner.execute(() -> new RefreshTab(viewTab, updateTab));

        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame("RefreshTabTest");
            f.add(refreshTab);
            f.pack();
            return f;
        });

        FrameFixture fixture = new FrameFixture(frame);
        fixture.show();
        return fixture;
    }

    @Test
    @DisplayName("Positive: clicking 'Refresh Now' calls reloadEmployees()/clearFields() and shows success message")
    void clickingRefreshNow_callsReloadAndClearAndShowsSuccess() {
        TrackingViewEmployeeTab viewTab = GuiActionRunner.execute(TrackingViewEmployeeTab::new);
        TrackingUpdateEmployeeTab updateTab = GuiActionRunner.execute(TrackingUpdateEmployeeTab::new);

        window = showRefreshTab(viewTab, updateTab);

        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("🔄  Refresh Now")).click();

        assertThat(viewTab.reloaded).isTrue();
        assertThat(updateTab.cleared).isTrue();

        window.label(new org.assertj.swing.core.GenericTypeMatcher<JLabel>(JLabel.class) {
            @Override
            protected boolean isMatching(JLabel label) {
                return "✅ Employee data refreshed successfully!".equals(label.getText());
            }
        }).requireVisible();
    }

    @Test
    @DisplayName("Negative/Regression: clicking 'Refresh Now' with both tabs null does not throw and still shows success")
    void clickingRefreshNow_withNullTabs_doesNotThrowAndShowsSuccess() {
        window = showRefreshTab(null, null);

        window.button(org.assertj.swing.core.matcher.JButtonMatcher.withText("🔄  Refresh Now")).click();

        // RefreshTab guards both calls with null checks, so this should
        // succeed exactly like the happy path - no exception, success label.
        window.label(new org.assertj.swing.core.GenericTypeMatcher<JLabel>(JLabel.class) {
            @Override
            protected boolean isMatching(JLabel label) {
                return "✅ Employee data refreshed successfully!".equals(label.getText());
            }
        }).requireVisible();
    }

    @Test
    @DisplayName("Smoke: header and initial status label are rendered")
    void smoke_headerAndStatusLabelAreRendered() {
        window = showRefreshTab(null, null);

        window.label(new org.assertj.swing.core.GenericTypeMatcher<JLabel>(JLabel.class) {
            @Override
            protected boolean isMatching(JLabel label) {
                return label.getText() != null && label.getText().contains("Refresh Employee Data");
            }
        }).requireVisible();
    }
}
