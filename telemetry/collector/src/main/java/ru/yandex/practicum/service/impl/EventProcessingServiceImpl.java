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

    /*
     Используем Visitor, так как this всегда содержит настоящий экземпляр текущего класса (с его состоянием и методами),
     в отличие от абстрактного родителя, т.е. задействуем полиморфизм и избавляемся от приведения типов, а так же это
     быстрее рефлексии, как пример:
     Method method = event.getClass().getMethod("publishSensorEvent")
     method.invoke(event);
     более того, можно создать мапу Map<EventType, Method>, но это усложнит код и потребуется отлавливать дополнительные
     исключения NoSuchMethodException и т.д.
     */
    @Override
    public void publishKafka(SensorEvent event) {
        event.accept(this); // Используем Visitor для событий датчиков
    }

    @Override
    public void publishKafka(HubEvent event) {
        event.accept(this); // Используем Visitor для событий хаба
    }

    @Override
    public void visit(ClimateSensorEvent event) {
        log.debug("Processing ClimateSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), climateSensorEventMapper.toAvro(event));
    }

    @Override
    public void visit(LightSensorEvent event) {
        log.debug("Processing LightSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), lightSensorEventMapper.toAvro(event));
    }

    @Override
    public void visit(MotionSensorEvent event) {
        log.debug("Processing MotionSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), motionSensorEventMapper.toAvro(event));
    }

    @Override
    public void visit(SwitchSensorEvent event) {
        log.debug("Processing SwitchSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), switchSensorEventMapper.toAvro(event));
    }

    @Override
    public void visit(TemperatureSensorEvent event) {
        log.debug("Processing TemperatureSensorEvent: {}", event);
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), temperatureSensorEventMapper.toAvro(event));
    }

    @Override
    public void visit(DeviceAddedEvent event) {
        log.debug("Processing DeviceAddedEvent: {}", event);
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), deviceAddedEventMapper.toAvro(event));
    }

    @Override
    public void visit(DeviceRemovedEvent event) {
        log.debug("Processing DeviceRemovedEvent: {}", event);
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), deviceRemovedEventMapper.toAvro(event));
    }

    @Override
    public void visit(ScenarioAddedEvent event) {
        log.debug("Processing ScenarioAddedEvent: {}", event);
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), scenarioAddedEventMapper.toAvro(event));
    }

    @Override
    public void visit(ScenarioRemovedEvent event) {
        log.debug("Processing ScenarioRemovedEvent: {}", event);
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), scenarioRemovedEventMapper.toAvro(event));
    }
}
