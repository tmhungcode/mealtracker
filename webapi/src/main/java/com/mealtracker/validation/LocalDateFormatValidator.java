package com.mealtracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateFormatValidator implements ConstraintValidator<LocalDateFormat, String> {
    private boolean nullable;

    @Override
    public void initialize(LocalDateFormat constraintAnnotation) {
        nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext context) {
        if (input == null) {
            return nullable;
        }

        try {
            LocalDate.parse(input);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
