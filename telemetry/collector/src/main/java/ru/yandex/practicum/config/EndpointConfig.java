package ru.yandex.practicum.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@Configuration
@ConfigurationProperties(prefix = "endpoints")
@Data
public class EndpointConfig {
    private String root;     // Корневой путь
    private String sensors;  // Вложенный путь для сенсоров
    private String hubs;     // Вложенный путь для хабов

    private String endpointSensor;
    private String endpointHub;

    public String getEndpointSensor() {
        if (ObjectUtils.isEmpty(endpointSensor)) {
            setEndpointSensor(root.concat(sensors));
        }

        return endpointSensor;
    }

    public String getEndpointHub() {
        if (ObjectUtils.isEmpty(endpointHub)) {
            setEndpointHub(root.concat(hubs));
        }

        return endpointHub;
    }
}
