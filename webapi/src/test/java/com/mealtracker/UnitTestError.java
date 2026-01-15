package com.mealtracker;

import com.mealtracker.payloads.Error;

public enum UnitTestError {
    RESOURCE_NOT_FOUND(40401, "The given %s does not exist"),
    ;

    private final int code;
    private final String template;

    UnitTestError(int code, String template) {
        this.code = code;
        this.template = template;
    }

    public int getCode() {
        return code;
    }

    public Error error(String... params) {
        return Error.of(code, String.format(template, (Object[]) params));
    }
}
