package com.mealtracker.payloads.meal;

import com.mealtracker.domains.Meal;
import com.mealtracker.payloads.MetaSuccessEnvelop;
import com.mealtracker.payloads.PaginationMeta;
import com.mealtracker.payloads.SuccessEnvelop;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public record MealResponse(long id, String name, LocalDate consumedDate, LocalTime consumedTime, int calories,
                           BriefUserResponse consumer) {

    private MealResponse(Meal meal) {
        this(meal.getId(), meal.getName(), meal.getConsumedDate(), meal.getConsumedTime(), meal.getCalories(),
                new BriefUserResponse(meal.getConsumer()));
    }

    public static SuccessEnvelop<MealResponse> envelop(Meal meal) {
        return new SuccessEnvelop<>(new MealResponse(meal));
    }

    public static MetaSuccessEnvelop<List<MealResponse>, PaginationMeta> envelop(Page<Meal> mealPage) {
        var myMealResponses = mealPage.getContent().stream().map(MealResponse::new).collect(Collectors.toList());
        return new MetaSuccessEnvelop<>(myMealResponses, PaginationMeta.of(mealPage));
    }
}
