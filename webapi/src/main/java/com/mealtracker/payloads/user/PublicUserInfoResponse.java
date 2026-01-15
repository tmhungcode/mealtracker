package com.mealtracker.payloads.user;

import com.mealtracker.domains.User;
import com.mealtracker.payloads.SuccessEnvelop;

public record PublicUserInfoResponse(String fullName, String email) {

    public static SuccessEnvelop<PublicUserInfoResponse> of(User user) {
        return new SuccessEnvelop<>(new PublicUserInfoResponse(user.getFullName(), user.getEmail()));
    }
}
