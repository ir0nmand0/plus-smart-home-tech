package ru.yandex.practicum.service;

/**
 * Универсальный интерфейс для публикации событий в Kafka.
 *
 * @param <S> Тип события от датчиков.
 * @param <H> Тип события от хабов.
 */
public interface KafkaProducerService<S, H> {

    /**
     * Публикует событие от датчика в Kafka.
     *
     * @param event Событие датчика.
     */
    void publishSensorEvent(S event);

    /**
     * Публикует событие от хаба в Kafka.
     *
     * @param event Событие хаба.
     */
    void publishHubEvent(H event);
}
