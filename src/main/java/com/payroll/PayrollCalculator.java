package com.payroll;

/**
 * Centralized, testable payroll calculation engine.
 *
 * WHY THIS CLASS EXISTS (QA NOTE):
 * The original project calculated salary in TWO different places with TWO
 * different formulas:
 *   1) AddEmployeeTab.calculateSalary()
 *   2) UpdateEmployeeTab.calculateAndDisplayPay()
 *
 * These two formulas did NOT match each other:
 *   - AddEmployeeTab capped "normal hours" at 40 (NORMAL_HOURS_THRESHOLD).
 *     UpdateEmployeeTab did NOT cap normal hours at all.
 *   - AddEmployeeTab used extendedHours * payRate * 1.0 ("EXTENDED_MULTIPLIER").
 *     UpdateEmployeeTab used extendedHours * payRate * 1.5.
 *   - AddEmployeeTab used specialWorkHours * payRate * 1.0.
 *     UpdateEmployeeTab used specialWorkHours * 3.0  (payRate was MISSING entirely!).
 *   - AddEmployeeTab calculated taxDeduction (20%) and medicalInsurance (10%)
 *     and netSalary. UpdateEmployeeTab never calculated these at all, and the
 *     UPDATE SQL never saved gross_salary / net_salary / deductions back to
 *     the database -> after editing an employee's hours, the Payslip tab
 *     would keep showing the OLD (stale) gross/net salary.
 *   - Neither screen validated for NEGATIVE numbers (negative pay rate,
 *     negative hours, negative bonus), which would silently produce a
 *     negative or wrong "net salary".
 *
 * FIX: both AddEmployeeTab and UpdateEmployeeTab should call this single
 * class so the math is guaranteed to be identical everywhere (Add, Update,
 * Payslip). This also makes the math 100% unit-testable without needing
 * Swing or MySQL.
 */
public final class PayrollCalculator {

    // ---- Business rule constants (single source of truth) ----
    public static final double OVERTIME_MULTIPLIER     = 1.5;
    public static final double DOUBLETIME_MULTIPLIER   = 2.0;
    public static final double EXTENDED_MULTIPLIER     = 1.0;
    public static final double HOLIDAYPAID_MULTIPLIER  = 1.0;
    public static final double SPECIALWORK_MULTIPLIER  = 1.0;

    public static final double TAX_PERCENTAGE      = 0.20; // 20%
    public static final double MEDICAL_PERCENTAGE  = 0.10; // 10%

    public static final int NORMAL_HOURS_THRESHOLD = 40;

    private PayrollCalculator() {
        // utility class - no instances
    }

    /**
     * Immutable result object holding every calculated value needed by the
     * "Add Employee", "Update Employee" and "Payslip" screens.
     */
    public static final class PayResult {
        public final double normalSalary;
        public final double overtimeSalary;
        public final double doubletimeSalary;
        public final double extendedSalary;
        public final double holidayPaidSalary;
        public final double specialWorkSalary;
        public final double bonusAmount;

        public final double grossSalary;
        public final double taxDeduction;
        public final double medicalInsurance;
        public final double totalDeductions;
        public final double netSalary;

        PayResult(double normalSalary, double overtimeSalary, double doubletimeSalary,
                  double extendedSalary, double holidayPaidSalary, double specialWorkSalary,
                  double bonusAmount, double grossSalary, double taxDeduction,
                  double medicalInsurance, double totalDeductions, double netSalary) {
            this.normalSalary = normalSalary;
            this.overtimeSalary = overtimeSalary;
            this.doubletimeSalary = doubletimeSalary;
            this.extendedSalary = extendedSalary;
            this.holidayPaidSalary = holidayPaidSalary;
            this.specialWorkSalary = specialWorkSalary;
            this.bonusAmount = bonusAmount;
            this.grossSalary = grossSalary;
            this.taxDeduction = taxDeduction;
            this.medicalInsurance = medicalInsurance;
            this.totalDeductions = totalDeductions;
            this.netSalary = netSalary;
        }
    }

    /**
     * Calculates a full payroll breakdown for one pay period.
     *
     * @throws IllegalArgumentException if payRate is negative, or if any
     *         hours/bonus value is negative (a payroll line item can never
     *         be negative - this was NOT validated in the original code).
     */
    public static PayResult calculate(double payRate,
                                       double totalHours,
                                       double overtimeHours,
                                       double doubletimeHours,
                                       double extendedHours,
                                       double holidayPaidHours,
                                       double specialWorkHours,
                                       double bonusAmount) {

        requireNonNegative("payRate", payRate);
        requireNonNegative("totalHours", totalHours);
        requireNonNegative("overtimeHours", overtimeHours);
        requireNonNegative("doubletimeHours", doubletimeHours);
        requireNonNegative("extendedHours", extendedHours);
        requireNonNegative("holidayPaidHours", holidayPaidHours);
        requireNonNegative("specialWorkHours", specialWorkHours);
        requireNonNegative("bonusAmount", bonusAmount);

        // 1. Salary components
        double normalHours = Math.min(totalHours, NORMAL_HOURS_THRESHOLD);
        double normalSalary = normalHours * payRate;

        double overtimeSalary    = overtimeHours    * payRate * OVERTIME_MULTIPLIER;
        double doubletimeSalary  = doubletimeHours  * payRate * DOUBLETIME_MULTIPLIER;
        double extendedSalary    = extendedHours    * payRate * EXTENDED_MULTIPLIER;
        double holidayPaidSalary = holidayPaidHours * payRate * HOLIDAYPAID_MULTIPLIER;
        double specialWorkSalary = specialWorkHours * payRate * SPECIALWORK_MULTIPLIER;

        double grossSalary = normalSalary + overtimeSalary + doubletimeSalary
                + extendedSalary + holidayPaidSalary + specialWorkSalary + bonusAmount;

        // 2. Deductions
        double taxDeduction     = round2(grossSalary * TAX_PERCENTAGE);
        double medicalInsurance = round2(grossSalary * MEDICAL_PERCENTAGE);
        double totalDeductions  = round2(taxDeduction + medicalInsurance);
        double netSalary        = round2(grossSalary - totalDeductions);

        return new PayResult(
                round2(normalSalary), round2(overtimeSalary), round2(doubletimeSalary),
                round2(extendedSalary), round2(holidayPaidSalary), round2(specialWorkSalary),
                round2(bonusAmount), round2(grossSalary), taxDeduction, medicalInsurance,
                totalDeductions, netSalary
        );
    }

    private static void requireNonNegative(String fieldName, double value) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " ঋণাত্মক (negative) হতে পারবে না: " + value);
        }
    }

    /** Rounds to 2 decimal places using standard half-up rounding (money-safe). */
    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}