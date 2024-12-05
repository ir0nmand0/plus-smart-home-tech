package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.service.impl.AggregatorServiceImpl;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit-тест для проверки корректности работы AggregatorService.
 * Основное внимание уделяется:
 * 1. Обновлению снапшотов (SensorsSnapshotAvro) на основе входных событий.
 * 2. Публикации снапшотов в указанный Kafka-топик.
 */
@ExtendWith(MockitoExtension.class)
public class AggregationServiceUnitTest {

    // Мокаем KafkaTemplate, чтобы проверить взаимодействие с ним
    @Mock
    private KafkaTemplate<String, SensorsSnapshotAvro> kafkaTemplate;

    // Мокаем конфигурацию, чтобы возвращать название топика для снапшотов
    @Mock
    private AppConfig appConfig;

    // Инжектируем моки в тестируемый сервис
    @InjectMocks
    private AggregatorServiceImpl aggregatorService;

    /**
     * Настройка теста перед каждым запуском:
     * - Указываем, что метод appConfig.getSnapshots() возвращает название топика "telemetry.snapshots.v1".
     */
    @BeforeEach
    void setup() {
        when(appConfig.getSnapshots()).thenReturn("telemetry.snapshots.v1");
    }

    /**
     * Тест проверяет, что:
     * 1. Событие сенсора обрабатывается и обновляет состояние снапшота.
     * 2. Обновлённый снапшот публикуется в указанный Kafka-топик.
     */
    @Test
    void testAggregationAndSnapshotPublishing() {
        // Шаг 1: Создаём тестовое событие SensorEventAvro
        SensorEventAvro sensorEvent = createTestSensorEvent();

        // Шаг 2: Вызываем метод агрегации, который обновляет снапшот и публикует его
        aggregatorService.aggregateEvent(sensorEvent);

        // Шаг 3: Захватываем аргументы вызова kafkaTemplate.send
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class); // Захват названия топика
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);   // Захват ключа сообщения
        // Захват значения сообщения
        ArgumentCaptor<SensorsSnapshotAvro> valueCaptor = ArgumentCaptor.forClass(SensorsSnapshotAvro.class);

        // Проверяем, что метод send был вызван один раз
        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), keyCaptor.capture(),
                valueCaptor.capture());

        // Шаг 4: Проверяем переданные аргументы

        // Проверка названия топика
        assertThat(topicCaptor.getValue()).isEqualTo("telemetry.snapshots.v1");

        // Проверка ключа сообщения
        assertThat(keyCaptor.getValue()).isEqualTo("test-hub");

        // Проверка содержимого переданного снапшота
        SensorsSnapshotAvro capturedValue = valueCaptor.getValue();
        assertThat(capturedValue).isNotNull();
        assertThat(capturedValue.getHubId()).isEqualTo("test-hub");
        assertThat(capturedValue.getSensorsState()).containsKey("sensor-1");
        assertThat(capturedValue.getSensorsState().get("sensor-1").getData()).isEqualTo("test-payload");
    }

    /**
     * Создаёт тестовое событие SensorEventAvro.
     * Используется для проверки обновления снапшотов.
     *
     * @return Тестовое событие SensorEventAvro.
     */
    private SensorEventAvro createTestSensorEvent() {
        return SensorEventAvro.newBuilder()
                .setHubId("test-hub")            // Идентификатор хаба
                .setId("sensor-1")               // Идентификатор сенсора
                .setTimestamp(Instant.now())     // Текущая метка времени
                .setPayload("test-payload")      // Полезная нагрузка
                .build();
    }
}
