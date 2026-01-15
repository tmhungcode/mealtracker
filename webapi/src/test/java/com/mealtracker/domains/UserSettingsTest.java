package com.mealtracker.domains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSettings Embeddable")
public class UserSettingsTest {

    private UserSettings settings(int dailyCalorieLimit) {
        var settings = new UserSettings();
        settings.setDailyCalorieLimit(dailyCalorieLimit);
        return settings;
    }

    @Nested
    @DisplayName("Business Logic")
    class BusinessLogic {

        @Test
        @DisplayName("should be disabled when limit is zero")
        void isCalorieLimitEnabled_LimitAsZero_ExpectDisabled() {
            // Given: Settings with zero limit
            UserSettings settings = settings(0);

            // When/Then: Should be disabled
            assertThat(settings.isCalorieLimitEnabled()).isFalse();
        }

        @Test
        @DisplayName("should be enabled when limit is more than zero")
        void isCalorieLimitEnabled_LimitMoreThanZero_ExpectEnabled() {
            // Given: Settings with positive limit
            UserSettings settings = settings(125);

            // When/Then: Should be enabled
            assertThat(settings.isCalorieLimitEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("equals() and hashCode()")
    class EqualsAndHashCode {

        @Test
        @DisplayName("should consider settings with same limit as equal")
        void sameLimitEquals() {
            // Given: Two settings with same calorie limit
            UserSettings settings1 = settings(2000);
            UserSettings settings2 = settings(2000);

            // When/Then: They should be equal
            assertThat(settings1).isEqualTo(settings2);
            assertThat(settings1.hashCode()).isEqualTo(settings2.hashCode());
        }

        @Test
        @DisplayName("should consider settings with different limits as not equal")
        void differentLimitNotEquals() {
            // Given: Two settings with different calorie limits
            UserSettings settings1 = settings(2000);
            UserSettings settings2 = settings(2500);

            // When/Then: They should not be equal
            assertThat(settings1).isNotEqualTo(settings2);
        }

        @Test
        @DisplayName("should handle zero limit correctly in equals")
        void zeroLimitEquals() {
            // Given: Two settings with zero limit
            UserSettings settings1 = settings(0);
            UserSettings settings2 = settings(0);

            // When/Then: They should be equal
            assertThat(settings1).isEqualTo(settings2);
        }
    }
}
