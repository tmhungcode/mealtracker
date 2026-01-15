package com.mealtracker.services.session;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SessionInput(
        @Email @NotNull String email,
        @NotNull String password) {
}
