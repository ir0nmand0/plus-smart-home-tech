package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.client.HubRouterClient;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.mapper.ScenarioActionMapper;
import ru.yandex.practicum.model.entity.Scenario;
import ru.yandex.practicum.model.entity.ScenarioAction;
import ru.yandex.practicum.model.entity.ScenarioCondition;
import ru.yandex.practicum.producer.DeviceActionKafkaProducer;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.SnapshotService;

import java.util.List;
import java.util.Objects;

/**
 * Реализация сервиса для обработки снапшотов состояния хаба.
 * <p>
 * Основные задачи:
 * 1. Проверка выполнения условий сценариев на основе данных сенсоров.
 * 2. Отправка действий, связанных с выполненными сценариями, в Kafka.
 * 3. Отправка действий на выполнение через gRPC-клиент.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    // Репозиторий для получения сценариев, связанных с хабом.
    private final ScenarioRepository scenarioRepository;

    // Маппер для преобразования сущностей ScenarioAction в DeviceActionProto.
    private final ScenarioActionMapper scenarioActionMapper;

    // Kafka-продюсер для отправки действий в топик.
    private final DeviceActionKafkaProducer deviceActionKafkaProducer;

    // gRPC-клиент для отправки команд на выполнение устройствам.
    private final HubRouterClient hubRouterClient;

    // Название Kafka-топика для отправки действий.
    @Value("${spring.kafka.topics.telemetry.sensors}")
    private String sensorTopic;

    /**
     * Обрабатывает снапшот состояния устройств, проверяя выполнение сценариев.
     *
     * @param snapshot снапшот состояния сенсоров.
     */
    @Transactional
    @Override
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Обработка снапшота для хаба: {}", snapshot.getHubId());

        // Получаем все сценарии, связанные с указанным hubId.
        List<Scenario> scenarios = scenarioRepository.findAllByHubId(snapshot.getHubId());
        if (scenarios.isEmpty()) {
            log.info("Нет сценариев для хаба: {}", snapshot.getHubId());
            return;
        }

        // Проверяем выполнение каждого сценария.
        scenarios.forEach(scenario -> {
            if (areConditionsMet(snapshot, scenario.getConditions())) {
                log.info("Сценарий '{}' выполнен для хаба {}", scenario.getName(), scenario.getHubId());
                // Если условия выполнены, обрабатываем все действия сценария.
                scenario.getActions().forEach(this::processAction);
            }
        });
    }

    /**
     * Проверяет выполнение всех условий сценария.
     *
     * @param snapshot   снапшот состояния сенсоров.
     * @param conditions список условий сценария.
     * @return true, если все условия выполнены; false — в противном случае.
     */
    private boolean areConditionsMet(SensorsSnapshotAvro snapshot, List<ScenarioCondition> conditions) {
        // Проверяем выполнение всех условий сценария.
        return conditions.stream().allMatch(condition -> isConditionMet(snapshot, condition));
    }

    /**
     * Проверяет выполнение одного условия.
     *
     * @param snapshot  снапшот состояния сенсоров.
     * @param condition условие сценария.
     * @return true, если условие выполнено; false — в противном случае.
     */
    private boolean isConditionMet(SensorsSnapshotAvro snapshot, ScenarioCondition condition) {
        // Ищем данные сенсора, которые соответствуют ID сенсора в условии.
        return snapshot.getSensorsState().entrySet().stream()
                .filter(entry -> entry.getKey().equals(condition.getSensor().getId()))
                .anyMatch(entry -> {
                    // Извлекаем значение сенсора.
                    Integer sensorValue = extractSensorValue(entry.getValue().getData());
                    if (sensorValue == null) {
                        return false;
                    }
                    // Проверяем выполнение условия на основе операции.
                    return switch (condition.getCondition().getOperation()) {
                        case EQUALS -> sensorValue.equals(condition.getCondition().getIntValue());
                        case GREATER_THAN -> sensorValue > condition.getCondition().getIntValue();
                        case LOWER_THAN -> sensorValue < condition.getCondition().getIntValue();
                        default -> {
                            log.warn("Неизвестная операция: {}", condition.getCondition().getOperation());
                            yield false;
                        }
                    };
                });
    }

    /**
     * Извлекает численное значение из данных сенсора.
     *
     * @param sensorData данные сенсора.
     * @return значение сенсора или null, если тип данных неизвестен.
     */
    private Integer extractSensorValue(Object sensorData) {
        switch (sensorData) {
            case TemperatureSensorAvro data -> {
                return data.getTemperatureC();
            }
            case LightSensorAvro data -> {
                return data.getLuminosity();
            }
            case MotionSensorAvro data -> {
                return data.getVoltage();
            }
            case null, default -> {
            }
        }
        log.warn("Неизвестный тип сенсора: {}", Objects.requireNonNull(sensorData).getClass().getSimpleName());
        return null;
    }

    /**
     * Обрабатывает одно действие сценария.
     *
     * @param action действие сценария.
     */
    private void processAction(ScenarioAction action) {
        log.info("Обработка действия для сенсора '{}': {}", action.getSensor().getId(), action);

        // Отправляем действие в Kafka.
        try {
            var actionProto = scenarioActionMapper.toProto(action);
            deviceActionKafkaProducer.sendDeviceAction(sensorTopic, actionProto);
        } catch (Exception e) {
            log.error("Ошибка отправки действия в Kafka: {}", e.getMessage(), e);
        }

        // Отправляем действие через gRPC.
        try {
            hubRouterClient.sendDeviceAction(
                    action.getSensor().getId(),
                    action.getAction().getType(),
                    action.getAction().getValue()
            );
        } catch (Exception e) {
            log.error("Ошибка выполнения действия через gRPC: {}", e.getMessage(), e);
        }
    }
}
