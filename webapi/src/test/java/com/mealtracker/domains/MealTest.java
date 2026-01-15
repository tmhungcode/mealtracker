package com.mealtracker.domains;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Meal Entity")
class MealTest {

    private Meal createMeal(Long id, String name) {
        Meal meal = new Meal();
        meal.setId(id);
        meal.setName(name);
        meal.setConsumedDate(LocalDate.now());
        meal.setConsumedTime(LocalTime.of(12, 0));
        meal.setCalories(500);
        return meal;
    }

    @Nested
    @DisplayName("equals() and hashCode()")
    class EqualsAndHashCode {

        @Test
        @DisplayName("should consider meals with same ID as equal")
        void samIdEquals() {
            // Given: Two meals with same ID but different data
            Meal meal1 = createMeal(1L, "Breakfast");
            Meal meal2 = createMeal(1L, "Lunch");

            // When/Then: They should be equal
            assertThat(meal1).isEqualTo(meal2);
            assertThat(meal1.hashCode()).isEqualTo(meal2.hashCode());
        }

        @Test
        @DisplayName("should consider meals with different IDs as not equal")
        void differentIdNotEquals() {
            // Given: Two meals with different IDs but same data
            Meal meal1 = createMeal(1L, "Breakfast");
            Meal meal2 = createMeal(2L, "Breakfast");

            // When/Then: They should not be equal
            assertThat(meal1).isNotEqualTo(meal2);
        }

        @Test
        @DisplayName("should consider transient meals (no ID) as not equal")
        void transientMealsNotEquals() {
            // Given: Two new meals without IDs
            Meal meal1 = createMeal(null, "Breakfast");
            Meal meal2 = createMeal(null, "Breakfast");

            // When/Then: They should not be equal (not yet persisted)
            assertThat(meal1).isNotEqualTo(meal2);
        }

        @Test
        @DisplayName("should work correctly in HashSet (no duplicates)")
        void worksInHashSet() {
            // Given: A set and a meal
            Set<Meal> meals = new HashSet<>();
            Meal meal = createMeal(1L, "Breakfast");

            // When: Adding the same meal twice
            meals.add(meal);
            meals.add(meal);

            // Then: Set should contain only one instance
            assertThat(meals).hasSize(1);
        }

        @Test
        @DisplayName("should maintain equality after detachment")
        void equalsAfterDetachment() {
            // Given: A meal that simulates being detached
            Meal meal1 = createMeal(1L, "Breakfast");
            Meal meal2 = createMeal(1L, "Breakfast");

            // Simulate setting consumer (lazy relationship)
            User consumer = new User();
            consumer.setId(100L);
            meal1.setConsumer(consumer);
            // meal2 has no consumer (simulating detached state)

            // When/Then: Should still be equal (based on ID only, not relationships)
            assertThat(meal1).isEqualTo(meal2);
        }
    }

    @Nested
    @DisplayName("toString()")
    class ToStringTest {

        @Test
        @DisplayName("should not include consumer relationship (avoid lazy loading)")
        void doesNotIncludeConsumer() {
            // Given: A meal with a lazy consumer
            Meal meal = createMeal(1L, "Breakfast");
            User consumer = new User();
            consumer.setId(100L);
            meal.setConsumer(consumer);

            // When: Calling toString
            String result = meal.toString();

            // Then: Should not include consumer details (avoids lazy loading)
            assertThat(result).contains("id=1");
            assertThat(result).contains("name='Breakfast'");
            assertThat(result).doesNotContain("consumer");
            assertThat(result).doesNotContain("User");
        }
    }
}
