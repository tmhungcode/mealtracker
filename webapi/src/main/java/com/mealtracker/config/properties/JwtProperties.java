package com.mealtracker.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("app.jwt")
public class JwtProperties {

    private String secretKey;
    private int expirationInMs;
}
