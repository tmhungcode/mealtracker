package com.mealtracker;

import com.mealtracker.domains.Privilege;
import com.mealtracker.domains.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static com.mealtracker.domains.Privilege.*;
import static java.util.Arrays.asList;

public record TestUser(long id, String email, String fullName, boolean enabled, Role role, List<Privilege> privileges,
                       String token) {

    public static final TestUser ADMIN = TestUser.builder()
            .id(1L)
            .email("admin@gmail.com")
            .fullName("Admin")
            .enabled(true)
            .role(Role.ADMIN).privileges(asList(MY_MEALS, MEAL_MANAGEMENT, USER_MANAGEMENT))
            .build();
    public static final TestUser USER_MANAGER = TestUser.builder()
            .id(2L)
            .email("user_manager@gmail.com")
            .fullName("User Manager")
            .enabled(true)
            .role(Role.USER_MANAGER).privileges(asList(MY_MEALS, USER_MANAGEMENT))
            .build();
    public static final TestUser USER = TestUser.builder()
            .id(3L)
            .email("regular_user@gmail.com")
            .fullName("Regular User")
            .enabled(true)
            .role(Role.REGULAR_USER).privileges(List.of(MY_MEALS))
            .build();
    public static final TestUser NO_MY_MEAL = TestUser.builder()
            .id(4L)
            .email("no_my_meal@gmail.com")
            .fullName("No My Meal")
            .enabled(true)
            .role(Role.REGULAR_USER).privileges(TestPrivilege.exclude(MY_MEALS))
            .build();
    public static final TestUser NO_USER_MANAGEMENT = TestUser.builder()
            .id(5L)
            .email("no_user_managent@gmail.com")
            .fullName("No User Management")
            .enabled(true)
            .role(Role.REGULAR_USER).privileges(TestPrivilege.exclude(USER_MANAGEMENT))
            .build();
    public static final TestUser NO_MEAL_MANAGEMENT = TestUser.builder()
            .id(6L)
            .email("no_meal_managent@gmail.com")
            .fullName("No Meal Management")
            .enabled(true)
            .role(Role.REGULAR_USER).privileges(TestPrivilege.exclude(MEAL_MANAGEMENT))
            .build();
    public static final TestUser ONLY_USER_MANAGEMENT = TestUser.builder()
            .id(7L)
            .email("only_user_management@gmail.com")
            .fullName("Only User Management")
            .enabled(true)
            .role(Role.REGULAR_USER).privileges(List.of(USER_MANAGEMENT))
            .build();

    static TestUserBuilder builder() {
        return new TestUserBuilder();
    }

    public static class TestUserBuilder {
        private static final String TEST_JWT_SECRET_KEY = "JWTSuperSecretKeyThatIsLongEnoughForHS512AlgorithmRequiring512BitsMinimumLength!";
        private static final String JWT_TOKEN_TEMPLATE = "Bearer %s";
        private long id;
        private String email;
        private String fullName;
        private boolean enabled;
        private Role role;
        private List<Privilege> privileges;

        TestUserBuilder() {
        }

        public TestUserBuilder id(final long id) {
            this.id = id;
            return this;
        }

        public TestUserBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public TestUserBuilder fullName(final String fullName) {
            this.fullName = fullName;
            return this;
        }

        public TestUserBuilder enabled(final boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public TestUserBuilder role(final Role role) {
            this.role = role;
            return this;
        }

        public TestUserBuilder privileges(final List<Privilege> privileges) {
            this.privileges = privileges;
            return this;
        }

        public TestUser build() {
            var claims = new HashMap<String, Object>();
            claims.put("id", id);
            claims.put("email", email);
            claims.put("role", role);
            claims.put("privileges", privileges);
            claims.put("fullName", fullName);
            var secretKey = Keys.hmacShaKeyFor(TEST_JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            var jwt = Jwts.builder()
                    .claims().add(claims).and()
                    .signWith(secretKey, Jwts.SIG.HS512)
                    .compact();

            return new TestUser(this.id, this.email, this.fullName, this.enabled, this.role, this.privileges, String.format(JWT_TOKEN_TEMPLATE, jwt));
        }

    }
}
