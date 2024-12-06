package ru.yandex.practicum.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotService;

/**
 * Kafka Consumer для обработки снапшотов.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotKafkaConsumer {

    private final SnapshotService snapshotService;

    @KafkaListener(topics = "${spring.kafka.topics.telemetry.snapshots}", groupId = "${spring.kafka.consumer.group-id}")
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Получен снапшот: {}", snapshot);
        snapshotService.processSnapshot(snapshot);
    }
}
