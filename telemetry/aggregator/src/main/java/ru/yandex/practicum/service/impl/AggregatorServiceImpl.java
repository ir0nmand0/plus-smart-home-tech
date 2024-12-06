package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.AggregatorService;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис агрегации событий сенсоров в снапшоты.
 * Получает события с топика telemetry.sensors.v1 (через KafkaListener или Poll Loop),
 * обновляет состояние соответствующего хаба и при изменениях записывает снапшот в telemetry.snapshots.v1.
 */
@Service
@RequiredArgsConstructor
public class AggregatorServiceImpl implements AggregatorService {

    private final AppConfig appConfig;
    private final KafkaTemplate<String, SensorsSnapshotAvro> kafkaTemplate;

    // Хранилище снапшотов по hubId.
    // Ключ: hubId, Значение: текущее состояние снапшота этого хаба.
    private final Map<String, SensorsSnapshotAvro> snapshots = new ConcurrentHashMap<>();

    @Override
    public void aggregateEvent(SensorEventAvro event) {
        // Пытаемся обновить снапшот на основе нового события.
        Optional<SensorsSnapshotAvro> updatedSnapshot = updateSnapshot(event);

        // Если снапшот был обновлён, отправляем его в Kafka.
        updatedSnapshot.ifPresent(snapshot -> {
            kafkaTemplate.send(appConfig.getSnapshots(), snapshot.getHubId(), snapshot);
            // flush гарантирует, что сообщение будет отправлено немедленно,
            // что важно для тестов.
            kafkaTemplate.flush();
        });
    }

    @Override
    public Optional<SensorsSnapshotAvro> updateSnapshot(SensorEventAvro event) {
        String hubId = event.getHubId();

        // Получаем или создаём новый снапшот для данного хаба.
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, this::createNewSnapshot);

        // Синхронизируемся на конкретном снапшоте, чтобы избежать состояния гонки между потоками.
        synchronized (snapshot) {
            // Получаем текущее состояние сенсора из снапшота.
            SensorStateAvro currentState = snapshot.getSensorsState().get(event.getId());

            // Проверяем, нужно ли обновлять состояние сенсора.
            // Обновляем если:
            // 1) Сенсор ещё не известен снапшоту.
            // 2) Полученное событие свежее (timestamp больше) уже имеющегося.
            // 3) Данные сенсора отличаются от текущих.
            boolean needUpdate = false;

            if (currentState == null) {
                // Новый сенсор — нужно обновить
                needUpdate = true;
            } else {
                // Есть текущее состояние, сравним временную метку.
                if (event.getTimestamp().isAfter(currentState.getTimestamp())) {
                    // Получим полезную нагрузку
                    Object newData = event.getPayload();
                    Object oldData = currentState.getData();
                    // Обновим, если данные отличаются или событие свежее.
                    if (!newData.equals(oldData)) {
                        needUpdate = true;
                    }
                }
            }

            if (!needUpdate) {
                // Если обновлять не нужно, возвращаем пустой Optional.
                return Optional.empty();
            }

            // Создаём новое состояние сенсора
            SensorStateAvro newState = new SensorStateAvro(event.getTimestamp(), event.getPayload());

            // Обновляем состояние снапшота
            snapshot.getSensorsState().put(event.getId(), newState);
            // Время снапшота теперь соответствует последнему обновлению
            snapshot.setTimestamp(event.getTimestamp());

            return Optional.of(snapshot);
        }
    }

    /**
     * Создаёт новый пустой снапшот для хаба.
     *
     * @param hubId идентификатор хаба
     * @return новый снапшот с пустым состоянием сенсоров
     */
    private SensorsSnapshotAvro createNewSnapshot(String hubId) {
        return new SensorsSnapshotAvro(hubId, Instant.now(), new ConcurrentHashMap<>());
    }
}
