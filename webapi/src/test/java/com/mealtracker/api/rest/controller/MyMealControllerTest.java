package com.mealtracker.api.rest.controller;

import com.mealtracker.MealTrackerApplication;
import com.mealtracker.api.rest.MyMealController;
import com.mealtracker.api.rest.meal.ListMyMealInputRequest;
import com.mealtracker.api.rest.meal.MealRequest;
import com.mealtracker.config.WebSecurityConfig;
import com.mealtracker.domains.Meal;
import com.mealtracker.services.meal.DeleteMealsInput;
import com.mealtracker.services.meal.MyMealService;
import com.mealtracker.services.user.UserService;
import com.mealtracker.utils.MockPageBuilder;
import com.mealtracker.utils.matchers.CurrentUserMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static com.mealtracker.TestError.AUTHORIZATION_API_ACCESS_DENIED;
import static com.mealtracker.TestUser.NO_MY_MEAL;
import static com.mealtracker.TestUser.USER;
import static com.mealtracker.api.rest.user.matchers.ListMyMealInputMatchers.eq;
import static com.mealtracker.api.rest.user.matchers.ListMyMealInputMatchers.fields;
import static com.mealtracker.request.AppRequestBuilders.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller layer tests for MyMealController.
 * Tests user meal management (CRUD operations) with mocked services.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {MyMealController.class})
