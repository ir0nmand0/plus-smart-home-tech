package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

/**
 * Интерфейс для обработки событий хабов.
 */
public interface HubEventService {
    void processHubEvent(HubEventProto hubEvent);
}
