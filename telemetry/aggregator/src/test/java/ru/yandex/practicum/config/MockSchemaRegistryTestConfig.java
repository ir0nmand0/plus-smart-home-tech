package ru.yandex.practicum.config;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Данный класс предоставляет MockSchemaRegistryClient и переопределяет фабрики Producer/Consumer для тестов.
 * Используем @TestConfiguration, чтобы она была доступна только в тестах.
 * Аннотации @Primary для того, чтобы эти бины заменили стандартные бины конфигурации, если такие есть.
 */
@TestConfiguration
public class MockSchemaRegistryTestConfig {

    /**
     * Создаём MockSchemaRegistryClient - это in-memory реализация.
     * Он будет использоваться KafkaAvroSerializer/KafkaAvroDeserializer для регистрации/доступа к схемам.
     */
    @Bean
    @Primary
    public MockSchemaRegistryClient mockSchemaRegistryClient() {
        return new MockSchemaRegistryClient();
    }

    /**
     * Переопределяем фабрику продюсера для тестов.
     * Важно указать:
     * - schema.registry.url: mock://test-schema-registry
     * - Использовать mockClient.
     *
     * "bootstrap.servers" здесь можно указать любой, так как EmbeddedKafka сам подставит реальный адрес.
     */
    @Bean
    @Primary
    public ProducerFactory<String, Object> testProducerFactory(MockSchemaRegistryClient mockClient) {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "dummy:1234"); // В процессе теста будет заменен на реальный адрес EmbeddedKafka
        props.put("key.serializer", KafkaAvroSerializer.class);
        props.put("value.serializer", KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://test-schema-registry");

        // Создаём ProducerFactory, указывая, что будем использовать mockClient для сериализации
        return new DefaultKafkaProducerFactory<>(props, null, new KafkaAvroSerializer(mockClient));
    }

    /**
     * Переопределяем фабрику консьюмера для тестов.
     * Аналогично продюсеру, используем mock schema registry.
     */
    @Bean
    @Primary
    public ConsumerFactory<String, Object> testConsumerFactory(MockSchemaRegistryClient mockClient) {
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", "dummy:1234"); // Это будет заменено EmbeddedKafka
        props.put("key.deserializer", KafkaAvroDeserializer.class);
        props.put("value.deserializer", KafkaAvroDeserializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://test-schema-registry");

        // ConsumerFactory с mockClient для десериализации Avro
        return new DefaultKafkaConsumerFactory<>(props, null, new KafkaAvroDeserializer(mockClient));
    }

    /**
     * Переопределяем KafkaTemplate, чтобы он использовал новый ProducerFactory.
     */
    @Bean
    @Primary
    public KafkaTemplate<String, Object> testKafkaTemplate(ProducerFactory<String, Object> pf) {
        return new KafkaTemplate<>(pf);
    }
}