@ContextConfiguration(classes = {MealTrackerApplication.class, WebSecurityConfig.class})
@Tag("controller")
@Tag("webmvc")
@DisplayName("MyMealController - User Meal Management")
class MyMealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MyMealService myMealService;

    private MealRequest addMealRequest() {
        return new MealRequest().calories(500).consumedDate("2019-05-01").consumedTime("02:25").name("Pizza");
    }

    private MealRequest updateMealRequest() {
        return new MealRequest().calories(400).consumedDate("2016-02-09").consumedTime("14:10").name("Soup");
    }

    private ListMyMealInputRequest listMyMealsInput() {
        return new ListMyMealInputRequest();
    }

    private DeleteMealsInput deleteMyMealsRequest(Long... ids) {
        var input = new DeleteMealsInput();
        input.setIds(Arrays.asList(ids));
        return input;
    }

    private Meal completeMealDetails() {
        Meal meal = new Meal();
        meal.setId(9L);
        meal.setConsumedDate(LocalDate.of(2011, 5, 8));
        meal.setConsumedTime(LocalTime.of(5, 5));
        meal.setCalories(100);
        meal.setName("Coffee");
        return meal;
    }

    @Nested
    @DisplayName("Add Meal Tests")
    class AddMealTests {

        @Test
        @DisplayName("Add meal without MY_MEALS privilege - Expect 403 Forbidden")
        void addMeal_NoMyMealUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(
                            post("/v1/users/me/meals").auth(NO_MY_MEAL).content(addMealRequest()))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Add meal with invalid date format - Expect 400 Bad Request")
        void addMeal_BadInput_ExpectBadInputError() throws Exception {
            mockMvc.perform(
                            post("/v1/users/me/meals").auth(USER).content(addMealRequest().consumedDate("2019/11/01")))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'consumedDate','message':'Date should be in this format yyyy-MM-dd'}]}}"));
        }

        @Test
        @DisplayName("Add meal with valid data - Expect meal added successfully")
        void addMeal_ValidAddMealRequest_ExpectMealAdded() throws Exception {
            mockMvc.perform(
                            post("/v1/users/me/meals").auth(USER).content(addMealRequest()))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'Meal added successfully'}}"));
        }
    }

    @Nested
    @DisplayName("Update Meal Tests")
    class UpdateMealTests {

        @Test
        @DisplayName("Update meal without MY_MEALS privilege - Expect 403 Forbidden")
        void updateMeal_NoMyMealUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(
                            put("/v1/users/me/meals/5").auth(NO_MY_MEAL).content(updateMealRequest()))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Update meal with valid data - Expect meal updated successfully")
        void updateMeal_ValidUpdateMealRequest_ExpectMealUpdated() throws Exception {
            mockMvc.perform(
                            put("/v1/users/me/meals/5").auth(USER).content(updateMealRequest()))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'Meal updated successfully'}}"));
        }
    }

    @Nested
    @DisplayName("Get Meal Tests")
    class GetMealTests {

        @Test
        @DisplayName("Get meal without MY_MEALS privilege - Expect 403 Forbidden")
        void getMeal_NoMyMealUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(
                            get("/v1/users/me/meals/155").auth(NO_MY_MEAL))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Get meal with valid ID - Expect meal details returned")
        void getMeal_MyMealUser_ExpectMealReturned() throws Exception {
            when(myMealService.getMeal(eq(4L), CurrentUserMatchers.eq(USER))).thenReturn(completeMealDetails());
            mockMvc.perform(
                            get("/v1/users/me/meals/4").auth(USER).content(updateMealRequest()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(
                            "{'data':{'id':9,'name':'Coffee','consumedDate':'2011-05-08','consumedTime':'05:05:00','calories':100}}"));
        }
    }

    @Nested
    @DisplayName("Delete Meals Tests")
    class DeleteMealsTests {

        @Test
        @DisplayName("Delete meals without MY_MEALS privilege - Expect 403 Forbidden")
        void deleteMeals_NoMyMealUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(
                            delete("/v1/users/me/meals").auth(NO_MY_MEAL).content(deleteMyMealsRequest(5L, 6L)))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Delete meals with no IDs - Expect 400 Bad Request")
        void deleteMeals_MyMealUser_NoIds_ExpectBadInputError() throws Exception {
            mockMvc.perform(
                            delete("/v1/users/me/meals").auth(USER).content(deleteMyMealsRequest()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'ids','message':'size must be between 1 and 50'}]}}"));
        }

        @Test
        @DisplayName("Delete meals with valid IDs - Expect meals deleted successfully")
        void deleteMeals_MyMealUser_SomeIds_ExpectMealsDeleted() throws Exception {
            mockMvc.perform(
                            delete("/v1/users/me/meals").auth(USER).content(deleteMyMealsRequest(1L, 5L)))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'Meals deleted successfully'}}"));
        }
    }

    @Nested
    @DisplayName("List Meals Tests")
    class ListMealsTests {

        @Test
        @DisplayName("List meals without MY_MEALS privilege - Expect 403 Forbidden")
        void listMeals_NoMyMealUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(
                            get("/v1/users/me/meals").auth(NO_MY_MEAL).content(listMyMealsInput()))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("List meals with invalid time format - Expect 400 Bad Request")
        void listMeals_BadInput_ExpectBadInputError() throws Exception {
            mockMvc.perform(
                            get("/v1/users/me/meals").param("fromTime", "99:15-00").auth(USER))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'fromTime','message':'Time should be in this format hh:mm'}]}}"));
        }

        @Test
        @DisplayName("List meals with valid filters - Expect meals returned")
        void listMeals_ValidInput_ExpectSomeData() throws Exception {
            var meal = new Meal();
            meal.setCalories(400);
            meal.setId(6L);
            meal.setConsumedDate(LocalDate.of(2000, 1, 1));
            meal.setConsumedTime(LocalTime.of(0, 0));
            meal.setName("Hacao");
            var mealPage = MockPageBuilder.oneRowsPerPage(215, meal);

            when(myMealService.listMeals(eq(fields().rowsPerPage(1).pageIndex(0)), CurrentUserMatchers.eq(USER)))
                    .thenReturn(mealPage);

            mockMvc.perform(
                            get("/v1/users/me/meals").auth(USER).oneRowPerPage())
                    .andExpect(status().isOk())
                    .andExpect(content().json(
                            "{'data':[{'id':6,'name':'Hacao','consumedDate':'2000-01-01','consumedTime':'00:00:00','calories':400}],'metaData':{'totalElements':215,'totalPages':215}}"));
        }
    }
}
