package ru.yandex.practicum.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Kafka-продюсера через параметры из application.yml.
 */
@Getter
@Configuration
public class KafkaProperties {

    /**
     * Адреса Kafka-брокеров.
     */
    private final String bootstrapServers;

    /**
     * Сериализатор ключей.
     */
    private final String keySerializer;

    /**
     * Сериализатор значений.
     */
    private final String valueSerializer;

    /**
     * URL реестра схем.
     */
    private final String schemaRegistryUrl;

    public KafkaProperties(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
                           @Value("${spring.kafka.producer.key-serializer}") String keySerializer,
                           @Value("${spring.kafka.producer.value-serializer}") String valueSerializer,
                           @Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistryUrl) {
        this.bootstrapServers = bootstrapServers;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.schemaRegistryUrl = schemaRegistryUrl;
    }
}