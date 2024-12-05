package ru.yandex.practicum.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.yandex.practicum.grpc.client.HubRouterClient;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.mapper.ScenarioActionMapper;
import ru.yandex.practicum.model.entity.*;
import ru.yandex.practicum.producer.DeviceActionKafkaProducer;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit-тест для класса SnapshotServiceImpl с учетом Avro и Protobuf.
 */
@ExtendWith(MockitoExtension.class)
class SnapshotServiceImplTest {

    // Mock для репозитория сценариев, который позволяет тестировать логику, связанную с JPA.
    @Mock
    private ScenarioRepository scenarioRepository;

    // Mock для маппера, который преобразует сущности ScenarioAction в Protobuf-объекты.
    @Mock
    private ScenarioActionMapper scenarioActionMapper;

    // Mock для Kafka-продюсера, используемого для отправки сообщений в Kafka.
    @Mock
    private DeviceActionKafkaProducer deviceActionKafkaProducer;

    // Mock для gRPC-клиента, используемого для отправки команд на устройства.
    @Mock
    private HubRouterClient hubRouterClient;

    // Класс, который тестируется.
    @InjectMocks
    private SnapshotServiceImpl snapshotService;

    // Captor используется для захвата аргументов, передаваемых в mock-методы.
    @Captor
    private ArgumentCaptor<DeviceActionProto> kafkaActionCaptor;

    @Captor
    private ArgumentCaptor<String> sensorIdCaptor;

    @Captor
    private ArgumentCaptor<ActionType> actionTypeCaptor;

    @Captor
    private ArgumentCaptor<Integer> valueCaptor;

    /**
     * Выполняется перед каждым тестом. Настраивает начальное состояние тестируемого объекта.
     */
    @BeforeEach
    void setup() {
        // Устанавливаем значение поля sensorTopic через ReflectionTestUtils.
        ReflectionTestUtils.setField(snapshotService, "sensorTopic", "telemetry.sensors.v1");
    }

    /**
     * Тест проверяет, что сценарии корректно обрабатываются и действия отправляются в Kafka и через gRPC.
     */
    @Test
    void processSnapshot_shouldProcessAndSendToKafkaAndGrpc() {
        // Подготовка данных
        SensorsSnapshotAvro snapshot = createTestSnapshot();

        Scenario scenario = Scenario.builder()
                .name("Test Scenario")
                .hubId("hub-123")
                .conditions(List.of(createTestCondition())) // Добавляем условие
                .actions(List.of(createTestAction())) // Добавляем действие
                .build();

        DeviceActionProto actionProto = DeviceActionProto.newBuilder()
                .setSensorId("sensor-1")
                .setType(ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto.ACTIVATE)
                .setValue(1)
                .build();

        // Настройка поведения mock-объектов
        when(scenarioRepository.findAllByHubId("hub-123")).thenReturn(List.of(scenario));
        when(scenarioActionMapper.toProto(any())).thenReturn(actionProto);

        // Выполнение тестируемого метода
        snapshotService.processSnapshot(snapshot);

        // Проверка вызова Kafka-продюсера
        verify(deviceActionKafkaProducer, times(1))
                .sendDeviceAction(eq("telemetry.sensors.v1"), kafkaActionCaptor.capture());
        DeviceActionProto capturedKafkaAction = kafkaActionCaptor.getValue();
        assertEquals("sensor-1", capturedKafkaAction.getSensorId());
        assertEquals(1, capturedKafkaAction.getValue());
        assertEquals(ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto.ACTIVATE, capturedKafkaAction.getType());

        // Проверка вызова gRPC-клиента
        verify(hubRouterClient, times(1))
                .sendDeviceAction("sensor-1", ActionType.ACTIVATE, 1);
    }

    /**
     * Создает тестовый снапшот состояния сенсоров.
     *
     * @return Тестовый объект SensorsSnapshotAvro.
     */
    private SensorsSnapshotAvro createTestSnapshot() {
        return SensorsSnapshotAvro.newBuilder()
                .setHubId("hub-123")
                .setTimestamp(Instant.now()) // Устанавливаем временную метку для снапшота
                .setSensorsState(Map.of(
                        "sensor-1", SensorStateAvro.newBuilder()
                                .setTimestamp(Instant.now()) // Временная метка состояния сенсора
                                .setData(TemperatureSensorAvro.newBuilder()
                                        .setTemperatureC(30) // Температура в градусах Цельсия
                                        .setTemperatureF(86) // Температура в градусах Фаренгейта
                                        .build())
                                .build()
                ))
                .build();
    }

    /**
     * Создает тестовое условие сценария.
     *
     * @return Тестовый объект ScenarioCondition.
     */
    private ScenarioCondition createTestCondition() {
        Condition condition = Condition.builder()
                .operation(ConditionOperation.EQUALS)
                .intValue(30) // Условие на температуру
                .build();

        return ScenarioCondition.builder()
                .sensor(Sensor.builder().id("sensor-1").build())
                .condition(condition)
                .build();
    }

    /**
     * Создает тестовое действие сценария.
     *
     * @return Тестовый объект ScenarioAction.
     */
    private ScenarioAction createTestAction() {
        Action action = Action.builder()
                .type(ActionType.ACTIVATE) // Тип действия
                .value(1) // Значение действия
                .build();

        return ScenarioAction.builder()
                .sensor(Sensor.builder().id("sensor-1").build())
                .action(action)
                .build();
    }
}
