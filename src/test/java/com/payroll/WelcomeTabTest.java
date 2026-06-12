package com.payroll;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JPanelFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Component;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AssertJ-Swing UI-automation tests for {@link WelcomeTab}.
 *
 * NOTE: these tests drive a real {@code java.awt.Robot} against an on-screen
 * (non-headless) window, so they are tagged {@code ui} and need a display.
 * Run with: {@code mvn test -Dgroups=ui} (or just {@code mvn test} on a
 * machine that has a display - they are included by default).
 *
 * "Exit" panel is intentionally NOT clicked anywhere here because it calls
 * {@code System.exit(0)}, which would kill the test JVM. That behaviour is
 * documented as a manual-only check in TEST_PLAN_GUI_TABS.md.
 */
@Tag("ui")
class WelcomeTabTest {

    private FrameFixture window;
    private JTabbedPane tabbedPane;

    @BeforeEach
    void setUp() {
        // Minimal JTabbedPane: only some of WelcomeTab's target tab names
        // exist here, so we can also test the "tab not found" negative case.
        tabbedPane = GuiActionRunner.execute(() -> {
            JTabbedPane tp = new JTabbedPane();
            tp.addTab("Welcome", new JPanel());
            tp.addTab("Login", new JPanel());
            tp.addTab("View Employees", new JPanel());
            tp.addTab("Refresh", new JPanel());
            return tp;
        });

        WelcomeTab welcomeTab = GuiActionRunner.execute(() -> new WelcomeTab(tabbedPane));

        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame("WelcomeTabTest");
            f.add(welcomeTab);
            f.pack();
            return f;
        });

        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Test
    @DisplayName("Smoke: ৯টি ক্লিকেবল প্যানেলের লেবেল রেন্ডার হয়")
    void smoke_allNineClickablePanelLabelsAreRendered() {
        String[] expectedLabels = {
                "Login", "Add Employee", "View Employees", "Update Employee",
                "Payslip", "Delete Employee", "Search Employee", "Refresh", "Exit"
        };
        for (String text : expectedLabels) {
            window.label(new GenericTypeMatcher<JLabel>(JLabel.class) {
                @Override
                protected boolean isMatching(JLabel label) {
                    return text.equals(label.getText());
                }
            }).requireVisible();
        }
    }

    @Test
    @DisplayName("Sanity (positive): Login প্যানেলে ক্লিক করলে Login ট্যাবে সুইচ হয়")
    void clickingLoginPanel_switchesTabbedPaneToLoginTab() {
        clickPanelByLabel("Login");

        assertThat(tabbedPane.getSelectedIndex()).isEqualTo(tabbedPane.indexOfTab("Login"));
    }

    @Test
    @DisplayName("Sanity (positive): View Employees প্যানেলে ক্লিক করলে View Employees ট্যাবে সুইচ হয়")
    void clickingViewEmployeesPanel_switchesTabbedPaneToViewEmployeesTab() {
        clickPanelByLabel("View Employees");

        assertThat(tabbedPane.getSelectedIndex()).isEqualTo(tabbedPane.indexOfTab("View Employees"));
    }

    @Test
    @DisplayName("Sanity (positive): Refresh প্যানেলে ক্লিক করলে Refresh ট্যাবে সুইচ হয়")
    void clickingRefreshPanel_switchesTabbedPaneToRefreshTab() {
        clickPanelByLabel("Refresh");

        assertThat(tabbedPane.getSelectedIndex()).isEqualTo(tabbedPane.indexOfTab("Refresh"));
    }

    @Test
    @DisplayName("Regression (negative): যে ট্যাব tabbedPane-এ নেই, তার প্যানেলে ক্লিক করলে কিছু পরিবর্তন হয় না, exception ছুঁড়বে না")
    void clickingPanelForTabNotInTabbedPane_doesNothingAndDoesNotThrow() {
        // "Add Employee" exists as a WelcomeTab button but NOT in our
        // minimal tabbedPane fixture -> indexOfTab() returns -1, so
        // tabbedPane.setSelectedIndex(...) must not be called.
        int selectedBefore = tabbedPane.getSelectedIndex();

        clickPanelByLabel("Add Employee");

        assertThat(tabbedPane.getSelectedIndex()).isEqualTo(selectedBefore);
    }

    /** Finds the JPanel that directly contains a JLabel with the given text and clicks it. */
    private void clickPanelByLabel(String text) {
        JPanelFixture panel = window.panel(new GenericTypeMatcher<JPanel>(JPanel.class) {
            @Override
            protected boolean isMatching(JPanel p) {
                for (Component c : p.getComponents()) {
                    if (c instanceof JLabel && text.equals(((JLabel) c).getText())) {
                        return true;
                    }
                }
                return false;
            }
        });
        panel.click();
    }
}
