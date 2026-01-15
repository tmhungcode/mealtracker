package com.mealtracker.api.rest;

import com.mealtracker.payloads.MessageResponse;
import com.mealtracker.payloads.SuccessEnvelop;
import com.mealtracker.payloads.me.GetMySettingsResponse;
import com.mealtracker.security.CurrentUser;
import com.mealtracker.services.usersettings.MySettingsInput;
import com.mealtracker.services.usersettings.UserSettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/me")
public class MeController {

    private final UserSettingsService userSettingsService;

    public MeController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    public SuccessEnvelop<GetMySettingsResponse> getMySettings(CurrentUser currentUser) {
        var userSettings = userSettingsService.getUserSettings(currentUser.getId());
        return GetMySettingsResponse.envelop(userSettings);
    }

    @PatchMapping
    public SuccessEnvelop<MessageResponse> updateMySettings(CurrentUser currentUser,
                                                            @Valid @RequestBody MySettingsInput updateRequest) {
        userSettingsService.updateUserSettings(currentUser.getId(), updateRequest);
        return MessageResponse.of("User settings updated successfully");
    }

}
