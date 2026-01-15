package com.mealtracker.validation;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class LocalDateFormatValidatorTest {

    @Test
    public void isValid_InputMissing_AllowNull_ExpectValid() {
        assertThat(validator(true).isValid(null, null)).isTrue();
    }

    @Test
    public void isValid_InputMissing_NotAllowNull_ExpectInvalid() {
        assertThat(validator(false).isValid(null, null)).isFalse();
    }

    @Test
    public void isValid_InputBadDateFormat_ExpectInvalid() {
        assertThat(validator(false).isValid("2018/05/06", null)).isFalse();
    }

    @Test
    public void isValid_InputValidDateFormat_ExpectValid() {
        assertThat(validator(false).isValid("2018-05-06", null)).isTrue();
    }

    private LocalDateFormatValidator validator(boolean nullable) {
        var constraintAnnotation = Mockito.mock(LocalDateFormat.class);
        when(constraintAnnotation.nullable()).thenReturn(nullable);

        var validator = new LocalDateFormatValidator();
        validator.initialize(constraintAnnotation);
        return validator;
    }
}
