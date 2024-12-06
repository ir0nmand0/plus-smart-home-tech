package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.HubEventProtobufToAvroMapper;
import ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf.SensorEventProtobufToAvroMapper;
import ru.yandex.practicum.service.KafkaProducerService;

@Service
@RequiredArgsConstructor
public class KafkaProtobufProducerServiceImpl implements KafkaProducerService<SensorEventProto, HubEventProto> {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    private final AppConfig appConfig;
    private final HubEventProtobufToAvroMapper hubEventProtobufToAvroMapper;
    private final SensorEventProtobufToAvroMapper sensorEventProtobufToAvroMapper;

    @Override
    public void publishSensorEvent(SensorEventProto event) {
        kafkaTemplate.send(appConfig.getSensors(), event.getId(), sensorEventProtobufToAvroMapper.toAvro(event));
    }

    @Override
    public void publishHubEvent(HubEventProto event) {
        kafkaTemplate.send(appConfig.getHubs(), event.getHubId(), hubEventProtobufToAvroMapper.toAvro(event));
    }
}