package com.payroll;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class AddEmployeeTab extends JPanel {

    private static final long serialVersionUID = 1L;

    // ===============================================
    // INTERFACE & CONSTANTS
    // ===============================================
    public interface EmployeeAddedListener {
        void onEmployeeAdded();
    }

    private EmployeeAddedListener listener;

    // ---------------------------------------------------------------
    // QA FIX (Bug #1): all calculation constants and the calculation
    // formula now live in ONE place: PayrollCalculator.
    // AddEmployeeTab and UpdateEmployeeTab both call
    // PayrollCalculator.calculate(...) so the math can never diverge
    // again between the "Add" and "Update" screens.
    //
    // QA FIX: name/phone/account-number validation and MM/dd/yyyy <->
    // yyyy-MM-dd date conversion now live in ONE place: EmployeeFormValidator,
    // so the same rules can be reused by UpdateEmployeeTab and others, and
    // are unit/Cucumber-testable without Swing.
    // ---------------------------------------------------------------

    // ===============================================
    // UI COMPONENTS
    // ===============================================
    // Input Fields (Existing & New)
    private JTextField txtEmployeeId, txtFirstName, txtLastName, txtContactNo, txtEmail, txtPayRate;
    private JTextField txtTotalHours, txtOvertimeHours, txtDoubletimeHours;
    private JComboBox<String> cmbDepartment, cmbDesignation;

    private JTextField txtSSN, txtHireDate, txtCustomer, txtBankName, txtAccountNo;
    private JTextField txtExtendedHours, txtHolidayPaidHours, txtSpecialWorkHours, txtBonusAmount;


    // Output/Calculated Fields (Existing & New)
    private JTextField txtNormalSalary, txtOvertimeSalary, txtDoubletimeSalary;
    private JTextField txtExtendedSalary, txtHolidayPaidSalary, txtSpecialWorkSalary;
    private JTextField txtGrossSalary, txtTaxDeduction, txtMedicalInsurance, txtTotalDeductions, txtNetSalary, txtPayDate;


    private JButton btnAdd, btnCalculate;
    private JLabel lblStatus;

    // ===============================================
    // CONSTRUCTOR
    // ===============================================
    public AddEmployeeTab(EmployeeAddedListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout());

        // --- Title Bar ---
        JLabel lblTitle = new JLabel("ADD NEW EMPLOYEE PAYROLL DATA", SwingConstants.CENTER);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(0, 102, 255));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        // --- Main Form Panel (Center) ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbcContainer = new GridBagConstraints();
        gbcContainer.insets = new Insets(10, 20, 10, 20);
        gbcContainer.anchor = GridBagConstraints.NORTHWEST;

        formContainer.add(createInputPanel(), gbcContainer);

        gbcContainer.gridx = 1;
        formContainer.add(createOutputPanel(), gbcContainer);

        add(new JScrollPane(formContainer), BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnAdd = new JButton("Add Employee Record");
        lblStatus = new JLabel("Please enter data and calculate salary.", SwingConstants.CENTER);

        bottomPanel.add(btnAdd);
        bottomPanel.add(lblStatus);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Event Listeners ---
        btnAdd.addActionListener(e -> addEmployee());

        // Initial setup
        loadLastEmployeeId();
    }

    // ===============================================
    // UI CREATION METHODS
    // ===============================================

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Personal & Hourly Data"));
        GridBagConstraints gbc = createGbc(5, 5);

        // Initialize all input fields
        txtEmployeeId = new JTextField(15);
        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        cmbDepartment = new JComboBox<>(new String[]{"-- Select --", "HR", "Finance", "IT", "Sales"});
        cmbDesignation = new JComboBox<>(new String[]{"-- Select --", "Manager", "Executive", "Engineer", "Assistant"});
        txtContactNo = new JTextField(15);
        txtEmail = new JTextField(15);
        txtPayRate = new JTextField(15);
        txtTotalHours = new JTextField(15);
        txtOvertimeHours = new JTextField(15);
        txtDoubletimeHours = new JTextField(15);

        // New input fields based on CSV/Schema
        txtSSN = new JTextField(15);
        txtHireDate = new JTextField(15);
        txtCustomer = new JTextField(15);
        txtBankName = new JTextField(15);
        txtAccountNo = new JTextField(15);
        txtBonusAmount = new JTextField(15);
        txtExtendedHours = new JTextField(15);
        txtHolidayPaidHours = new JTextField(15);
        txtSpecialWorkHours = new JTextField(15);


        String[] labels = {
            "Employee ID:", "First Name:", "Last Name:",
            "Department:", "Designation:", "SSN:", "Contact Number:",
            "Email ID:", "Hire Date (MM/DD/YYYY):", "Customer (Optional):",
            "Bank Name:", "Account No:", "Pay Rate (hour):", "Total Hours:",
            "Overtime Hours:", "Doubletime Hours:", "Extended Hours:",
            "Holiday Paid Hours:", "Special Work Hours:", "Bonus Amount:"
        };

        Component[] components = {
            txtEmployeeId, txtFirstName, txtLastName, cmbDepartment, cmbDesignation,
            txtSSN, txtContactNo, txtEmail, txtHireDate, txtCustomer,
            txtBankName, txtAccountNo, txtPayRate, txtTotalHours,
            txtOvertimeHours, txtDoubletimeHours, txtExtendedHours,
            txtHolidayPaidHours, txtSpecialWorkHours, txtBonusAmount
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panel.add(components[i], gbc);
        }
        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Salary & Deduction Calculations"));
        GridBagConstraints gbc = createGbc(5, 5);

        // Initialize calculated fields
        txtNormalSalary = new JTextField(15);
        txtOvertimeSalary = new JTextField(15);
        txtDoubletimeSalary = new JTextField(15);
        txtExtendedSalary = new JTextField(15);
        txtHolidayPaidSalary = new JTextField(15);
        txtSpecialWorkSalary = new JTextField(15);

        txtGrossSalary = new JTextField(15);
        txtTaxDeduction = new JTextField(15);
        txtMedicalInsurance = new JTextField(15);
        txtTotalDeductions = new JTextField(15);
        txtNetSalary = new JTextField(15);
        txtPayDate = new JTextField(15);

        // Initialize btnCalculate
        btnCalculate = new JButton("Calculate Salary");

        // Make calculated fields non-editable
        List<JTextField> calculatedFields = Arrays.asList(
            txtNormalSalary, txtOvertimeSalary, txtDoubletimeSalary,
            txtExtendedSalary, txtHolidayPaidSalary, txtSpecialWorkSalary,
            txtGrossSalary, txtTaxDeduction, txtMedicalInsurance,
            txtTotalDeductions, txtNetSalary
        );
        calculatedFields.forEach(f -> f.setEditable(false));

        String[] labels = {
            "Normal Salary:", "Overtime Salary:", "Doubletime Salary:",
            "Extended Salary:", "Holiday Paid Salary:", "Special Work Salary:",
            "Gross Salary:",
            String.format("Tax Deduction (%.0f%%):", PayrollCalculator.TAX_PERCENTAGE * 100),
            String.format("Medical Insurance (%.0f%%):", PayrollCalculator.MEDICAL_PERCENTAGE * 100),
            "Total Deductions:", "Net Salary:", "Pay Date (MM/DD/YYYY):"
        };
        JTextField[] fields = {
            txtNormalSalary, txtOvertimeSalary, txtDoubletimeSalary,
            txtExtendedSalary, txtHolidayPaidSalary, txtSpecialWorkSalary,
            txtGrossSalary, txtTaxDeduction, txtMedicalInsurance,
            txtTotalDeductions, txtNetSalary, txtPayDate
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        // Calculate button
        gbc.gridx = 1; gbc.gridy = labels.length;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnCalculate, gbc);

        // Add listener here to ensure btnCalculate is initialized
        btnCalculate.addActionListener(e -> calculateSalary());

        return panel;
    }

    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(y, x, y, x);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    // ===============================================
    // CORE LOGIC METHODS
    // ===============================================

    /**
     * Loads the next suggested Employee ID from the database.
     */
    public void loadLastEmployeeId() {
        String query = "SELECT MAX(employee_id) AS last_id FROM employees";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            int nextId = 1;

            if (rs.next()) {
                int lastId = rs.getInt("last_id");

                if (!rs.wasNull()) {
                    nextId = lastId + 1;
                }
            }
            txtEmployeeId.setText(String.valueOf(nextId));
            // Employee ID is now Editable.
        } catch (SQLException e) {
            System.err.println("Error fetching last Employee ID: " + e.getMessage());
            txtEmployeeId.setText("1");
        }
    }

    /**
     * Calculates Gross Salary, Deductions, and Net Salary.
     *
     * QA FIX (Bug #1 & #2): the math now lives in PayrollCalculator so it
     * is identical to UpdateEmployeeTab, AND negative pay rate / hours /
     * bonus are now rejected with a clear error message instead of being
     * silently accepted and producing a negative net salary.
     */
    private void calculateSalary() {
        try {
            double payRate = parseRequired(txtPayRate, "Pay Rate");
            double totalHours = parseRequired(txtTotalHours, "Total Hours");

            // Hours and Bonus fields, default to 0 if empty
            double overtimeHours = parseOrZero(txtOvertimeHours);
            double doubletimeHours = parseOrZero(txtDoubletimeHours);
            double extendedHours = parseOrZero(txtExtendedHours);
            double holidayPaidHours = parseOrZero(txtHolidayPaidHours);
            double specialWorkHours = parseOrZero(txtSpecialWorkHours);
            double bonusAmount = parseOrZero(txtBonusAmount);

            PayrollCalculator.PayResult result = PayrollCalculator.calculate(
                    payRate, totalHours, overtimeHours, doubletimeHours,
                    extendedHours, holidayPaidHours, specialWorkHours, bonusAmount);

            // --- Update TextFields ---
            txtNormalSalary.setText(String.format("%.2f", result.normalSalary));
            txtOvertimeSalary.setText(String.format("%.2f", result.overtimeSalary));
            txtDoubletimeSalary.setText(String.format("%.2f", result.doubletimeSalary));
            txtExtendedSalary.setText(String.format("%.2f", result.extendedSalary));
            txtHolidayPaidSalary.setText(String.format("%.2f", result.holidayPaidSalary));
            txtSpecialWorkSalary.setText(String.format("%.2f", result.specialWorkSalary));

            txtGrossSalary.setText(String.format("%.2f", result.grossSalary));
            txtTaxDeduction.setText(String.format("%.2f", result.taxDeduction));
            txtMedicalInsurance.setText(String.format("%.2f", result.medicalInsurance));
            txtTotalDeductions.setText(String.format("%.2f", result.totalDeductions));
            txtNetSalary.setText(String.format("%.2f", result.netSalary));

            lblStatus.setText("Salary calculated. Ready to Add.");
            lblStatus.setForeground(Color.BLUE);

        } catch (NumberFormatException e) {
            lblStatus.setText("❌ Error: Pay Rate, Bonus, and all Hour fields must contain valid numbers.");
            lblStatus.setForeground(Color.RED);
            clearCalculatedFields();
        } catch (IllegalArgumentException e) {
            // QA FIX (Bug #2): negative numbers are rejected here.
            lblStatus.setText("❌ Error: " + e.getMessage());
            lblStatus.setForeground(Color.RED);
            clearCalculatedFields();
        }
    }

    /** Parses a required numeric field, throwing NumberFormatException if blank/invalid. */
    private double parseRequired(JTextField field, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new NumberFormatException(fieldName + " is required.");
        }
        return Double.parseDouble(text);
    }

    /** Parses an optional numeric field, treating blank as 0. */
    private double parseOrZero(JTextField field) {
        String text = field.getText().trim();
        return text.isEmpty() ? 0.0 : Double.parseDouble(text);
    }

    private void clearCalculatedFields() {
        List<JTextField> calculatedFields = Arrays.asList(
            txtNormalSalary, txtOvertimeSalary, txtDoubletimeSalary,
            txtExtendedSalary, txtHolidayPaidSalary, txtSpecialWorkSalary,
            txtGrossSalary, txtTaxDeduction, txtMedicalInsurance,
            txtTotalDeductions, txtNetSalary
        );
        calculatedFields.forEach(f -> f.setText(""));
    }

    /**
     * Clears all fields in the form and reloads the next Employee ID.
     */
    private void clearFields() {
        List<JTextField> fieldsToClear = Arrays.asList(
            txtFirstName, txtLastName, txtContactNo, txtEmail, txtPayRate,
            txtTotalHours, txtOvertimeHours, txtDoubletimeHours, txtPayDate,
            txtSSN, txtHireDate, txtCustomer, txtBankName, txtAccountNo,
            txtExtendedHours, txtHolidayPaidHours, txtSpecialWorkHours, txtBonusAmount
        );
        fieldsToClear.forEach(f -> f.setText(""));
        clearCalculatedFields();

        cmbDepartment.setSelectedIndex(0);
        cmbDesignation.setSelectedIndex(0);

        loadLastEmployeeId();

        lblStatus.setText(" ");
        lblStatus.setForeground(Color.BLACK);
    }


    private void addEmployee() {

        // --- 1. Initial Data Validation and Parsing ---
        String employeeIdStr = txtEmployeeId.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String department = cmbDepartment.getSelectedItem().toString();
        String designation = cmbDesignation.getSelectedItem().toString();
        String ssnStr = txtSSN.getText().trim();
        String contactNoStr = txtContactNo.getText().trim();
        String emailId = txtEmail.getText().trim();
        String hireDateStr = txtHireDate.getText().trim();
        String customer = txtCustomer.getText().trim();
        String bankName = txtBankName.getText().trim();
        String accountNoStr = txtAccountNo.getText().trim();


        // Check for required fields
        if (employeeIdStr.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || contactNoStr.isEmpty() ||
            emailId.isEmpty() || hireDateStr.isEmpty() || txtPayRate.getText().isEmpty() ||
            txtTotalHours.getText().isEmpty() || bankName.isEmpty() || accountNoStr.isEmpty() ||
            department.equals("-- Select --") || designation.equals("-- Select --") ||
            txtPayDate.getText().isEmpty())
        {
            lblStatus.setText("❌ Error: Please fill all required fields and select options.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        // Check for calculated values
        if (txtNetSalary.getText().isEmpty()) {
            lblStatus.setText("❌ Error: Please press 'Calculate Salary' first.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        // Name Validation (letters, spaces, apostrophe, hyphen only)
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

        // QA FIX (Bug #6): validate contact/account numbers as TEXT.
        if (!EmployeeFormValidator.isValidPhone(contactNoStr)) {
            lblStatus.setText("❌ Error: Contact Number is invalid. Use digits, optional leading '+', spaces or hyphens (7-20 chars).");
            lblStatus.setForeground(Color.RED);
            return;
        }
        if (!EmployeeFormValidator.isValidAccountNo(accountNoStr)) {
            lblStatus.setText("❌ Error: Account No is invalid. Use 4-34 letters/digits/hyphens.");
            lblStatus.setForeground(Color.RED);
            return;
        }

        // Numeric Validation
        int employeeId;
        double payRate, totalHours, overtimeHours, doubletimeHours, extendedHours, holidayPaidHours, specialWorkHours, bonusAmount;

        try {
            employeeId = Integer.parseInt(employeeIdStr);

            payRate = Double.parseDouble(txtPayRate.getText());
            totalHours = Double.parseDouble(txtTotalHours.getText());
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

        // QA FIX (Bug #2): re-validate (and re-derive) the calculation here too,
        // in case the user changed a field after pressing "Calculate Salary"
        // but before pressing "Add Employee Record".
        PayrollCalculator.PayResult result;
        try {
            result = PayrollCalculator.calculate(payRate, totalHours, overtimeHours, doubletimeHours,
                    extendedHours, holidayPaidHours, specialWorkHours, bonusAmount);
        } catch (IllegalArgumentException e) {
            lblStatus.setText("❌ Error: " + e.getMessage() + " পুনরায় 'Calculate Salary' চাপুন।");
            lblStatus.setForeground(Color.RED);
            return;
        }

        // Date formatting
        String sqlPayDateString;
        try {
            sqlPayDateString = EmployeeFormValidator.toSqlDate(txtPayDate.getText().trim());
        } catch (DateTimeParseException e) {
            lblStatus.setText("❌ Error: Invalid pay date format. Must be MM/DD/YYYY (e.g., 11/15/2025).");
            lblStatus.setForeground(Color.RED);
            return;
        }

        String sqlHireDateString;
        try {
            sqlHireDateString = EmployeeFormValidator.toSqlDate(hireDateStr);
        } catch (DateTimeParseException e) {
            lblStatus.setText("❌ Error: Invalid hire date format. Must be MM/DD/YYYY (e.g., 10/04/2024).");
            lblStatus.setForeground(Color.RED);
            return;
        }


        // --- 2. Database Insertion ---
        // The auto-increment primary key (likely 'id' or 'row_id') is omitted from the column list,
        // and 'employee_id' is moved to the end of the list (34 parameters total).
        String sql = "INSERT INTO employees (first_name, last_name, SSN, designation, contact_no, email_id, hire_date, Total_hours, pay_rate_hourly, active, department, customer, bank_name, account_no, salary, overtime_hours, overtime_amount, doubletime_hours, doubletime_amount, extended_hours, extended_amount, holidaypaid_hours, holidaypaid_amount, specialwork_hours, specialwork_amount, bonus_amount, gross_salary, ytd_gross, tax_deduction, medical_insurance, net_salary, pay_date, total_deductions, employee_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Parameters 1-14: identity / employment info
            ps.setString(1, firstName.toUpperCase());
            ps.setString(2, lastName.toUpperCase());
            ps.setString(3, ssnStr.isEmpty() ? null : ssnStr);
            ps.setString(4, designation);
            ps.setString(5, contactNoStr);
            ps.setString(6, emailId.toLowerCase());

            ps.setString(7, sqlHireDateString);
            ps.setDouble(8, totalHours);
            ps.setDouble(9, payRate);

            // Parameter 10: active status
            ps.setInt(10, 1);

            ps.setString(11, department);
            ps.setString(12, customer.isEmpty() ? null : customer);
            ps.setString(13, bankName);
            ps.setString(14, accountNoStr);

            // Parameters 15-26: hours & amounts (now sourced directly from PayrollCalculator.PayResult)
            ps.setDouble(15, result.normalSalary);          // salary (normal)
            ps.setDouble(16, overtimeHours);
            ps.setDouble(17, result.overtimeSalary);          // overtime_amount
            ps.setDouble(18, doubletimeHours);
            ps.setDouble(19, result.doubletimeSalary);        // doubletime_amount

            ps.setDouble(20, extendedHours);
            ps.setDouble(21, result.extendedSalary);          // extended_amount
            ps.setDouble(22, holidayPaidHours);
            ps.setDouble(23, result.holidayPaidSalary);       // holidaypaid_amount
            ps.setDouble(24, specialWorkHours);
            ps.setDouble(25, result.specialWorkSalary);       // specialwork_amount
            ps.setDouble(26, result.bonusAmount);             // bonus_amount (input)

            // Parameters 27-33: gross/deductions/net
            ps.setDouble(27, result.grossSalary);             // gross_salary

            // QA FIX (Bug #3): ytd_gross was always hard-coded to 0.0.
            // For a brand-new employee record this IS the first pay period
            // of the year, so YTD gross starts equal to this period's gross
            // salary (instead of permanently 0). NOTE: a proper fix is to
            // move to a separate `payroll_history` table and compute
            // ytd_gross with SUM(gross_salary) over the current year - see
            // the QA report, section 6.3.
            ps.setDouble(28, result.grossSalary);             // ytd_gross

            ps.setDouble(29, result.taxDeduction);            // tax_deduction
            ps.setDouble(30, result.medicalInsurance);        // medical_insurance
            ps.setDouble(31, result.netSalary);               // net_salary
            ps.setString(32, sqlPayDateString);               // pay_date

            ps.setDouble(33, result.totalDeductions);         // total_deductions

            // Parameter 34: employee_id
            ps.setInt(34, employeeId);

            ps.executeUpdate();

            lblStatus.setText("✅ Employee added successfully! ID: " + employeeId);
            lblStatus.setForeground(new Color(0, 128, 0));

            // Signal the parent GUI
            if (listener != null) {
                listener.onEmployeeAdded();
            }

            clearFields();

        } catch (SQLException ex) {
            String dbError;
            if (ex.getErrorCode() == 1062) {
                 dbError = "❌ Database Error: Employee ID " + employeeId + " already exists (Primary Key Violation).";
            } else if (ex.getMessage().contains("Column count does not match value count")) {
                 dbError = "❌ Database Error: Column count mismatch. Your table structure might be different from expected 35 columns.";
            } else {
                 dbError = "❌ Database Error: " + ex.getMessage() + " (SQL State: " + ex.getSQLState() + ")";
            }

            lblStatus.setText(dbError);
            lblStatus.setForeground(Color.RED);
            ex.printStackTrace();
        } catch (Exception ex) {
            lblStatus.setText("❌ An unexpected error occurred: " + ex.getMessage());
            lblStatus.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }
}