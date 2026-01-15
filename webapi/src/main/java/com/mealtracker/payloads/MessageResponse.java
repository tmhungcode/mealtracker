package com.mealtracker.payloads;

public record MessageResponse(String message) {

    public static SuccessEnvelop<MessageResponse> of(String message) {
        return new SuccessEnvelop<>(new MessageResponse(message));
    }
}
