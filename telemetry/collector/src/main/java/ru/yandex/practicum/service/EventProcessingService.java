package ru.yandex.practicum.service;

import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventProcessorVisitor;
import ru.yandex.practicum.model.sensor.EventProcessorVisitor;
import ru.yandex.practicum.model.sensor.SensorEvent;

public interface EventProcessingService extends HubEventProcessorVisitor, EventProcessorVisitor {
    void processSensorEvent(SensorEvent event);

    void processHubEvent(HubEvent event);
}
