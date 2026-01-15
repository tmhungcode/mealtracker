package com.mealtracker.api.rest;

import com.mealtracker.payloads.MessageResponse;
import com.mealtracker.payloads.MetaSuccessEnvelop;
import com.mealtracker.payloads.PaginationMeta;
import com.mealtracker.payloads.SuccessEnvelop;
import com.mealtracker.payloads.meal.MyMealResponse;
import com.mealtracker.security.CurrentUser;
import com.mealtracker.services.meal.DeleteMealsInput;
import com.mealtracker.services.meal.ListMyMealsInput;
import com.mealtracker.services.meal.MyMealInput;
import com.mealtracker.services.meal.MyMealService;
import jakarta.validation.Valid;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users/me/meals")
@Secured("MY_MEALS")
public class MyMealController {

    private final MyMealService myMealService;

    public MyMealController(MyMealService myMealService) {
        this.myMealService = myMealService;
    }

    @GetMapping
    public MetaSuccessEnvelop<List<MyMealResponse>, PaginationMeta> listMeal(@Valid ListMyMealsInput input,
                                                                             CurrentUser currentUser) {
        var mealPage = myMealService.listMeals(input, currentUser);
        return MyMealResponse.envelop(mealPage);
    }

    @PostMapping
    public SuccessEnvelop<MessageResponse> addMeal(@Valid @RequestBody MyMealInput input, CurrentUser currentUser) {
        myMealService.addMeal(input, currentUser);
        return MessageResponse.of("Meal added successfully");
    }

    @DeleteMapping
    public SuccessEnvelop<MessageResponse> deleteMeals(@Valid @RequestBody DeleteMealsInput input,
                                                       CurrentUser currentUser) {
        myMealService.deleteMeals(input, currentUser);
        return MessageResponse.of("Meals deleted successfully");
    }

    @PutMapping("/{mealId}")
    public SuccessEnvelop<MessageResponse> updateMeal(@PathVariable long mealId,
                                                      @Valid @RequestBody MyMealInput input,
                                                      CurrentUser currentUser) {
        myMealService.updateMeal(mealId, input, currentUser);
        return MessageResponse.of("Meal updated successfully");
    }

    @GetMapping("/{mealId}")
    public SuccessEnvelop<MyMealResponse> getMeal(@PathVariable long mealId,
                                                  CurrentUser currentUser) {
        var meal = myMealService.getMeal(mealId, currentUser);
        return MyMealResponse.envelop(meal);
    }
}
