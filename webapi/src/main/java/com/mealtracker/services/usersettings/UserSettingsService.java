package com.mealtracker.services.usersettings;

import com.mealtracker.domains.UserSettings;
import com.mealtracker.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserSettingsService {

    private final UserRepository userRepository;

    public UserSettingsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserSettings getUserSettings(long userId) {
        var user = userRepository.getReferenceById(userId);
        return user.getUserSettings();
    }

    public UserSettings updateUserSettings(long userId, MySettingsInput input) {
        var user = userRepository.getReferenceById(userId);
        if (input.isDailyCalorieLimitPatched()) {
            user.getUserSettings().setDailyCalorieLimit(input.getDailyCalorieLimit());
        }
        return userRepository.save(user).getUserSettings();
    }
}
