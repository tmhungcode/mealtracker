package com.mealtracker.domains;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class UserSettings {
    private static final int DISABLED_CALORIE_LIMIT = 0;

    @Column(name = "daily_calorie_limit", nullable = false)
    private int dailyCalorieLimit = DISABLED_CALORIE_LIMIT;

    public boolean isCalorieLimitEnabled() {
        return dailyCalorieLimit > DISABLED_CALORIE_LIMIT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserSettings that = (UserSettings) o;
        return dailyCalorieLimit == that.dailyCalorieLimit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dailyCalorieLimit);
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "dailyCalorieLimit=" + dailyCalorieLimit +
                '}';
    }
}
