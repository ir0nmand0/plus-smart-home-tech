package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.config.EndpointConfig;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.EventProcessingService;

@Slf4j
@RestController
@RequestMapping("${endpoints.root}") // Базовый путь из конфигурации
@RequiredArgsConstructor
public class CollectorController {

    private final EventProcessingService eventProcessingService;
    private final EndpointConfig endpointConfig;

    @PostMapping("${endpoints.sensors}") // Вложенный путь для сенсоров
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.debug("Получено событие сенсора: {}", event);
        eventProcessingService.publishKafka(event);
        log.info("Событие сенсора обработано: id={}, тип события={}", event.getId(), event.getClass().getSimpleName());
    }

    @PostMapping("${endpoints.hubs}") // Вложенный путь для хабов
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.debug("Получено событие хаба: {}", event);
        eventProcessingService.publishKafka(event);
        log.info("Событие хаба обработано: hubId={}, тип события={}", event.getHubId(), event.getClass().getSimpleName());
    }
}
