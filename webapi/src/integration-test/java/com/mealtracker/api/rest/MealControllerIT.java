package com.mealtracker.api.rest;

import com.mealtracker.MealTrackerApplication;
import com.mealtracker.api.rest.meal.MealRequest;
import com.mealtracker.config.WebSecurityConfig;
import com.mealtracker.services.meal.DeleteMealsInput;
import com.mealtracker.services.meal.MealService;
import com.mealtracker.services.user.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static com.mealtracker.TestError.AUTHORIZATION_API_ACCESS_DENIED;
import static com.mealtracker.TestUser.NO_MEAL_MANAGEMENT;
import static com.mealtracker.request.AppRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {MealController.class})
@ContextConfiguration(classes = {MealTrackerApplication.class, WebSecurityConfig.class})
@Tag("integration")
@Tag("controller")
public class MealControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MealService mealService;

    @MockitoBean
    private UserService userService;

    @Test
    public void listMeal_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(get("/v1/meals").auth(NO_MEAL_MANAGEMENT))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    @Test
    public void addMeal_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(post("/v1/meals").auth(NO_MEAL_MANAGEMENT).content(mealRequest()))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    @Test
    public void deleteMeals_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(delete("/v1/meals").auth(NO_MEAL_MANAGEMENT).content(deleteMealsRequest(5L, 6L)))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    @Test
    public void updateMeal_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(put("/v1/meals/10").auth(NO_MEAL_MANAGEMENT).content(mealRequest()))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    @Test
    public void getMeal_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(get("/v1/meals/52").auth(NO_MEAL_MANAGEMENT))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    private MealRequest mealRequest() {
        return new MealRequest().consumerId(5L).calories(400).consumedDate("2019-04-02").consumedTime("10:00")
                .name("Ice Cream");
    }

    private DeleteMealsInput deleteMealsRequest(Long... ids) {
        var input = new DeleteMealsInput();
        input.setIds(Arrays.asList(ids));
        return input;
    }
}
