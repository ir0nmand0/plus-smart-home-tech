package ru.yandex.practicum.aggregator.service;


import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

/**
 * Интерфейс для сервиса обработки событий сенсоров.
 */
public interface AggregatorService {

    /**
     * Обрабатывает событие сенсора.
     *
     * @param event Событие сенсора.
     */
    void aggregateEvent(SensorEventAvro event);

    /**
     * Обновляет состояние снапшота на основе события.
     *
     * @param event Событие сенсора.
     * @return Optional с обновленным снапшотом или пустой, если обновление не требуется.
     */
    Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event);
}
