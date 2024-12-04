package ru.yandex.practicum.aggregator.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.aggregator.config.AppConfig;
import ru.yandex.practicum.aggregator.service.AggregatorService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация сервиса агрегации.
 */
@Service
@RequiredArgsConstructor
public class AggregatorServiceImpl implements AggregatorService {

    private final AppConfig appConfig;
    private final KafkaTemplate<String, SensorsSnapshotAvro> kafkaTemplate;

    // Мапа снапшотов для хабов (потокобезопасная)
    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    @Override
    public void aggregateEvent(SensorEventAvro event) {
        // Обновляем снапшот на основе события
        Optional<SensorsSnapshotAvro> updatedSnapshot = updateSnapshot(event);

        // Отправляем обновленный снапшот в Kafka, если он изменился
        updatedSnapshot.ifPresent(snapshot -> {
                    kafkaTemplate.send(appConfig.getSnapshots(), snapshot.getHubId(), snapshot);
                }
        );
    }

    @Override
    public Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event) {
        String hubId = event.getHubId();

        // Получаем или создаем новый снапшот для конкретного хаба
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, this::createNewSnapshot);

        // Синхронизация для предотвращения состояния гонки при обновлении снапшота
        synchronized (snapshot) {
            // Проверяем текущее состояние сенсора
            var currentState = Optional.ofNullable(snapshot.getSensorsState().get(event.getId()));
            if (currentState.map(state -> state.getTimestamp().isAfter(event.getTimestamp())).orElse(false)) {
                // Если текущее состояние более свежее, игнорируем событие
                return Optional.empty();
            }

            // Обновляем состояние сенсора
            SensorStateAvro newState = new SensorStateAvro(event.getTimestamp(), event.getPayload());
            snapshot.getSensorsState().put(event.getId(), newState);

            // Обновляем общий таймстемп снапшота
            snapshot.setTimestamp(event.getTimestamp());
        }

        return Optional.of(snapshot);
    }

    /**
     * Создает новый пустой снапшот для хаба.
     *
     * @param hubId идентификатор хаба.
     * @return новый объект SensorsSnapshotAvro.
     */
    private SensorsSnapshotAvro createNewSnapshot(String hubId) {
        return new SensorsSnapshotAvro(hubId, Instant.now(), new ConcurrentHashMap<>());
    }
}
