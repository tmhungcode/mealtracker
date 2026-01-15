package com.mealtracker.api.rest;

import com.mealtracker.payloads.MessageResponse;
import com.mealtracker.payloads.MetaSuccessEnvelop;
import com.mealtracker.payloads.PaginationMeta;
import com.mealtracker.payloads.SuccessEnvelop;
import com.mealtracker.payloads.meal.MealResponse;
import com.mealtracker.services.meal.DeleteMealsInput;
import com.mealtracker.services.meal.ListMealsInput;
import com.mealtracker.services.meal.MealInput;
import com.mealtracker.services.meal.MealService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Secured("MEAL_MANAGEMENT")
@RestController
@RequestMapping("/v1/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    public MetaSuccessEnvelop<List<MealResponse>, PaginationMeta> listMeals(@Valid ListMealsInput input) {
        var mealPage = mealService.listMeals(input);
        return MealResponse.envelop(mealPage);
    }

    @PostMapping
    public SuccessEnvelop<MessageResponse> addMeal(@Valid @RequestBody MealInput mealInput) {
        mealService.addMeal(mealInput);
        return MessageResponse.of("Meal added successfully");
    }

    @DeleteMapping
    public SuccessEnvelop<MessageResponse> deleteMeals(@Valid @RequestBody DeleteMealsInput input) {
        mealService.deleteMeals(input);
        return MessageResponse.of("Meals deleted successfully");
    }

    @PutMapping("/{mealId}")
    public SuccessEnvelop<MessageResponse> updateMeal(@PathVariable long mealId,
                                                      @Valid @RequestBody MealInput mealInput) {
        mealService.updateMeal(mealId, mealInput);
        return MessageResponse.of("Meal updated successfully");
    }

    @GetMapping("/{mealId}")
    public SuccessEnvelop<MealResponse> getMeal(@PathVariable long mealId) {
        var meal = mealService.getMeal(mealId);
        return MealResponse.envelop(meal);
    }

}
