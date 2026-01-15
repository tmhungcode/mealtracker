package com.mealtracker.api.rest.controller;

import com.mealtracker.MealTrackerApplication;
import com.mealtracker.api.rest.MealController;
import com.mealtracker.api.rest.meal.MealRequest;
import com.mealtracker.config.WebSecurityConfig;
import com.mealtracker.services.meal.DeleteMealsInput;
import com.mealtracker.services.meal.MealService;
import com.mealtracker.services.user.UserService;
import org.junit.jupiter.api.DisplayName;
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

/**
 * Controller layer tests for MealController.
 * Tests HTTP request/response handling, authorization, and validation with
 * mocked services.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {MealController.class})
@ContextConfiguration(classes = {MealTrackerApplication.class, WebSecurityConfig.class})
@Tag("controller")
@Tag("webmvc")
@DisplayName("MealController - Admin Meal Management")
class MealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MealService mealService;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("List meals without MEAL_MANAGEMENT privilege - Expect 403 Forbidden")
    void listMeal_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(get("/v1/meals").auth(NO_MEAL_MANAGEMENT))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    @Test
    @DisplayName("Add meal without MEAL_MANAGEMENT privilege - Expect 403 Forbidden")
    void addMeal_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(post("/v1/meals").auth(NO_MEAL_MANAGEMENT).content(mealRequest()))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    @Test
    @DisplayName("Delete meals without MEAL_MANAGEMENT privilege - Expect 403 Forbidden")
    void deleteMeals_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(delete("/v1/meals").auth(NO_MEAL_MANAGEMENT).content(deleteMealsRequest(5L, 6L)))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    @Test
    @DisplayName("Update meal without MEAL_MANAGEMENT privilege - Expect 403 Forbidden")
    void updateMeal_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
        mockMvc.perform(put("/v1/meals/10").auth(NO_MEAL_MANAGEMENT).content(mealRequest()))
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
    }

    @Test
    @DisplayName("Get meal without MEAL_MANAGEMENT privilege - Expect 403 Forbidden")
    void getMeal_NoMealManagementUser_ExpectAuthorizationError() throws Exception {
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
