package com.mealtracker.services.meal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class DeleteMealsInput {

    @NotNull
    @Size(min = 1, max = 50)
    private List<Long> ids;

    public List<Long> getIds() {
        return ids == null ? Collections.emptyList() : ids;
    }
}
