package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.avro.mapper.hub.DeviceAddedEventMapper;
import ru.yandex.practicum.avro.mapper.hub.DeviceRemovedEventMapper;
import ru.yandex.practicum.avro.mapper.hub.ScenarioAddedEventMapper;
import ru.yandex.practicum.avro.mapper.hub.ScenarioRemovedEventMapper;
import ru.yandex.practicum.avro.mapper.sensor.*;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.*;
import ru.yandex.practicum.service.EventProcessingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessingServiceImpl implements EventProcessingService {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    private final AppConfig appConfig;
    private final ClimateSensorEventMapper climateSensorEventMapper;
    private final LightSensorEventMapper lightSensorEventMapper;
    private final MotionSensorEventMapper motionSensorEventMapper;
    private final SwitchSensorEventMapper switchSensorEventMapper;
    private final TemperatureSensorEventMapper temperatureSensorEventMapper;
    private final DeviceAddedEventMapper deviceAddedEventMapper;
    private final DeviceRemovedEventMapper deviceRemovedEventMapper;
    private final ScenarioAddedEventMapper scenarioAddedEventMapper;
    private final ScenarioRemovedEventMapper scenarioRemovedEventMapper;

    @Override
    public void processSensorEvent(SensorEvent event) {
        event.accept(this); // Используем Visitor для событий датчиков
    }

    @Override
    public void processHubEvent(HubEvent event) {
        event.accept(this); // Используем Visitor для событий хаба
    }

    @Override
    public void process(ClimateSensorEvent event) {
        log.debug("Processing ClimateSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), climateSensorEventMapper.toAvro(event));
    }

    @Override
    public void process(LightSensorEvent event) {
        log.debug("Processing LightSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), lightSensorEventMapper.toAvro(event));
    }

    @Override
    public void process(MotionSensorEvent event) {
        log.debug("Processing MotionSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), motionSensorEventMapper.toAvro(event));
    }

    @Override
    public void process(SwitchSensorEvent event) {
        log.debug("Processing SwitchSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), switchSensorEventMapper.toAvro(event));
    }

    @Override
    public void process(TemperatureSensorEvent event) {
        log.debug("Processing TemperatureSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), temperatureSensorEventMapper.toAvro(event));
    }

    @Override
    public void process(DeviceAddedEvent event) {
        log.debug("Processing DeviceAddedEvent: {}", event);
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), deviceAddedEventMapper.toAvro(event));
    }

    @Override
    public void process(DeviceRemovedEvent event) {
        log.debug("Processing DeviceRemovedEvent: {}", event);
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), deviceRemovedEventMapper.toAvro(event));
    }

    @Override
    public void process(ScenarioAddedEvent event) {
        log.debug("Processing ScenarioAddedEvent: {}", event);
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), scenarioAddedEventMapper.toAvro(event));
    }

    @Override
    public void process(ScenarioRemovedEvent event) {
        log.debug("Processing ScenarioRemovedEvent: {}", event);
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), scenarioRemovedEventMapper.toAvro(event));
    }
}
