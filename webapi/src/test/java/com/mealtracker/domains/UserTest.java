package com.mealtracker.domains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Entity")
public class UserTest {

    private User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFullName("Test User");
        user.setEncryptedPassword("encrypted");
        user.setRole(Role.REGULAR_USER);
        user.setUserSettings(new UserSettings());
        return user;
    }

    @Nested
    @DisplayName("Business Logic")
    class BusinessLogic {

        @Test
        @DisplayName("should be disabled when deleted")
        void isEnabled_UserDeleted_ExpectDisabled() {
            // Given: A deleted user
            var user = new User();
            user.setDeleted(true);

            // When/Then: Should be disabled
            assertThat(user.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("should be enabled when not deleted")
        void isEnabled_UserNotDeleted_ExpectEnabled() {
            // Given: A non-deleted user
            var user = new User();
            user.setDeleted(false);

            // When/Then: Should be enabled
            assertThat(user.isEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("equals() and hashCode()")
    class EqualsAndHashCode {

        @Test
        @DisplayName("should consider users with same ID as equal")
        void sameIdEquals() {
            // Given: Two users with same ID but different data
            User user1 = createUser(1L, "user1@example.com");
            User user2 = createUser(1L, "user2@example.com");

            // When/Then: They should be equal
            assertThat(user1).isEqualTo(user2);
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }

        @Test
        @DisplayName("should consider users with different IDs as not equal")
        void differentIdNotEquals() {
            // Given: Two users with different IDs but same email
            User user1 = createUser(1L, "user@example.com");
            User user2 = createUser(2L, "user@example.com");

            // When/Then: They should not be equal
            assertThat(user1).isNotEqualTo(user2);
        }

        @Test
        @DisplayName("should consider transient users (no ID) as not equal")
        void transientUsersNotEquals() {
            // Given: Two new users without IDs
            User user1 = createUser(null, "user1@example.com");
            User user2 = createUser(null, "user2@example.com");

            // When/Then: They should not be equal (not yet persisted)
            assertThat(user1).isNotEqualTo(user2);
        }

        @Test
        @DisplayName("should work correctly in HashSet (no duplicates)")
        void worksInHashSet() {
            // Given: A set and a user
            Set<User> users = new HashSet<>();
            User user = createUser(1L, "user@example.com");

            // When: Adding the same user twice
            users.add(user);
            users.add(user);

            // Then: Set should contain only one instance
            assertThat(users).hasSize(1);
        }

        @Test
        @DisplayName("should maintain equality after modifying non-ID fields")
        void equalsAfterFieldModification() {
            // Given: Two users with same ID
            User user1 = createUser(1L, "user@example.com");
            User user2 = createUser(1L, "user@example.com");

            // When: Modifying non-ID fields
            user1.setFullName("John Doe");
            user2.setFullName("Jane Smith");
            user1.setRole(Role.ADMIN);
            user2.setRole(Role.REGULAR_USER);

            // Then: Should still be equal (based on ID only)
            assertThat(user1).isEqualTo(user2);
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToStringTest {

        @Test
        @DisplayName("should not include sensitive data (password)")
        void doesNotIncludeSensitiveData() {
            // Given: A user with password
            User user = createUser(1L, "user@example.com");
            user.setPassword("plainPassword");
            user.setEncryptedPassword("$2a$10$encryptedHash");

            // When: Calling toString
            String result = user.toString();

            // Then: Should not expose passwords
            assertThat(result).contains("id=1");
            assertThat(result).contains("user@example.com");
            assertThat(result).doesNotContain("plainPassword");
            assertThat(result).doesNotContain("encryptedPassword");
            assertThat(result).doesNotContain("$2a$10$");
        }
    }
}
