package com.mealtracker.api.rest.controller;

import com.mealtracker.MealTrackerApplication;
import com.mealtracker.api.rest.MyAlertController;
import com.mealtracker.config.WebSecurityConfig;
import com.mealtracker.services.alert.CalorieAlertOutput;
import com.mealtracker.services.alert.CalorieAlertService;
import com.mealtracker.services.user.UserService;
import com.mealtracker.utils.matchers.CurrentUserMatchers;
import com.mealtracker.utils.matchers.LocalDateMatchers;
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

import static com.mealtracker.TestError.AUTHENTICATION_MISSING_TOKEN;
import static com.mealtracker.TestUser.USER;
import static com.mealtracker.request.AppRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller layer tests for MyAlertController.
 * Tests calorie alert retrieval for authenticated users with mocked services.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {MyAlertController.class})
@ContextConfiguration(classes = {MealTrackerApplication.class, WebSecurityConfig.class})
@Tag("controller")
@Tag("webmvc")
@DisplayName("MyAlertController - Calorie Alerts")
class MyAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalorieAlertService calorieAlertService;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Get calorie alert without authentication - Expect 401 Unauthorized")
    void getCalorieAlert_Anonymous_ExpectAuthenticationError() throws Exception {
        mockMvc.perform(get("/v1/users/me/alerts/calorie?date=2016-05-04"))
                .andExpect(AUTHENTICATION_MISSING_TOKEN.httpStatus())
                .andExpect(AUTHENTICATION_MISSING_TOKEN.json());
    }

    @Test
    @DisplayName("Get calorie alert for authenticated user - Expect alert details returned")
    void getCalorieAlert_AuthenticatedUser_ExpectAlertDetailsReturned() throws Exception {
        var date = "2018-12-01";
        when(calorieAlertService.getAlert(LocalDateMatchers.eq(date), CurrentUserMatchers.eq(USER)))
                .thenReturn(new CalorieAlertOutput(true, 2500, 4000));

        mockMvc.perform(get("/v1/users/me/alerts/calorie?date=" + date).auth(USER))
                .andExpect(status().isOk())
                .andExpect(content().json("{'data':{'alerted':true,'dailyCalorieLimit':2500,'totalCalories':4000}}"));
    }
}
