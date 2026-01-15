package com.mealtracker.payloads.user;

import com.mealtracker.domains.Role;
import com.mealtracker.domains.User;
import com.mealtracker.payloads.MetaSuccessEnvelop;
import com.mealtracker.payloads.PaginationMeta;
import com.mealtracker.payloads.SuccessEnvelop;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public record ManageUserInfoResponse(long id, String email, String fullName, Role role, long dailyCalorieLimit) {

    private ManageUserInfoResponse(User user) {
        this(user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getUserSettings().getDailyCalorieLimit());
    }

    public static MetaSuccessEnvelop<List<ManageUserInfoResponse>, PaginationMeta> envelop(Page<User> userPage) {
        var users = userPage.getContent().stream().map(ManageUserInfoResponse::new).collect(Collectors.toList());
        return new MetaSuccessEnvelop<>(users, PaginationMeta.of(userPage));
    }

    public static SuccessEnvelop<ManageUserInfoResponse> envelop(User user) {
        return new SuccessEnvelop<>(new ManageUserInfoResponse(user));
    }
}
