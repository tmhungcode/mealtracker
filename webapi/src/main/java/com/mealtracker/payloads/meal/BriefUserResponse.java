package com.mealtracker.payloads.meal;

import com.mealtracker.domains.User;

public record BriefUserResponse(long id, String email, String fullName) {

    public BriefUserResponse(User user) {
        this(user.getId(), user.getEmail(), user.getFullName());
    }
}
