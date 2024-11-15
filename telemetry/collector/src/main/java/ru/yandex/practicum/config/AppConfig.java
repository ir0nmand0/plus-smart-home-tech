package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
@Getter
@RequiredArgsConstructor
public class AppConfig {
    private String sensors;
    private String hubs;
}
