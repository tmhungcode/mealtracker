package com.mealtracker.domains;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.mealtracker.domains.Privilege.*;
import static com.mealtracker.domains.Role.*;

public class RoleTest {
    @Test
    public void regularUser_GetPrivileges_ExpectMyMeals() {
        Assertions.assertThat(REGULAR_USER.getPrivileges()).containsExactly(MY_MEALS);
    }

    @Test
    public void userManager_GetPrivileges_ExpectMyMealsAndUserManagement() {
        Assertions.assertThat(USER_MANAGER.getPrivileges()).containsExactlyInAnyOrder(MY_MEALS, USER_MANAGEMENT);
    }

    @Test
    public void userManager_GetPrivileges_ExpectMyMealsAndUserManagementAndMealManagement() {
        Assertions.assertThat(ADMIN.getPrivileges()).containsExactlyInAnyOrder(MY_MEALS, USER_MANAGEMENT, MEAL_MANAGEMENT);
    }
}
