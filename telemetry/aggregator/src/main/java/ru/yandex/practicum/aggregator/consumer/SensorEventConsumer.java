package ru.yandex.practicum.aggregator.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.service.AggregatorService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorEventConsumer {

    private final AggregatorService aggregatorService;

    /**
     * Метод для обработки событий от сенсоров.
     * Темы Kafka и группа потребителей задаются через application.yml.
     *
     * @param event Событие сенсора.
     */
    @KafkaListener(
            topics = "${spring.kafka.topics.telemetry.sensors}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeSensorEvent(SensorEventAvro event) {
        aggregatorService.aggregateEvent(event);
    }
}
