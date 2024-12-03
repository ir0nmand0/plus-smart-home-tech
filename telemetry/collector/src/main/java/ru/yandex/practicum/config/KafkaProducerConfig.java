package ru.yandex.practicum.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

/**
 * Конфигурация для Kafka-продюсера.
 * <p>
 * Использует параметры из {@link KafkaProperties}, настроенные через application.yml.
 */
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    /**
     * Объект с параметрами конфигурации Kafka.
     */
    private final KafkaProperties kafkaProperties;

    /**
     * Создает фабрику продюсеров для отправки сообщений с использованием Avro.
     * <p>
     * Параметры конфигурации включают настройки надежности, сериализации и реестра схем.
     *
     * @return объект {@link ProducerFactory} для работы с Kafka.
     */
    @Bean
    public ProducerFactory<String, SpecificRecord> avroProducerFactory() {
        // Конфигурация для продюсера Kafka
        Map<String, Object> configProps = new HashMap<>();

        // Настройки подключения
        configProps.put(BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        // Сериализация ключей и значений
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getKeySerializer());
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getValueSerializer());

        // URL реестра схем для Avro
        configProps.put(SCHEMA_REGISTRY_URL_CONFIG, kafkaProperties.getSchemaRegistryUrl());

        // Настройки надежности
        configProps.put(ACKS_CONFIG, "all"); // Гарантирует подтверждение всеми репликами
        configProps.put(RETRIES_CONFIG, 3);  // Повторные попытки отправки сообщения в случае ошибки

        // Настройки производительности
        configProps.put(LINGER_MS_CONFIG, 5);     // Задержка перед отправкой для повышения пропускной способности
        configProps.put(BATCH_SIZE_CONFIG, 16384); // Размер пакета сообщений для отправки
        configProps.put(COMPRESSION_TYPE_CONFIG, "lz4"); // Использование компрессии для уменьшения объема данных

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Создает шаблон KafkaTemplate для отправки сообщений с использованием Avro.
     * <p>
     * Этот бин позволяет легко отправлять сообщения в Kafka через фабрику продюсеров.
     *
     * @param producerFactory объект {@link ProducerFactory} для продюсеров.
     * @return объект {@link KafkaTemplate} для отправки сообщений.
     */
    @Bean
    public KafkaTemplate<String, SpecificRecord> avroKafkaTemplate(ProducerFactory<String, SpecificRecord> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
