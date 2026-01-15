package com.mealtracker.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class ValueInListValidatorTest {

    @Test
    public void isValid_InputInList_ExpectValid() {
        Assertions.assertThat(validator("ONE", "TWO", "THREE").isValid("TWO", null)).isTrue();
    }

    @Test
    public void isValid_InputNotInList_ExpectInvalid() {
        Assertions.assertThat(validator("1", "2", "3").isValid("5", null)).isFalse();
    }

    private ValueInListValidator validator(String... value) {
        var constraintAnnotation = Mockito.mock(ValueInList.class);
        when(constraintAnnotation.value()).thenReturn(value);

        var validator = new ValueInListValidator();
        validator.initialize(constraintAnnotation);
        return validator;
    }

}
