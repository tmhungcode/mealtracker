package com.mealtracker.services.usersettings;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MySettingsInput {

    @Min(0)
    @Max(50000)
    private Integer dailyCalorieLimit;

    public boolean isDailyCalorieLimitPatched() {
        return dailyCalorieLimit != null;
    }
}
