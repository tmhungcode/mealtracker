package com.mealtracker.api.rest.controller.user;

import com.mealtracker.MealTrackerApplication;
import com.mealtracker.api.rest.user.UserController;
import com.mealtracker.api.rest.user.builders.DomainUserBuilder;
import com.mealtracker.config.WebSecurityConfig;
import com.mealtracker.domains.Role;
import com.mealtracker.services.pagination.PageableOrder;
import com.mealtracker.services.user.*;
import com.mealtracker.utils.matchers.CurrentUserMatchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static com.mealtracker.TestError.AUTHORIZATION_API_ACCESS_DENIED;
import static com.mealtracker.TestUser.NO_USER_MANAGEMENT;
import static com.mealtracker.TestUser.ONLY_USER_MANAGEMENT;
import static com.mealtracker.api.rest.user.matchers.ListUsersInputMatchers.pagination;
import static com.mealtracker.request.AppRequestBuilders.*;
import static com.mealtracker.utils.MockPageBuilder.oneRowsPerPage;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller layer tests for UserController.
 * Tests user management operations (CRUD) for authenticated managers with
 * mocked services.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {UserController.class})
@ContextConfiguration(classes = {MealTrackerApplication.class, WebSecurityConfig.class})
@Tag("controller")
@Tag("webmvc")
@DisplayName("UserController - User Management")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserManagementServiceResolver serviceResolver;

    @MockitoBean
    private UserManagementService userManagementService;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(serviceResolver.resolve(CurrentUserMatchers.eq(ONLY_USER_MANAGEMENT))).thenReturn(userManagementService);
    }

    private ManageUserInput manageUserRequest() {
        var input = new ManageUserInput();
        input.setRole(Role.REGULAR_USER.name());
        input.setDailyCalorieLimit(1000);
        input.setEmail("superman@gmail.com");
        input.setFullName("Clark Kent");
        input.setPassword("HelloWorld");
        return input;
    }

    private DeleteUsersInput deleteUsersInput(Long... userIds) {
        var input = new DeleteUsersInput();
        input.setIds(Arrays.asList(userIds));
        return input;
    }

    @Nested
    @DisplayName("Add User Tests")
    class AddUserTests {

        @Test
        @DisplayName("Add user without USER_MANAGEMENT privilege - Expect 403 Forbidden")
        void addUser_NoUserManagementUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(post("/v1/users").auth(NO_USER_MANAGEMENT).content(manageUserRequest()))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Add user with null password - Expect 400 Bad Request")
        void addUser_NullPassword_ExpectBadInputError() throws Exception {
            var userWithoutPassword = manageUserRequest();
            userWithoutPassword.setPassword(null);

            mockMvc.perform(post("/v1/users").auth(ONLY_USER_MANAGEMENT).content(userWithoutPassword))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'password','message':'must not be null'}]}}"));
        }

        @Test
        @DisplayName("Add user with valid data - Expect user added successfully")
        void addUser_ValidRequest_ExpectUserAdded() throws Exception {
            mockMvc.perform(post("/v1/users").auth(ONLY_USER_MANAGEMENT).content(manageUserRequest()))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'User added successfully'}}"));
        }
    }

    @Nested
    @DisplayName("List Users Tests")
    class ListUsersTests {

        @Test
        @DisplayName("List users without USER_MANAGEMENT privilege - Expect 403 Forbidden")
        void listUsers_NoUserManagementUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(get("/v1/users").auth(NO_USER_MANAGEMENT))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("List users with valid request - Expect paginated user list")
        void listUsers_ValidRequest_ExpectUsersListed() throws Exception {
            var hulk = new DomainUserBuilder()
                    .calorieLimit(1500)
                    .email("hulk@abc.com")
                    .fullName("David Banner")
                    .id(99L).role(Role.USER_MANAGER).build();
            var userPage = oneRowsPerPage(494, hulk);
            when(userManagementService.listUsers(pagination("id", PageableOrder.DESC))).thenReturn(userPage);

            mockMvc.perform(get("/v1/users").auth(ONLY_USER_MANAGEMENT).oneRowPerPage())
                    .andExpect(status().isOk())
                    .andExpect(content().json(
                            "{'data':[{'id':99,'email':'hulk@abc.com','fullName':'David Banner','role':'USER_MANAGER','dailyCalorieLimit':1500}],'metaData':{'totalElements':494,'totalPages':494}}"));
        }
    }

    @Nested
    @DisplayName("Delete Users Tests")
    class DeleteUsersTests {

        @Test
        @DisplayName("Delete users without USER_MANAGEMENT privilege - Expect 403 Forbidden")
        void deleteUsers_NoUserManagementUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(delete("/v1/users").auth(NO_USER_MANAGEMENT).content(deleteUsersInput(5L, 7L)))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Delete users with empty request - Expect 400 Bad Request")
        void deleteUsers_BadInput_ExpectBadInputError() throws Exception {
            mockMvc.perform(delete("/v1/users").auth(ONLY_USER_MANAGEMENT).emptyJsonContent())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'ids','message':'must not be null'}]}}"));
        }

        @Test
        @DisplayName("Delete users with valid IDs - Expect users deleted successfully")
        void deleteUsers_ValidRequest_ExpectUsersDeleted() throws Exception {
            mockMvc.perform(delete("/v1/users").auth(ONLY_USER_MANAGEMENT).content(deleteUsersInput(1L, 2L)))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'Users deleted successfully'}}"));
        }
    }

    @Nested
    @DisplayName("Get User Tests")
    class GetUserTests {

        @Test
        @DisplayName("Get user without USER_MANAGEMENT privilege - Expect 403 Forbidden")
        void getUser_NoUserManagementUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(get("/v1/users/6").auth(NO_USER_MANAGEMENT))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Get user with valid ID - Expect user details returned")
        void getUser_ValidRequest_ExpectUserDetailsReturned() throws Exception {
            var user = new DomainUserBuilder()
                    .calorieLimit(1500)
                    .email("batman@abc.com")
                    .fullName("Bruce Wayne")
                    .id(15L).role(Role.ADMIN).build();

            when(userManagementService.getUser(eq(15L))).thenReturn(user);
            mockMvc.perform(get("/v1/users/15").auth(ONLY_USER_MANAGEMENT))
                    .andExpect(status().isOk())
                    .andExpect(content().json(
                            "{'data':{'id':15,'email':'batman@abc.com','fullName':'Bruce Wayne','role':'ADMIN','dailyCalorieLimit':1500}}"));
        }
    }

    @Nested
    @DisplayName("Update User Tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Update user without USER_MANAGEMENT privilege - Expect 403 Forbidden")
        void updateUser_NoUserManagementUser_ExpectAuthorizationError() throws Exception {
            mockMvc.perform(put("/v1/users/6").auth(NO_USER_MANAGEMENT).content(manageUserRequest()))
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.httpStatus())
                    .andExpect(AUTHORIZATION_API_ACCESS_DENIED.json());
        }

        @Test
        @DisplayName("Update user with null email - Expect 400 Bad Request")
        void updateUser_BadInput_ExpectBadInputError() throws Exception {
            var badInput = manageUserRequest();
            badInput.setEmail(null);

            mockMvc.perform(put("/v1/users/2").auth(ONLY_USER_MANAGEMENT).content(badInput))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(
                            "{'error':{'code':40000,'message':'Bad Input','errorFields':[{'name':'email','message':'must not be null'}]}}"));
        }

        @Test
        @DisplayName("Update user with valid data - Expect user updated successfully")
        void updateUser_ValidRequest_ExpectUserDetailsUpdated() throws Exception {
            mockMvc.perform(put("/v1/users/2").auth(ONLY_USER_MANAGEMENT).content(manageUserRequest()))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'User updated successfully'}}"));
        }

        @Test
        @DisplayName("Update user without password - Expect user updated successfully (password optional)")
        void updateUser_PasswordOption_ExpectUserDetails() throws Exception {
            var inputWithoutPassword = manageUserRequest();
            inputWithoutPassword.setPassword(null);

            mockMvc.perform(put("/v1/users/2").auth(ONLY_USER_MANAGEMENT).content(inputWithoutPassword))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{'data':{'message':'User updated successfully'}}"));
        }
    }
}
