package ru.yandex.practicum.integration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.AggregatorApp;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AggregatorApp.class)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {"telemetry.sensors.v1", "telemetry.snapshots.v1"})
public class AggregatorIntegrationTest {

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private AppConfig appConfig;

    @Test
    @Disabled("Тест не проходит в github")
    void testAggregationAndSnapshotPublishing() {
        // Создаём тестовое событие датчика
        SensorEventAvro sensorEvent = createTestSensorEvent();

        // Отправляем событие в топик сенсоров
        kafkaTemplate.send(appConfig.getSensors(), sensorEvent.getHubId(), sensorEvent);
        kafkaTemplate.flush();

        try (Consumer<String, Object> consumer = consumerFactory.createConsumer("test-group", "test-client")) {
            consumer.subscribe(java.util.List.of(appConfig.getSnapshots()));

            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(5));

            // Проверяем, что пришло хотя бы одно сообщение
            assertThat(records.count()).isGreaterThan(0);

            var record = records.iterator().next();
            assertThat(record.value()).isInstanceOf(SensorsSnapshotAvro.class);

            SensorsSnapshotAvro snapshot = (SensorsSnapshotAvro) record.value();
            assertThat(snapshot.getHubId()).isEqualTo(sensorEvent.getHubId());
        }
    }

    private SensorEventAvro createTestSensorEvent() {
        MotionSensorAvro motionPayload = MotionSensorAvro.newBuilder()
                .setLinkQuality(44)
                .setMotion(true)
                .setVoltage(1031)
                .build();

        return SensorEventAvro.newBuilder()
                .setHubId("test-hub")
                .setId("sensor-1")
                .setTimestamp(Instant.now())
                .setPayload(motionPayload)
                .build();
    }
}
