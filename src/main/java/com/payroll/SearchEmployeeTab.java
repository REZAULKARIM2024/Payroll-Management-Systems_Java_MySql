package com.payroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchEmployeeTab extends JPanel {
    private JTextField txtSearch;
    private JButton btnSearch;
    private JTable table;
    private DefaultTableModel model;

    public SearchEmployeeTab() {
        setLayout(new BorderLayout());

        // Top Search Panel
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Search by Employee ID or First Name:"));
        txtSearch = new JTextField(15);
        topPanel.add(txtSearch);
        btnSearch = new JButton("Search");
        topPanel.add(btnSearch);

        add(topPanel, BorderLayout.NORTH);

        // Table Setup
        model = new DefaultTableModel();
        table = new JTable(model);
        
        // Columns must match the order in the SELECT statement
        model.setColumnIdentifiers(new String[]{
            "ID", "First Name", "Last Name", "Designation", 
            "Contact No", "Email ID", "Department", "Hourly Pay Rate" 
        });
        
        // Make table read-only
        table.setEnabled(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Action Listener
        btnSearch.addActionListener(e -> searchEmployee());
    }

    private void searchEmployee() {
        String keyword = txtSearch.getText().trim();
        if(keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID or First Name to search.");
            return;
        }
        
        model.setRowCount(0);

        // QA FIX: the "is this an ID or a name search?" decision and the SQL
        // it produces are now in SearchQueryHelper (pure logic, unit/Cucumber
        // tested), following the EmployeeFormValidator extraction pattern.
        try (Connection conn = DBConnection.getConnection()) {
            String sql;
            PreparedStatement ps;

            // Check if the keyword is purely numeric (for ID search)
            if (SearchQueryHelper.isNumericId(keyword)) {
                sql = SearchQueryHelper.buildIdSearchSql();
                ps = conn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(keyword));

            } else {
                // Search by first name (case-insensitive partial match)
                sql = SearchQueryHelper.buildNameSearchSql();
                ps = conn.prepareStatement(sql);
                ps.setString(1, SearchQueryHelper.buildNamePattern(keyword));
            }

            ResultSet rs = ps.executeQuery();

            if (!rs.isBeforeFirst()) {    
                JOptionPane.showMessageDialog(this, "No matching employees found.");
                return;
            }

            // Populate table with results
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("employee_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("designation"),
                    rs.getString("contact_no"),
                    rs.getString("email_id"),
                    rs.getString("department"),
                    rs.getDouble("pay_rate_hourly")
                });
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Invalid number entered for Employee ID search.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}