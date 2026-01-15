package com.mealtracker.payloads.alert;

import com.mealtracker.payloads.SuccessEnvelop;
import com.mealtracker.services.alert.CalorieAlertOutput;

public record CalorieAlertResponse(boolean alerted, int dailyCalorieLimit, int totalCalories) {

    public static SuccessEnvelop<CalorieAlertResponse> of(CalorieAlertOutput calorieAlert) {
        var response = new CalorieAlertResponse(calorieAlert.isAlerted(),
                calorieAlert.getDailyCalorieLimit(), calorieAlert.getTotalCalories());
        return new SuccessEnvelop<>(response);
    }
}
