package ru.yandex.practicum;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Главный класс сервиса Aggregator.
 * <p>
 * Этот сервис является частью системы умного дома Smart Home Technologies.
 * Основная задача:
 * - Чтение данных с сенсоров, поступающих из Kafka-топика `telemetry.sensors.v1`.
 * - Агрегация этих данных в снапшоты состояния хабов (SensorsSnapshot).
 * - Отправка снапшотов в Kafka-топик `telemetry.snapshots.v1`.
 * <p>
 * Особенности реализации:
 * 1. **Использование Avro-сериализации**:
 * - Для сериализации/десериализации Avro используется `KafkaAvroDeserializer` и `KafkaAvroSerializer`.
 * - Это позволяет работать с Avro-схемами без дополнительных самописных реализаций.
 * 2. **Отказ от Schema Registry**:
 * - Вместо автоматической регистрации схем в Schema Registry, все Avro-схемы хранятся локально и
 * используются при компиляции приложения.
 * - Это минимизирует инфраструктурные требования и упрощает развёртывание сервиса.
 * 3. **Spring Boot и KafkaListener**:
 * - Интеграция с Kafka осуществляется через аннотацию `@KafkaListener`, что упрощает обработку сообщений.
 * 4. **Управление конфигурацией**:
 * - Все настройки Kafka (например, топики, сериализаторы) вынесены в `application.yml` для удобства.
 * - Конфигурация доступна через класс `AppConfig` с поддержкой Spring Boot `@ConfigurationProperties`.
 * <p>
 * Преимущества:
 * - Отсутствие необходимости писать собственные сериализаторы/десериализаторы.
 * - Минимальная сложность инфраструктуры: можно развернуть сервис без Schema Registry.
 * - Централизованное управление конфигурацией через Spring Boot.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AggregatorApp {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(AggregatorApp.class, args);
    }
}
