package com.mealtracker.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealtracker.TestUser;
import jakarta.servlet.ServletContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class AppRequestBuilder implements RequestBuilder {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final MockHttpServletRequestBuilder builder;

    public AppRequestBuilder(MockHttpServletRequestBuilder builder) {
        this.builder = builder;
        this.builder.characterEncoding("utf-8");
    }

    private static String json(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MockHttpServletRequest buildRequest(ServletContext servletContext) {
        return builder.buildRequest(servletContext);
    }

    public AppRequestBuilder auth(TestUser user) {
        builder.header("Authorization", user.getToken());
        return this;
    }

    public AppRequestBuilder content(Object object) {
        if (object instanceof String str) {
            builder.content(str);
        } else {
            builder.contentType(MediaType.APPLICATION_JSON);
            builder.content(json(object));
        }
        return this;
    }

    public AppRequestBuilder emptyJsonContent() {
        builder.contentType(MediaType.APPLICATION_JSON);
        builder.content("{}");
        return this;
    }

    public AppRequestBuilder pagination(int rowPerPages) {
        builder.param("rowsPerPage", String.valueOf(rowPerPages));
        return this;
    }

    public AppRequestBuilder param(String name, String... values) {
        builder.param(name, values);
        return this;
    }

    public AppRequestBuilder oneRowPerPage() {
        return pagination(1);
    }
}
