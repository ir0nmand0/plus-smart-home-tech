package ru.yandex.practicum.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;

/**
 * Производитель Kafka для отправки действий устройств.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceActionKafkaProducer {

    private final KafkaTemplate<String, DeviceActionProto> kafkaTemplate;

    public void sendDeviceAction(String topic, DeviceActionProto action) {
        log.info("Отправка действия устройства в топик {}: {}", topic, action);
        kafkaTemplate.send(topic, action);
    }
}
