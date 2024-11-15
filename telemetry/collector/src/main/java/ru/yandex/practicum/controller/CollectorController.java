package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.EventProcessingService;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class CollectorController {
    private final EventProcessingService eventProcessingService;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.debug("Received sensor event: {}", event);
        eventProcessingService.processSensorEvent(event);
        log.info("Sensor event processed: id={}, eventType={}", event.getId(), event.getType());
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.debug("Received hub event: {}", event);
        eventProcessingService.processHubEvent(event);
        log.info("Hub event processed: hubId={}, eventType={}", event.getHubId(), event.getType());
    }
}