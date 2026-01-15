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

public record MyMealResponse(long id, String name, LocalDate consumedDate, LocalTime consumedTime, int calories) {

    public static MyMealResponse of(Meal meal) {
        return new MyMealResponse(meal.getId(), meal.getName(), meal.getConsumedDate(), meal.getConsumedTime(), meal.getCalories());
    }

    public static SuccessEnvelop<MyMealResponse> envelop(Meal meal) {
        return new SuccessEnvelop<>(of(meal));
    }

    public static MetaSuccessEnvelop<List<MyMealResponse>, PaginationMeta> envelop(Page<Meal> mealPage) {
        var myMealResponses = mealPage.getContent().stream().map(MyMealResponse::of).collect(Collectors.toList());
        return new MetaSuccessEnvelop<>(myMealResponses, PaginationMeta.of(mealPage));
    }
}
