package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.client.HubRouterClient;
import ru.yandex.practicum.kafka.telemetry.event.*;
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
 * Сервис обработки снапшотов состояния хаба.
 * При получении снапшота:
 * 1. Загружает все сценарии хаба.
 * 2. Проверяет условия каждого сценария.
 * 3. Если условия выполнены — отправляет действия в Kafka и через gRPC клиент.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioActionMapper scenarioActionMapper;
    private final DeviceActionKafkaProducer deviceActionKafkaProducer;
    private final HubRouterClient hubRouterClient;

    @Value("${spring.kafka.topics.telemetry.sensors}")
    private String sensorTopic;

    @Transactional
    @Override
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Обработка снапшота для хаба: {}", snapshot.getHubId());

        // Загружаем все сценарии для данного хаба
        List<Scenario> scenarios = scenarioRepository.findAllByHubId(snapshot.getHubId());
        if (scenarios.isEmpty()) {
            log.info("Нет сценариев для хаба: {}", snapshot.getHubId());
            return;
        }

        // Проверяем каждый сценарий
        scenarios.forEach(scenario -> {
            if (areConditionsMet(snapshot, scenario.getConditions())) {
                log.info("Сценарий '{}' для хаба '{}' выполнен", scenario.getName(), scenario.getHubId());
                // Если условия выполнены, обрабатываем действия
                scenario.getActions().forEach(this::processAction);
            }
        });
    }

    /**
     * Проверяет, выполнены ли все условия сценария.
     */
    private boolean areConditionsMet(SensorsSnapshotAvro snapshot, List<ScenarioCondition> conditions) {
        // Все условия должны быть истинны
        return conditions.stream().allMatch(condition -> isConditionMet(snapshot, condition));
    }

    /**
     * Проверяет выполнение одного условия.
     */
    private boolean isConditionMet(SensorsSnapshotAvro snapshot, ScenarioCondition condition) {
        // Ищем данные конкретного сенсора
        var entryOpt = snapshot.getSensorsState().entrySet().stream()
                .filter(entry -> entry.getKey().equals(condition.getSensor().getId()))
                .findFirst();

        if (entryOpt.isEmpty()) {
            // Сенсор не найден в снапшоте — условие невыполнено
            return false;
        }

        var sensorData = entryOpt.get().getValue().getData();
        Integer sensorValue = extractSensorValue(sensorData);
        if (sensorValue == null) {
            // Не удалось извлечь числовое значение
            return false;
        }

        var cond = condition.getCondition();
        // Проверяем операцию
        return switch (cond.getOperation()) {
            case EQUALS -> Objects.equals(sensorValue, cond.getIntValue());
            case GREATER_THAN -> sensorValue > cond.getIntValue();
            case LOWER_THAN -> sensorValue < cond.getIntValue();
            default -> false;
        };
    }

    /**
     * Извлекает числовое значение из данных сенсора.
     *
     * Поддерживаются следующие типы сенсоров:
     * - TemperatureSensorAvro: возвращает temperature_c
     * - LightSensorAvro: возвращает luminosity
     * - MotionSensorAvro: возвращает voltage
     * - ClimateSensorAvro: возвращает co2_level
     * - SwitchSensorAvro: возвращает 1, если state == true, иначе 0
     *
     * Если тип сенсора не распознан или не поддерживается, возвращает null.
     *
     * При необходимости можно добавить поддержку новых типов сенсоров, расширив
     * switch выражение новыми case-блоками.
     */
    private Integer extractSensorValue(Object sensorData) {
        if (sensorData == null) {
            log.warn("Получен null вместо данных сенсора.");
            return null;
        }

        return switch (sensorData) {
            case TemperatureSensorAvro data -> // Температурный сенсор: используем температуру в °C
                    data.getTemperatureC();
            case LightSensorAvro data -> // Сенсор освещённости: используем luminosity (единицы измерения зависят от датчика)
                    data.getLuminosity();
            case MotionSensorAvro data -> // Сенсор движения: берем напряжение. Можно добавить доп. логику при необходимости
                    data.getVoltage();
            case ClimateSensorAvro data -> // Климатический сенсор: используем уровень CO2
                    data.getCo2Level();
            case SwitchSensorAvro data -> // Переключатель: true -> 1, false -> 0
                    data.getState() ? 1 : 0;

            // Если тип данных сенсора нам не известен, выводим предупреждение.
            default -> {
                log.warn("Неизвестный или неподдерживаемый тип сенсора: {}", sensorData.getClass().getSimpleName());
                yield null;
            }
        };
    }


    /**
     * Обрабатывает действие сценария:
     * 1. Отправляет действие в Kafka.
     * 2. Отправляет действие через gRPC.
     */
    private void processAction(ScenarioAction action) {
        log.info("Обработка действия для сенсора '{}': {}", action.getSensor().getId(), action);

        // Отправляем действие в Kafka
        try {
            var actionProto = scenarioActionMapper.toProto(action);
            deviceActionKafkaProducer.sendDeviceAction(sensorTopic, actionProto);
        } catch (Exception e) {
            log.error("Ошибка отправки действия в Kafka: {}", e.getMessage(), e);
        }

        // Отправляем действие через gRPC
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
