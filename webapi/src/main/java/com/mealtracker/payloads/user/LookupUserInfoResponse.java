package com.mealtracker.payloads.user;

import com.mealtracker.domains.User;
import com.mealtracker.payloads.MetaSuccessEnvelop;
import com.mealtracker.payloads.PaginationMeta;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public record LookupUserInfoResponse(long id, String email, String fullName) {

    public LookupUserInfoResponse(User user) {
        this(user.getId(), user.getEmail(), user.getFullName());
    }

    public static MetaSuccessEnvelop<List<LookupUserInfoResponse>, PaginationMeta> envelop(Page<User> userPage) {
        var users = userPage.getContent().stream().map(LookupUserInfoResponse::new).collect(Collectors.toList());
        return new MetaSuccessEnvelop<>(users, PaginationMeta.of(userPage));
    }
}
