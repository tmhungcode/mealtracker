package com.mealtracker.services.user;

import com.mealtracker.domains.User;
import com.mealtracker.domains.UserSettings;
import com.mealtracker.validation.OnAdd;
import com.mealtracker.validation.OnUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserInput {

    @NotNull(groups = {OnAdd.class, OnUpdate.class})
    @Email(groups = {OnAdd.class, OnUpdate.class})
    @Size(min = 5, max = 200, groups = {OnAdd.class, OnUpdate.class})
    private String email;

    @NotNull(groups = {OnAdd.class, OnUpdate.class})
    @Size(min = 5, max = 200, groups = {OnAdd.class, OnUpdate.class})
    private String fullName;

    @NotNull(groups = {OnAdd.class})
    @Size(min = 5, max = 100, groups = {OnAdd.class, OnUpdate.class})
    private String password;

    public User toUser() {
        var user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPassword(password);
        user.setUserSettings(new UserSettings());
        return user;
    }
}
