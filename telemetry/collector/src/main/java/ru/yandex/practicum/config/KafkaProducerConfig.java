package ru.yandex.practicum.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
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

@EnableKafka
@Configuration
public class KafkaProducerConfig {
    private final String bootstrapServers;
    private final String schemaRegistryUrl;

    public KafkaProducerConfig(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.properties.schema.registry.url}") String schemaRegistryUrl) {
        this.bootstrapServers = bootstrapServers;
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    @Bean
    public ProducerFactory<String, SpecificRecord> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);

        // Оптимальные настройки надёжности
        configProps.put(ACKS_CONFIG, "all"); // Гарантирует, что сообщение будет записано всеми репликами
        configProps.put(RETRIES_CONFIG, 3); // Количество повторных попыток отправки сообщения в случае ошибки
        configProps.put(LINGER_MS_CONFIG, 5); // Добавляет небольшую задержку перед отправкой для повышения производительности
        configProps.put(BATCH_SIZE_CONFIG, 16384); // Размер пакета для отправки данных
        configProps.put(COMPRESSION_TYPE_CONFIG, "lz4"); // Использование компрессии для уменьшения объема данных
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, SpecificRecord> kafkaTemplate(ProducerFactory<String, SpecificRecord> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
