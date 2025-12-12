package com.hoangluongtran0309.personal_blog.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.hoangluongtran0309.personal_blog.db.InMemoryUniqueIdGenerator;
import com.hoangluongtran0309.personal_blog.db.UniqueIdGenerator;

@TestConfiguration
public class TestConfig {

    @Bean
    UniqueIdGenerator uniqueIdGenerator() {
        return new InMemoryUniqueIdGenerator();
    }
}
