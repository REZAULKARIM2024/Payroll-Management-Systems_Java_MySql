package com.payroll;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateEmployeeTab extends JPanel {

    private static final long serialVersionUID = 1L;

    // ===============================================
    // CONSTANTS
    // ===============================================
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String[] DEPARTMENTS = {"HR", "IT", "Finance", "Sales", "Marketing", "Operations"};
    private static final String[] DESIGNATIONS = {"Manager", "Senior Developer", "Junior Developer", "Analyst", "Clerk"};

    // ===============================================
    // UI COMPONENTS
    // ===============================================
    private JTextField txtEmployeeId, txtFirstName, txtLastName, txtEmail, txtContactNo;
    private JTextField txtPayRateHourly, txtHireDate, txtTotalHours, txtSalary;
    private JTextField txtOvertimeHours, txtDoubletimeHours, txtPayDate;
    private JTextField txtSsn, txtCustomer, txtBankName, txtAccountNo;
    private JTextField txtExtendedHours, txtHolidayPaidHours, txtSpecialWorkHours;
    private JTextField txtBonusAmount;
    private JTextField txtCalculation;

    // QA FIX: new read-only output fields so the calculated breakdown
    // (gross / tax / medical / total deductions / net) is visible BEFORE
    // saving, exactly like AddEmployeeTab.
    private JTextField txtGrossSalary, txtTaxDeduction, txtMedicalInsurance, txtTotalDeductions, txtNetSalary;

    private JComboBox<String> cmbActive, cmbDepartment, cmbDesignation;
    private JLabel lblStatus;
    private JButton btnUpdate, btnClear, btnLoad, btnCalculate;

    public UpdateEmployeeTab() {
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("UPDATE EMPLOYEE PAYROLL DATA", SwingConstants.CENTER);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(0, 102, 255));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 4, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Row: Employee ID + Load button
        txtEmployeeId = new JTextField();
        btnLoad = new JButton("Load Employee");
        form.add(new JLabel("Employee ID:"));
        form.add(txtEmployeeId);
        form.add(btnLoad);
        form.add(new JLabel(""));

        // Row: First/Last Name
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        form.add(new JLabel("First Name:"));
        form.add(txtFirstName);
        form.add(new JLabel("Last Name:"));
        form.add(txtLastName);

        // Row: Department / Designation
        cmbDepartment = new JComboBox<>(DEPARTMENTS);
        cmbDesignation = new JComboBox<>(DESIGNATIONS);
        form.add(new JLabel("Department:"));
        form.add(cmbDepartment);
        form.add(new JLabel("Designation:"));
        form.add(cmbDesignation);

        // Row: SSN / Customer
        txtSsn = new JTextField();
        txtCustomer = new JTextField();
        form.add(new JLabel("SSN:"));
        form.add(txtSsn);
        form.add(new JLabel("Customer:"));
        form.add(txtCustomer);

        // Row: Bank Name / Account No
        txtBankName = new JTextField();
        txtAccountNo = new JTextField();
        form.add(new JLabel("Bank Name:"));
        form.add(txtBankName);
        form.add(new JLabel("Account No:"));
        form.add(txtAccountNo);

        // Row: Contact / Email
        txtContactNo = new JTextField();
        txtEmail = new JTextField();
        form.add(new JLabel("Contact No:"));
        form.add(txtContactNo);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);

        // Row: Hire Date / Pay Rate
        txtHireDate = new JTextField();
        txtPayRateHourly = new JTextField();
        form.add(new JLabel("Hire Date (yyyy-MM-dd):"));
        form.add(txtHireDate);
        form.add(new JLabel("Pay Rate (hourly):"));
        form.add(txtPayRateHourly);

        // Row: Active Status / Total Hours
        cmbActive = new JComboBox<>(new String[]{"1", "0"});
        txtTotalHours = new JTextField();
        form.add(new JLabel("Active (1=Yes, 0=No):"));
        form.add(cmbActive);
        form.add(new JLabel("Total Hours:"));
        form.add(txtTotalHours);

        // Row: Overtime / Doubletime Hours
        txtOvertimeHours = new JTextField();
        txtDoubletimeHours = new JTextField();
        form.add(new JLabel("Overtime Hours:"));
        form.add(txtOvertimeHours);
        form.add(new JLabel("Doubletime Hours:"));
        form.add(txtDoubletimeHours);

        // Row: Extended / Holiday Paid Hours
        txtExtendedHours = new JTextField();
        txtHolidayPaidHours = new JTextField();
        form.add(new JLabel("Extended Hours:"));
        form.add(txtExtendedHours);
        form.add(new JLabel("Holiday Paid Hours:"));
        form.add(txtHolidayPaidHours);

        // Row: Special Work Hours / Pay Date
        txtSpecialWorkHours = new JTextField();
        txtPayDate = new JTextField();
        form.add(new JLabel("Special Work Hours:"));
        form.add(txtSpecialWorkHours);
        form.add(new JLabel("Pay Date (yyyy-MM-dd):"));
        form.add(txtPayDate);

        // QA NEW ROW: Bonus Amount (was missing entirely - needed so
        // PayrollCalculator.calculate(...) has a bonus value to work with,
        // and so bonus_amount can be persisted on update).
        txtBonusAmount = new JTextField();
        form.add(new JLabel("Bonus Amount:"));
        form.add(txtBonusAmount);
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        // Row: Salary (view-only) / Calculated Pay (Net Salary summary)
        txtSalary = new JTextField();
        txtSalary.setEditable(false);
        txtCalculation = new JTextField();
        txtCalculation.setEditable(false);
        form.add(new JLabel("Salary (Normal, view-only):"));
        form.add(txtSalary);
        form.add(new JLabel("Net Salary (Calculated):"));
        form.add(txtCalculation);

        // QA NEW ROWS: full breakdown, mirroring AddEmployeeTab so the user
        // can verify gross/tax/medical/total-deductions/net before saving.
        txtGrossSalary = new JTextField();
        txtGrossSalary.setEditable(false);
        txtTaxDeduction = new JTextField();
        txtTaxDeduction.setEditable(false);
        form.add(new JLabel("Gross Salary:"));
        form.add(txtGrossSalary);
        form.add(new JLabel(String.format("Tax Deduction (%.0f%%):", PayrollCalculator.TAX_PERCENTAGE * 100)));
        form.add(txtTaxDeduction);

        txtMedicalInsurance = new JTextField();
        txtMedicalInsurance.setEditable(false);
        txtTotalDeductions = new JTextField();
        txtTotalDeductions.setEditable(false);
        form.add(new JLabel(String.format("Medical Insurance (%.0f%%):", PayrollCalculator.MEDICAL_PERCENTAGE * 100)));
        form.add(txtMedicalInsurance);
        form.add(new JLabel("Total Deductions:"));
        form.add(txtTotalDeductions);

        txtNetSalary = new JTextField();
        txtNetSalary.setEditable(false);
        form.add(new JLabel("Net Salary:"));
        form.add(txtNetSalary);
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        add(form, BorderLayout.CENTER);

        // --- Bottom panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnCalculate = new JButton("Calculate Salary");
        btnUpdate = new JButton("Update Employee");
        btnClear = new JButton("Clear");
        lblStatus = new JLabel("Enter Employee ID and click 'Load Employee'.", SwingConstants.CENTER);

        bottomPanel.add(btnCalculate);
        bottomPanel.add(btnUpdate);
        bottomPanel.add(btnClear);
        bottomPanel.add(lblStatus);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Listeners ---
        btnLoad.addActionListener(e -> fetchEmployeeDetails(txtEmployeeId.getText().trim()));
        btnCalculate.addActionListener(e -> calculateAndDisplayPay());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnClear.addActionListener(e -> clearFields());

        setFieldsEditable(false);
    }

    /**
     * Called from the employee list/table to pre-fill the form for editing.
     */
    public void loadSelectedRow(DefaultTableModel model, int row) {
        Object idVal = model.getValueAt(row, 0);
        if (idVal != null) {
            txtEmployeeId.setText(idVal.toString());
            fetchEmployeeDetails(idVal.toString());
        }
    }

    private void setFieldsEditable(boolean editable) {
        txtFirstName.setEditable(editable);
        txtLastName.setEditable(editable);
        txtEmail.setEditable(editable);
        txtContactNo.setEditable(editable);
        txtPayRateHourly.setEditable(editable);
        txtHireDate.setEditable(editable);
        txtTotalHours.setEditable(editable);
        txtOvertimeHours.setEditable(editable);
        txtDoubletimeHours.setEditable(editable);
        txtPayDate.setEditable(editable);
        txtSsn.setEditable(editable);
        txtCustomer.setEditable(editable);
        txtBankName.setEditable(editable);
        txtAccountNo.setEditable(editable);
        txtExtendedHours.setEditable(editable);
        txtHolidayPaidHours.setEditable(editable);
        txtSpecialWorkHours.setEditable(editable);
        txtBonusAmount.setEditable(editable);

        cmbActive.setEnabled(editable);
        cmbDepartment.setEnabled(editable);
        cmbDesignation.setEnabled(editable);

        btnUpdate.setEnabled(editable);
        btnCalculate.setEnabled(editable);
    }

    /**
     * Loads an employee's full record from the database into the form.
     */
    private void fetchEmployeeDetails(String employeeIdStr) {
        if (employeeIdStr.isEmpty()) {
            lblStatus.setText("❌ Error: Please enter an Employee ID to load.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        int employeeId;
        try {
            employeeId = Integer.parseInt(employeeIdStr);
        } catch (NumberFormatException e) {
            lblStatus.setText("❌ Error: Employee ID must be a number.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        String sql = "SELECT first_name, last_name, designation, department, email_id, contact_no, hire_date, " +
                "pay_rate_hourly, active, Total_hours, overtime_hours, doubletime_hours, salary, pay_date, ssn, " +
                "customer, bank_name, account_no, extended_hours, holidaypaid_hours, specialwork_hours, bonus_amount " +
                "FROM employees WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtFirstName.setText(rs.getString("first_name"));
                    txtLastName.setText(rs.getString("last_name"));
                    cmbDesignation.setSelectedItem(rs.getString("designation"));
                    cmbDepartment.setSelectedItem(rs.getString("department"));
                    txtEmail.setText(rs.getString("email_id"));
                    txtContactNo.setText(rs.getString("contact_no"));

                    java.sql.Date hireDate = rs.getDate("hire_date");
                    txtHireDate.setText(hireDate != null ? hireDate.toString() : "");

                    txtPayRateHourly.setText(String.valueOf(rs.getDouble("pay_rate_hourly")));
                    cmbActive.setSelectedItem(String.valueOf(rs.getInt("active")));
                    txtTotalHours.setText(String.valueOf(rs.getDouble("Total_hours")));
                    txtOvertimeHours.setText(String.valueOf(rs.getDouble("overtime_hours")));
                    txtDoubletimeHours.setText(String.valueOf(rs.getDouble("doubletime_hours")));
                    txtSalary.setText(String.valueOf(rs.getDouble("salary")));

                    java.sql.Date payDate = rs.getDate("pay_date");
                    txtPayDate.setText(payDate != null ? payDate.toString() : "");

                    txtSsn.setText(rs.getString("ssn"));
                    txtCustomer.setText(rs.getString("customer"));
                    txtBankName.setText(rs.getString("bank_name"));
                    txtAccountNo.setText(rs.getString("account_no"));
                    txtExtendedHours.setText(String.valueOf(rs.getDouble("extended_hours")));
                    txtHolidayPaidHours.setText(String.valueOf(rs.getDouble("holidaypaid_hours")));
                    txtSpecialWorkHours.setText(String.valueOf(rs.getDouble("specialwork_hours")));
                    txtBonusAmount.setText(String.valueOf(rs.getDouble("bonus_amount")));

                    setFieldsEditable(true);
                    lblStatus.setText("✅ Employee #" + employeeId + " loaded. You may now edit and Update.");
                    lblStatus.setForeground(new Color(0, 128, 0));

                    // Show the previously calculated breakdown immediately.
                    calculateAndDisplayPay();
                } else {
                    setFieldsEditable(false);
                    lblStatus.setText("❌ Error: Employee ID " + employeeId + " not found.");
                    lblStatus.setForeground(Color.RED);
                }
            }
        } catch (SQLException ex) {
            lblStatus.setText("❌ Database Error: " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    /** Parses a numeric field, treating blank as 0 instead of throwing. */
    private double parseOrZero(JTextField field) {
        String text = field.getText().trim();
        return text.isEmpty() ? 0.0 : Double.parseDouble(text);
    }

    /**
     * Calculates Gross/Tax/Medical/Total Deductions/Net Salary.
     *
     * QA FIX (Bug #1): this method previously had its OWN, inconsistent
     * formula (no 40-hr cap on normal hours, extended hours at 1.5x instead
     * of 1.0x, special work hours at a flat 3.0 instead of payRate*1.0, and
     * NO tax/medical/net calculation at all). It now delegates to
     * PayrollCalculator.calculate(...) - the SAME method used by
     * AddEmployeeTab - so results are guaranteed identical regardless of
     * which screen is used.
     *
     * QA FIX: empty hour fields now default to 0 (parseOrZero) instead of
     * throwing NumberFormatException, matching AddEmployeeTab's behaviour.
     */
    private void calculateAndDisplayPay() {
        try {
            double payRate = Double.parseDouble(txtPayRateHourly.getText().trim());
            double totalHours = parseOrZero(txtTotalHours);
            double overtimeHours = parseOrZero(txtOvertimeHours);
            double doubletimeHours = parseOrZero(txtDoubletimeHours);
            double extendedHours = parseOrZero(txtExtendedHours);
            double holidayPaidHours = parseOrZero(txtHolidayPaidHours);
            double specialWorkHours = parseOrZero(txtSpecialWorkHours);
            double bonusAmount = parseOrZero(txtBonusAmount);

            PayrollCalculator.PayResult result = PayrollCalculator.calculate(
                    payRate, totalHours, overtimeHours, doubletimeHours,
                    extendedHours, holidayPaidHours, specialWorkHours, bonusAmount);

            txtSalary.setText(String.format("%.2f", result.normalSalary));
            txtGrossSalary.setText(String.format("%.2f", result.grossSalary));
            txtTaxDeduction.setText(String.format("%.2f", result.taxDeduction));
            txtMedicalInsurance.setText(String.format("%.2f", result.medicalInsurance));
            txtTotalDeductions.setText(String.format("%.2f", result.totalDeductions));
            txtNetSalary.setText(String.format("%.2f", result.netSalary));
            txtCalculation.setText(String.format("%.2f", result.netSalary));

            lblStatus.setText("Salary recalculated. Review and click 'Update Employee' to save.");
            lblStatus.setForeground(Color.BLUE);

        } catch (NumberFormatException e) {
            txtCalculation.setText("Error");
            lblStatus.setText("❌ Error: Pay Rate, Bonus, and all Hour fields must contain valid numbers.");
            lblStatus.setForeground(Color.RED);
        } catch (IllegalArgumentException e) {
            // QA FIX (Bug #2): negative inputs are now rejected here too.
            txtCalculation.setText("Error");
            lblStatus.setText("❌ Error: " + e.getMessage());
            lblStatus.setForeground(Color.RED);
        }
    }

    /**
     * Persists the edited employee record back to the database.
     *
     * QA FIX (Bug #1): the UPDATE statement now also writes
     * gross_salary, tax_deduction, medical_insurance, total_deductions,
     * net_salary and bonus_amount - previously these were left stale
     * (still containing the values from when the record was first added),
     * even though the on-screen "Calculated Pay" had changed.
     */
    private void updateEmployee() {
        String employeeIdStr = txtEmployeeId.getText().trim();
        if (employeeIdStr.isEmpty()) {
            lblStatus.setText("❌ Error: No employee loaded.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        int employeeId;
        double payRate, totalHours, overtimeHours, doubletimeHours, extendedHours, holidayPaidHours, specialWorkHours, bonusAmount;
        try {
            employeeId = Integer.parseInt(employeeIdStr);
            payRate = Double.parseDouble(txtPayRateHourly.getText().trim());
            totalHours = parseOrZero(txtTotalHours);
            overtimeHours = parseOrZero(txtOvertimeHours);
            doubletimeHours = parseOrZero(txtDoubletimeHours);
            extendedHours = parseOrZero(txtExtendedHours);
            holidayPaidHours = parseOrZero(txtHolidayPaidHours);
            specialWorkHours = parseOrZero(txtSpecialWorkHours);
            bonusAmount = parseOrZero(txtBonusAmount);
        } catch (NumberFormatException e) {
            lblStatus.setText("❌ Error: ID, Pay Rate, Bonus, and all Hour fields must be valid numbers.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        // QA FIX (Bug #1 & #2): always recompute via PayrollCalculator right
        // before saving, so the persisted financial columns are guaranteed
        // consistent with the entered hours/rate (and negative values are
        // rejected) even if the user forgot to press "Calculate Salary".
        PayrollCalculator.PayResult result;
        try {
            result = PayrollCalculator.calculate(payRate, totalHours, overtimeHours, doubletimeHours,
                    extendedHours, holidayPaidHours, specialWorkHours, bonusAmount);
        } catch (IllegalArgumentException e) {
            lblStatus.setText("❌ Error: " + e.getMessage());
            lblStatus.setForeground(Color.RED);
            return;
        }

        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String designation = (String) cmbDesignation.getSelectedItem();
        String department = (String) cmbDepartment.getSelectedItem();
        String email = txtEmail.getText().trim();
        String contactNo = txtContactNo.getText().trim();
        String hireDate = txtHireDate.getText().trim();
        int active = Integer.parseInt((String) cmbActive.getSelectedItem());
        String payDate = txtPayDate.getText().trim();
        String ssn = txtSsn.getText().trim();
        String customer = txtCustomer.getText().trim();
        String bankName = txtBankName.getText().trim();
        String accountNo = txtAccountNo.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || contactNo.isEmpty()
                || hireDate.isEmpty() || payDate.isEmpty()) {
            lblStatus.setText("❌ Error: Please fill all required fields.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        // QA FIX: AddEmployeeTab validates name/phone/account-number format
        // via EmployeeFormValidator, but UpdateEmployeeTab previously did not
        // validate these at all - allowing an edit to silently corrupt
        // previously-valid data. Apply the SAME shared validation rules here.
        if (!EmployeeFormValidator.isValidName(firstName)) {
            lblStatus.setText("❌ Error: First Name must contain only letters, spaces, apostrophes or hyphens.");
            lblStatus.setForeground(Color.RED);
            return;
        }
        if (!EmployeeFormValidator.isValidName(lastName)) {
            lblStatus.setText("❌ Error: Last Name must contain only letters, spaces, apostrophes or hyphens.");
            lblStatus.setForeground(Color.RED);
            return;
        }
        if (!EmployeeFormValidator.isValidPhone(contactNo)) {
            lblStatus.setText("❌ Error: Contact Number is invalid. Use digits, optional leading '+', spaces or hyphens (7-20 chars).");
            lblStatus.setForeground(Color.RED);
            return;
        }
        if (!accountNo.isEmpty() && !EmployeeFormValidator.isValidAccountNo(accountNo)) {
            lblStatus.setText("❌ Error: Account No is invalid. Use 4-34 letters/digits/hyphens.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        String sql = "UPDATE employees SET first_name=?, last_name=?, designation=?, department=?, email_id=?, " +
                "contact_no=?, hire_date=?, pay_rate_hourly=?, active=?, Total_hours=?, overtime_hours=?, " +
                "doubletime_hours=?, pay_date=?, ssn=?, customer=?, bankname=?, accountno=?, extended_hours=?, " +
                "holiday_paid_hours=?, special_work_hours=?, " +
                "gross_salary=?, tax_deduction=?, medical_insurance=?, total_deductions=?, net_salary=?, bonus_amount=? " +
                "WHERE employee_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, firstName.toUpperCase());
            ps.setString(2, lastName.toUpperCase());
            ps.setString(3, designation);
            ps.setString(4, department);
            ps.setString(5, email.toLowerCase());
            ps.setString(6, contactNo);
            ps.setString(7, hireDate);
            ps.setDouble(8, payRate);
            ps.setInt(9, active);
            ps.setDouble(10, totalHours);
            ps.setDouble(11, overtimeHours);
            ps.setDouble(12, doubletimeHours);
            ps.setString(13, payDate);
            ps.setString(14, ssn);
            ps.setString(15, customer);
            ps.setString(16, bankName);
            ps.setString(17, accountNo);
            ps.setDouble(18, extendedHours);
            ps.setDouble(19, holidayPaidHours);
            ps.setDouble(20, specialWorkHours);

            // QA FIX (Bug #1): newly persisted financial columns.
            ps.setDouble(21, result.grossSalary);
            ps.setDouble(22, result.taxDeduction);
            ps.setDouble(23, result.medicalInsurance);
            ps.setDouble(24, result.totalDeductions);
            ps.setDouble(25, result.netSalary);
            ps.setDouble(26, result.bonusAmount);

            ps.setInt(27, employeeId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                lblStatus.setText("✅ Employee #" + employeeId + " updated successfully.");
                lblStatus.setForeground(new Color(0, 128, 0));
            } else {
                lblStatus.setText("❌ Error: Employee ID " + employeeId + " not found (no rows updated).");
                lblStatus.setForeground(Color.RED);
            }
        } catch (SQLException ex) {
            lblStatus.setText("❌ Database Error: " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    /**
     * Clears the form back to its initial (non-editable, no employee loaded) state.
     * QA FIX: made public - RefreshTab.java calls updateTab.clearFields() directly.
     */
    public void clearFields() {
        txtEmployeeId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtContactNo.setText("");
        txtPayRateHourly.setText("");
        txtHireDate.setText("");
        txtTotalHours.setText("");
        txtSalary.setText("");
        txtOvertimeHours.setText("");
        txtDoubletimeHours.setText("");
        txtPayDate.setText("");
        txtSsn.setText("");
        txtCustomer.setText("");
        txtBankName.setText("");
        txtAccountNo.setText("");
        txtExtendedHours.setText("");
        txtHolidayPaidHours.setText("");
        txtSpecialWorkHours.setText("");
        txtBonusAmount.setText("");
        txtCalculation.setText("");
        txtGrossSalary.setText("");
        txtTaxDeduction.setText("");
        txtMedicalInsurance.setText("");
        txtTotalDeductions.setText("");
        txtNetSalary.setText("");

        cmbActive.setSelectedIndex(0);
        cmbDepartment.setSelectedIndex(0);
        cmbDesignation.setSelectedIndex(0);

        setFieldsEditable(false);

        lblStatus.setText("Enter Employee ID and click 'Load Employee'.");
        lblStatus.setForeground(Color.BLACK);
    }
}

/*
 * দ্রষ্টব্য: এই কোডটি DBConnection ক্লাস এবং আপনার ডাটাবেসের স্কিমা (employee
 * টেবিলের কলাম নাম) বিদ্যমান এবং সঠিক বলে ধরে নিয়েছে। UPDATE কোয়েরিতে নতুন
 * যোগ করা ৬টি কলাম (gross_salary, tax_deduction, medical_insurance,
 * total_deductions, net_salary, bonus_amount) আপনার employees টেবিলে অবশ্যই
 * থাকতে হবে - AddEmployeeTab-এর INSERT কোয়েরিতে এই কলামগুলো ইতিমধ্যে ব্যবহৃত
 * হচ্ছে।
 */