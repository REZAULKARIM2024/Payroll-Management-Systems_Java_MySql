package com.payroll;

import javax.swing.*;
import java.awt.*;

public class RefreshTab extends JPanel {

    private static final long serialVersionUID = 1L;

    public RefreshTab(ViewEmployeeTab viewTab, UpdateEmployeeTab updateTab) {

        setLayout(new BorderLayout(30, 30));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // ================================
        // HEADER
        // ================================
        JLabel lblInfo = new JLabel(
                "<html><h1>Refresh Employee Data</h1>" +
                "<p style='font-size:14px;'>Click the button below to reload all employee records<br>" +
                "and clear the Update Employee editor fields.</p></html>"
        );

        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblInfo, BorderLayout.NORTH);

        // ================================
        // REFRESH BUTTON
        // ================================
        JButton btnRefresh = new JButton("🔄  Refresh Now");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnRefresh.setPreferredSize(new Dimension(200, 45));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.CENTER);

        // ================================
        // STATUS LABEL
        // ================================
        JLabel lblStatus = new JLabel(" ");
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(lblStatus, BorderLayout.SOUTH);

        // ================================
        // BUTTON ACTION
        // ================================
        btnRefresh.addActionListener(e -> {
            try {

                if (viewTab != null) {
                    viewTab.reloadEmployees();   // Now valid
                }

                if (updateTab != null) {
                    updateTab.clearFields();
                }

                lblStatus.setText("✅ Employee data refreshed successfully!");
                lblStatus.setForeground(new Color(0, 128, 0));

            } catch (Exception ex) {
                lblStatus.setText("❌ Error refreshing data: " + ex.getMessage());
                lblStatus.setForeground(Color.RED);
            }
        });
    }
}
