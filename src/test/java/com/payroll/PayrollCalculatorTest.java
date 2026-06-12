package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 unit tests for {@link PayrollCalculator}.
 *
 * Run with: mvn test
 * (requires junit-jupiter dependency - see pom.xml notes in the QA report)
 */
class PayrollCalculatorTest {

    private static final double DELTA = 0.001;

    // ---------------------------------------------------------------
    // 1. Basic / happy-path cases
    // ---------------------------------------------------------------

    @Test
    @DisplayName("৪০ ঘন্টার কম কাজ করলে শুধু Normal Salary হিসাব হবে")
    void normalHoursUnder40_noOvertime() {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                /*payRate*/ 20.0, /*totalHours*/ 35, 0, 0, 0, 0, 0, 0);

        assertEquals(700.0, r.normalSalary, DELTA);   // 35 * 20
        assertEquals(0.0, r.overtimeSalary, DELTA);
        assertEquals(700.0, r.grossSalary, DELTA);
        assertEquals(140.0, r.taxDeduction, DELTA);     // 20% of 700
        assertEquals(70.0, r.medicalInsurance, DELTA);  // 10% of 700
        assertEquals(210.0, r.totalDeductions, DELTA);
        assertEquals(490.0, r.netSalary, DELTA);
    }

    @Test
    @DisplayName("ঠিক ৪০ ঘন্টা কাজ করলে পুরো ৪০ ঘন্টাই Normal Salary হবে (boundary)")
    void exactly40Hours_boundary() {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                15.0, 40, 0, 0, 0, 0, 0, 0);

        assertEquals(600.0, r.normalSalary, DELTA); // 40 * 15
        assertEquals(0.0, r.overtimeSalary, DELTA);
    }

    @Test
    @DisplayName("৪০ ঘন্টার বেশি Total Hours দিলেও Normal Salary সর্বোচ্চ ৪০ ঘন্টাতেই capped থাকবে")
    void totalHoursAbove40_isCappedAt40_forNormalSalary() {
        // Bug fixed: UpdateEmployeeTab did NOT cap this; AddEmployeeTab did.
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                10.0, 50, 0, 0, 0, 0, 0, 0);

        assertEquals(400.0, r.normalSalary, DELTA); // min(50,40) * 10 = 400, NOT 500
    }

    @Test
    @DisplayName("Overtime Hours থাকলে rate এর ১.৫ গুণ হারে যোগ হবে")
    void overtimeHours_areCalculatedAt1point5x() {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                10.0, 40, 5, 0, 0, 0, 0, 0);

        assertEquals(400.0, r.normalSalary, DELTA);
        assertEquals(75.0, r.overtimeSalary, DELTA);  // 5 * 10 * 1.5
        assertEquals(475.0, r.grossSalary, DELTA);
    }

    @Test
    @DisplayName("Doubletime Hours থাকলে rate এর ২.০ গুণ হারে যোগ হবে")
    void doubletimeHours_areCalculatedAt2x() {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                10.0, 40, 0, 4, 0, 0, 0, 0);

        assertEquals(80.0, r.doubletimeSalary, DELTA); // 4 * 10 * 2.0
        assertEquals(480.0, r.grossSalary, DELTA);
    }

    @Test
    @DisplayName("Extended / Holiday Paid / Special Work Hours -> সব pay rate অনুযায়ী হিসাব হবে (1.0x)")
    void extendedHolidaySpecial_useNormalRate() {
        // Bug fixed: UpdateEmployeeTab used 1.5x for extended (Add used 1.0x),
        // and used a hard-coded 3.0 (without payRate) for special work hours.
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                10.0, 0, 0, 0, /*extended*/ 2, /*holiday*/ 3, /*special*/ 4, 0);

        assertEquals(20.0, r.extendedSalary, DELTA);     // 2 * 10 * 1.0
        assertEquals(30.0, r.holidayPaidSalary, DELTA);  // 3 * 10 * 1.0
        assertEquals(40.0, r.specialWorkSalary, DELTA);  // 4 * 10 * 1.0 (NOT 4*3.0=12)
        assertEquals(90.0, r.grossSalary, DELTA);
    }

    @Test
    @DisplayName("Bonus Amount সরাসরি Gross Salary-এর সাথে যোগ হবে কিন্তু কর/মেডিকেল হিসাবেও যুক্ত থাকবে")
    void bonusAmount_addsToGrossAndIsTaxed() {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                10.0, 40, 0, 0, 0, 0, 0, /*bonus*/ 100);

        assertEquals(500.0, r.grossSalary, DELTA);   // 400 normal + 100 bonus
        assertEquals(100.0, r.taxDeduction, DELTA);  // 20% of 500
        assertEquals(50.0, r.medicalInsurance, DELTA); // 10% of 500
        assertEquals(350.0, r.netSalary, DELTA);
    }

    // ---------------------------------------------------------------
    // 2. Edge cases (০ মান, খুব বড় মান)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("সব ইনপুট শূন্য হলে সব আউটপুট শূন্য হবে")
    void allZeroInputs_resultInAllZeroOutputs() {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(0, 0, 0, 0, 0, 0, 0, 0);

        assertEquals(0.0, r.grossSalary, DELTA);
        assertEquals(0.0, r.taxDeduction, DELTA);
        assertEquals(0.0, r.medicalInsurance, DELTA);
        assertEquals(0.0, r.netSalary, DELTA);
    }

    @Test
    @DisplayName("খুব বড় মান (high pay rate, অনেক ঘন্টা) ঠিকভাবে হিসাব হওয়া উচিত - overflow নেই")
    void largeValues_doNotOverflowOrLoseAccuracy() {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                500.0, 80, 40, 20, 10, 10, 10, 5000);

        // normal = min(80,40)*500 = 20000
        // OT = 40*500*1.5 = 30000
        // DT = 20*500*2.0 = 20000
        // ext = 10*500 = 5000
        // holiday = 10*500 = 5000
        // special = 10*500 = 5000
        // bonus = 5000
        // gross = 20000+30000+20000+5000+5000+5000+5000 = 90000
        assertEquals(90000.0, r.grossSalary, DELTA);
        assertEquals(18000.0, r.taxDeduction, DELTA);
        assertEquals(9000.0, r.medicalInsurance, DELTA);
        assertEquals(63000.0, r.netSalary, DELTA);
    }

    @Test
    @DisplayName("খুব ছোট দশমিক মান (পয়সা) সঠিকভাবে round হবে")
    void fractionalCents_areRoundedCorrectly() {
        PayrollCalculator.PayResult r = PayrollCalculator.calculate(
                10.005, 1, 0, 0, 0, 0, 0, 0);

        // 1 * 10.005 = 10.005 -> rounds to 10.01 (half-up) or 10.00 depending on FP repr
        assertEquals(PayrollCalculator.round2(10.005), r.normalSalary, DELTA);
    }

    // ---------------------------------------------------------------
    // 3. Negative / invalid input -> must be REJECTED (bug fix)
    // ---------------------------------------------------------------

    @ParameterizedTest(name = "negative {0} মান দিলে IllegalArgumentException ছুঁড়বে")
    @DisplayName("যেকোনো ইনপুট ঋণাত্মক হলে ব্যতিক্রম (exception) আসা উচিত")
    @CsvSource({
            "-1, 0, 0, 0, 0, 0, 0, 0",   // negative pay rate
            "10, -1, 0, 0, 0, 0, 0, 0",  // negative total hours
            "10, 0, -1, 0, 0, 0, 0, 0",  // negative overtime hours
            "10, 0, 0, -1, 0, 0, 0, 0",  // negative doubletime hours
            "10, 0, 0, 0, -1, 0, 0, 0",  // negative extended hours
            "10, 0, 0, 0, 0, -1, 0, 0",  // negative holiday hours
            "10, 0, 0, 0, 0, 0, -1, 0",  // negative special work hours
            "10, 0, 0, 0, 0, 0, 0, -1",  // negative bonus
    })
    void negativeInputs_throwIllegalArgumentException(double payRate, double totalHours,
            double overtimeHours, double doubletimeHours, double extendedHours,
            double holidayPaidHours, double specialWorkHours, double bonusAmount) {

        assertThrows(IllegalArgumentException.class, () ->
                PayrollCalculator.calculate(payRate, totalHours, overtimeHours, doubletimeHours,
                        extendedHours, holidayPaidHours, specialWorkHours, bonusAmount));
    }

    // ---------------------------------------------------------------
    // 4. Cross-screen consistency check (Add vs Update vs Payslip)
    // ---------------------------------------------------------------

    @Test
    @DisplayName("একই ইনপুটে Add ও Update স্ক্রিন থেকে আসা ফলাফল অভিন্ন (consistent) হওয়া উচিত")
    void calculationIsConsistentRegardlessOfCallingScreen() {
        // Simulates the same employee data being entered on the
        // "Add Employee" screen and later edited on the "Update Employee"
        // screen - both must now produce IDENTICAL gross/net figures.
        PayrollCalculator.PayResult addScreen = PayrollCalculator.calculate(
                25.0, 45, 5, 2, 3, 1, 1, 50);
        PayrollCalculator.PayResult updateScreen = PayrollCalculator.calculate(
                25.0, 45, 5, 2, 3, 1, 1, 50);

        assertEquals(addScreen.grossSalary, updateScreen.grossSalary, DELTA);
        assertEquals(addScreen.netSalary, updateScreen.netSalary, DELTA);
        assertEquals(addScreen.taxDeduction, updateScreen.taxDeduction, DELTA);
        assertEquals(addScreen.medicalInsurance, updateScreen.medicalInsurance, DELTA);
    }
}