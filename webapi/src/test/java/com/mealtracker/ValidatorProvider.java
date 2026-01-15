package com.mealtracker;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

public class ValidatorProvider {
    private static Validator validator;

    public static Validator getValidator() {
        if (validator != null) {
            return validator;
        }
        validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(
                        new ResourceBundleMessageInterpolator(
                                new PlatformResourceBundleLocator("messages/messages")
                        )
                )
                .buildValidatorFactory()
                .getValidator();
        return validator;
    }
}
