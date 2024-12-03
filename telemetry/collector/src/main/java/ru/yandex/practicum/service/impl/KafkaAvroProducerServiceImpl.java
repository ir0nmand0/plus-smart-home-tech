package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.DeviceAddedEventAvroMapper;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.DeviceRemovedEventAvroMapper;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.ScenarioAddedEventAvroMapper;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.ScenarioRemovedEventAvroMapper;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.*;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.*;
import ru.yandex.practicum.service.KafkaProducerService;

@Service
@RequiredArgsConstructor
public class KafkaAvroProducerServiceImpl implements KafkaProducerService<SensorEvent, HubEvent>,
        HubEventProcessorVisitor, EventProcessorVisitor {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    private final AppConfig appConfig;
    private final ClimateSensorEventAvroMapper climateSensorEventAvroMapper;
    private final LightSensorEventAvroMapper lightSensorEventAvroMapper;
    private final MotionSensorEventAvroMapper motionSensorEventAvroMapper;
    private final SwitchSensorEventAvroMapper switchSensorEventAvroMapper;
    private final TemperatureSensorEventAvroMapper temperatureSensorEventAvroMapper;
    private final DeviceAddedEventAvroMapper deviceAddedEventAvroMapper;
    private final DeviceRemovedEventAvroMapper deviceRemovedEventAvroMapper;
    private final ScenarioAddedEventAvroMapper scenarioAddedEventAvroMapper;
    private final ScenarioRemovedEventAvroMapper scenarioRemovedEventAvroMapper;

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
    public void publishSensorEvent(SensorEvent event) {
        event.accept(this); // Используем Visitor для событий датчиков
    }

    @Override
    public void publishHubEvent(HubEvent event) {
        event.accept(this); // Используем Visitor для событий хаба
    }

    @Override
    public void visit(ClimateSensorEvent event) {
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), climateSensorEventAvroMapper.toAvro(event));
    }

    @Override
    public void visit(LightSensorEvent event) {
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), lightSensorEventAvroMapper.toAvro(event));
    }

    @Override
    public void visit(MotionSensorEvent event) {
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), motionSensorEventAvroMapper.toAvro(event));
    }

    @Override
    public void visit(SwitchSensorEvent event) {
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), switchSensorEventAvroMapper.toAvro(event));
    }

    @Override
    public void visit(TemperatureSensorEvent event) {
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), temperatureSensorEventAvroMapper.toAvro(event));
    }

    @Override
    public void visit(DeviceAddedEvent event) {
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), deviceAddedEventAvroMapper.toAvro(event));
    }

    @Override
    public void visit(DeviceRemovedEvent event) {
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), deviceRemovedEventAvroMapper.toAvro(event));
    }

    @Override
    public void visit(ScenarioAddedEvent event) {
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), scenarioAddedEventAvroMapper.toAvro(event));
    }

    @Override
    public void visit(ScenarioRemovedEvent event) {
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), scenarioRemovedEventAvroMapper.toAvro(event));
    }
}
