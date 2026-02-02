package com.mealtracker.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.mealtracker.domains"})
@EnableJpaRepositories(basePackages = {"com.mealtracker.repositories"})
@EnableTransactionManagement
public class JpaConfig {
}

