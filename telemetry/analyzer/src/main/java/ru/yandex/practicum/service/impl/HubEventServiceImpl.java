package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.mapper.ScenarioMapper;
import ru.yandex.practicum.mapper.SensorMapper;
import ru.yandex.practicum.model.entity.Sensor;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.HubEventService;

/**
 * Сервис для обработки событий хаба.
 * Обрабатывает добавление/удаление устройств (сенсоров) и сценариев.
 * Сохраняет или удаляет данные из БД, чтобы Analyzer мог использовать актуальную конфигурацию хаба.
 */
@Service
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorMapper sensorMapper;
    private final ScenarioMapper scenarioMapper;

    @Override
    @Transactional
    public void processHubEvent(HubEventProto hubEvent) {
        if (hubEvent.hasDeviceAdded()) {
            handleDeviceAdded(hubEvent);
        } else if (hubEvent.hasDeviceRemoved()) {
            handleDeviceRemoved(hubEvent);
        } else if (hubEvent.hasScenarioAdded()) {
            handleScenarioAdded(hubEvent);
        } else if (hubEvent.hasScenarioRemoved()) {
            handleScenarioRemoved(hubEvent);
        }
    }

    private void handleDeviceAdded(HubEventProto hubEvent) {
        Sensor sensor = sensorMapper.toEntity(hubEvent);
        // Если датчика нет в БД, добавляем его.
        if (!sensorRepository.existsById(sensor.getId())) {
            sensorRepository.save(sensor);
        }
    }

    private void handleDeviceRemoved(HubEventProto hubEvent) {
        String sensorId = hubEvent.getDeviceRemoved().getId();
        // Удаляем датчик, если есть.
        sensorRepository.deleteById(sensorId);
    }

    private void handleScenarioAdded(HubEventProto hubEvent) {
        var scenario = scenarioMapper.toEntity(hubEvent.getScenarioAdded());
        scenario.setHubId(hubEvent.getHubId());
        // Добавляем сценарий, если не существует.
        if (scenarioRepository.findByHubIdAndName(hubEvent.getHubId(), scenario.getName()).isEmpty()) {
            scenarioRepository.save(scenario);
        }
    }

    private void handleScenarioRemoved(HubEventProto hubEvent) {
        var scenarioName = hubEvent.getScenarioRemoved().getName();
        scenarioRepository.findByHubIdAndName(hubEvent.getHubId(), scenarioName)
                .ifPresent(scenarioRepository::delete);
    }
}
