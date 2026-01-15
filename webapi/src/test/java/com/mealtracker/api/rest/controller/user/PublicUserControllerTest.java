package com.mealtracker.api.rest.controller.user;

import com.mealtracker.MealTrackerApplication;
import com.mealtracker.api.rest.user.PublicUserController;
import com.mealtracker.api.rest.user.UserController;
import com.mealtracker.config.WebSecurityConfig;
import com.mealtracker.domains.Role;
import com.mealtracker.domains.User;
import com.mealtracker.exceptions.BadRequestAppException;
import com.mealtracker.services.user.*;
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

import static com.mealtracker.TestError.AUTHORIZATION_API_ACCESS_DENIED;
import static com.mealtracker.TestError.BAD_SPECIFIC_INPUT;
import static com.mealtracker.TestUser.USER;
import static com.mealtracker.request.AppRequestBuilders.post;
import static com.mealtracker.utils.matchers.UserRegistrationInputMatchers.email;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller layer tests for PublicUserController.
 * Tests public user registration and lookup with mocked services.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {PublicUserController.class, UserController.class})
@ContextConfiguration(classes = {MealTrackerApplication.class, WebSecurityConfig.class})
@Tag("controller")
@Tag("webmvc")
@DisplayName("PublicUserController - Public User Operations")
class PublicUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublicUserService publicUserService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserManagementServiceResolver managementServiceResolver;

    RegisterUserInput registrationRequest() {
        var request = new RegisterUserInput();
        request.setEmail("superman@gmail.com");
        request.setFullName("Superman");
        request.setPassword("JusticeLeague");
        return request;
    }

    User userWithCompleteDetails() {
        var user = new User();
        user.setEmail("hello@gmail.com");
        user.setFullName("Hello World");
        user.setEncryptedPassword("4895742");
        user.setId(5L);
        user.setDeleted(false);
        return user;
    }

    private ManageUserInput manageUserRequest() {
        var input = new ManageUserInput();
        input.setRole(Role.REGULAR_USER.name());
        input.setDailyCalorieLimit(1000);
        input.setEmail("panda@gmail.com");
        input.setFullName("Kungfu Kent");
        input.setPassword("Heyyoo");
        return input;
    }

    @Nested
    @DisplayName("User Registration Tests")
    class UserRegistrationTests {

        @Test
        @DisplayName("Authenticated user tries to register - Expect 403 Forbidden (redirected to management API)")
        void registerUser_RegularUserWithValidInput_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(post("/v1/users").auth(USER).content(manageUserRequest()))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Anonymous user registers with valid data - Expect user registered successfully")
        void registerUser_AnonymousWithValidInput_ExpectUserRegistered() throws Exception {
            var registrationInput = registrationRequest();
            mockMvc.perform(post("/v1/users").content(registrationInput))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'User registered successfully'}}"));
        }

        @Test
        @DisplayName("Register with invalid email - Expect 400 Bad Request with validation errors")
        void registerUser_InvalidEmail_ExpectEmailValidationErrors() throws Exception {
            String invalidEmail = "abc";
            var input = registrationRequest();
            input.setEmail(invalidEmail);

            mockMvc.perform(post("/v1/users").content(input))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'email','message':'size must be between 5 and 200'},{'name':'email','message':'must be a well-formed email address'}]}}"));
        }

        @Test
        @DisplayName("Register with invalid full name - Expect 400 Bad Request with validation errors")
        void registerUser_InvalidFullName_ExpectFullNameValidationErrors() throws Exception {
            String invalidFullName = "aa";
            var input = registrationRequest();
            input.setFullName(invalidFullName);

            mockMvc.perform(post("/v1/users").content(input))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'fullName','message':'size must be between 5 and 200'}]}}"));
        }

        @Test
        @DisplayName("Register with invalid password - Expect 400 Bad Request with validation errors")
        void registerUser_InvalidPassword_ExpectFullNameValidationErrors() throws Exception {
            String invalidPassword = "d";
            var input = registrationRequest();
            input.setPassword(invalidPassword);

            mockMvc.perform(post("/v1/users").content(input))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'password','message':'size must be between 5 and 100'}]}}"));
        }

        @Test
        @DisplayName("Register with existing email - Expect 400 Bad Request (email taken)")
        void registerUser_ExistingEmail_ExpectError() throws Exception {
            var input = registrationRequest();

            when(publicUserService.registerUser(email(input.getEmail())))
                    .thenThrow(BadRequestAppException.emailTaken("superman@gmail.com"));

            mockMvc.perform(post("/v1/users").content(input))
                    .andExpect(BAD_SPECIFIC_INPUT.httpStatus())
                    .andExpect(BAD_SPECIFIC_INPUT.json("Email superman@gmail.com is already taken"));
        }

        @Test
        @DisplayName("Register with valid user data - Expect user registered successfully")
        void registerUser_ValidUser_ExpectUserRegistered() throws Exception {
            var registrationInput = registrationRequest();
            mockMvc.perform(post("/v1/users").content(registrationInput))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'User registered successfully'}}"));
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Get user by email - Expect public user info returned")
        void getUser_ExistingUser_ExpectPublicUserInfoReturned() throws Exception {
            var user = userWithCompleteDetails();
            when(publicUserService.getByEmail(user.getEmail())).thenReturn(user);
            mockMvc.perform(get("/v1/users?email=" + user.getEmail()))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'fullName':'Hello World','email':'hello@gmail.com'}}"));
        }
    }
}
