package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.KafkaProducerService;

@RestController
@RequestMapping("${endpoints.root}") // Базовый путь из конфигурации
@RequiredArgsConstructor
public class CollectorRestController {

    private final KafkaProducerService<SensorEvent, HubEvent> kafkaProducerService;

    @PostMapping("${endpoints.sensors}") // Вложенный путь для сенсоров
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        kafkaProducerService.publishSensorEvent(event);
    }

    @PostMapping("${endpoints.hubs}") // Вложенный путь для хабов
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        kafkaProducerService.publishHubEvent(event);
    }
}
