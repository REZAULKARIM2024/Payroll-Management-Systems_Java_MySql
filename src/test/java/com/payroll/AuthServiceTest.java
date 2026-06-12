package com.payroll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit 5 unit tests for {@link AuthService} (extracted from LoginTab).
 */
class AuthServiceTest {

    @Test
    @DisplayName("সঠিক username/password দিলে login সফল হবে")
    void correctCredentials_authenticate() {
        assertTrue(AuthService.authenticate("admin", "admin123"));
    }

    @ParameterizedTest
    @DisplayName("ভুল username বা password দিলে login ব্যর্থ হবে")
    @CsvSource({
            "admin, wrongpass",
            "wronguser, admin123",
            "wronguser, wrongpass",
            "Admin, admin123",   // case-sensitive
            "admin, Admin123",   // case-sensitive
            "'', ''",            // empty/blank
    })
    void wrongCredentials_areRejected(String username, String password) {
        assertFalse(AuthService.authenticate(username, password));
    }

    @Test
    @DisplayName("null username/password reject হবে, exception ছুঁড়বে না")
    void nullCredentials_areRejectedSafely() {
        assertFalse(AuthService.authenticate(null, "admin123"));
        assertFalse(AuthService.authenticate("admin", null));
        assertFalse(AuthService.authenticate(null, null));
    }
}
