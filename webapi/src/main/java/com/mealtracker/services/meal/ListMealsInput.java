package com.mealtracker.services.meal;

import com.mealtracker.services.pagination.SingleSortableColumnPageableParams;
import com.mealtracker.validation.ValueInList;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListMealsInput extends SingleSortableColumnPageableParams {

    @ValueInList(value = {"name", "consumedDate", "consumedTime", "calories"})
    private String orderBy = "consumedDate";

}
