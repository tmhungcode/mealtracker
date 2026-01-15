package com.mealtracker.services.user;

import com.mealtracker.ValidatorProvider;
import com.mealtracker.services.session.SessionInput;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static com.mealtracker.assertions.AppAssertions.assertThat;

public class SessionInputTest {

    private final Validator validator = ValidatorProvider.getValidator();

    @Test
    public void input_EmailNull_ExpectNotNullViolation() {
        var input = new SessionInput(null, "password");
        assertThat(validator.validate(input)).violateNotNull("email");
    }

    @Test
    public void input_EmailBadFormat_ExpectBadEmailFormatViolation() {
        var input = new SessionInput("abc", "password");
        assertThat(validator.validate(input)).violateEmailFormat("email");
    }

    @Test
    public void input_PasswordNull_ExpectNotNullViolation() {
        var input = new SessionInput("abc@gmail.com", null);
        assertThat(validator.validate(input)).violateNotNull("password");
    }

    private SessionInput validInput() {
        return new SessionInput("abc@gmail.com", "helloworld");
    }
}
