package com.mealtracker.services.alert;

import com.mealtracker.validation.LocalDateFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CalorieAlertInput {

    @NotNull
    @LocalDateFormat
    private String date;

    public LocalDate getDate() {
        return LocalDate.parse(date);
    }

}
