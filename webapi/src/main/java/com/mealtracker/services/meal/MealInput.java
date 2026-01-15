package com.mealtracker.services.meal;

import com.mealtracker.domains.Meal;
import com.mealtracker.domains.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MealInput extends MyMealInput {

    @NotNull
    Long consumerId;

    public Meal toMeal() {
        var meal = super.toMeal();
        var consumer = new User();
        consumer.setId(consumerId);
        meal.setConsumer(consumer);
        return meal;
    }

    public void merge(Meal existingMeal) {
        super.merge(existingMeal);
        var consumer = new User();
        consumer.setId(consumerId);
        existingMeal.setConsumer(consumer);
    }
}
