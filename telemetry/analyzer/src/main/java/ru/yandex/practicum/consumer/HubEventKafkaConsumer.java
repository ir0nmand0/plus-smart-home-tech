package ru.yandex.practicum.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.HubEventService;

/**
 * Потребитель Kafka для обработки событий хабов.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventKafkaConsumer {

    private final HubEventService hubEventService;

    @KafkaListener(
            topics = "${spring.kafka.topics.telemetry.hubs}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onHubEventReceived(HubEventProto hubEvent) {
        log.info("Получено событие хаба: {}", hubEvent);
        hubEventService.processHubEvent(hubEvent);
    }
}
