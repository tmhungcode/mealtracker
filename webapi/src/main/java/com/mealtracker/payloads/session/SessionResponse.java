package com.mealtracker.payloads.session;

import com.mealtracker.payloads.SuccessEnvelop;
import com.mealtracker.services.session.AccessToken;

public record SessionResponse(String accessToken, String tokenType) {

    public static SuccessEnvelop<SessionResponse> envelop(AccessToken accessToken) {
        return new SuccessEnvelop<>(new SessionResponse(accessToken.getToken(), accessToken.getType()));
    }
}
