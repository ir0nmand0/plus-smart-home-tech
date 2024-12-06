package ru.yandex.practicum.service;


import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

/**
 * Интерфейс для обработки снапшотов.
 */
public interface SnapshotService {
    void processSnapshot(SensorsSnapshotAvro snapshot);
}
