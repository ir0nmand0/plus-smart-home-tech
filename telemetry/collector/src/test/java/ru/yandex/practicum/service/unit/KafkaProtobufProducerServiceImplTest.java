package ru.yandex.practicum.service.unit;

import org.apache.avro.specific.SpecificRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.HubEventProtobufToAvroMapper;
import ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf.SensorEventProtobufToAvroMapper;
import ru.yandex.practicum.service.impl.KafkaProtobufProducerServiceImpl;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProtobufProducerServiceImplTest {

    @Mock
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @Mock
    private AppConfig appConfig;

    @Mock
    private HubEventProtobufToAvroMapper hubEventMapper;

    @Mock
    private SensorEventProtobufToAvroMapper sensorEventMapper;

    private KafkaProtobufProducerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new KafkaProtobufProducerServiceImpl(kafkaTemplate, appConfig, hubEventMapper, sensorEventMapper);

        // Используем lenient() для моков, которые могут не понадобиться в некоторых тестах
        lenient().when(appConfig.getSensors()).thenReturn("telemetry.sensors.v1");
        lenient().when(appConfig.getHubs()).thenReturn("telemetry.hubs.v1");
    }

    @Test
    void testPublishSensorEvent() {
        SensorEventProto sensorEventProto = SensorEventProto.newBuilder().setId("sensor1").build();
        SensorEventAvro sensorEventAvro = mock(SensorEventAvro.class);

        when(sensorEventMapper.toAvro(sensorEventProto)).thenReturn(sensorEventAvro);

        service.publishSensorEvent(sensorEventProto);

        verify(kafkaTemplate).send(appConfig.getSensors(), "sensor1", sensorEventAvro);
        verify(sensorEventMapper).toAvro(sensorEventProto);
    }

    @Test
    void testPublishHubEvent() {
        HubEventProto hubEventProto = HubEventProto.newBuilder().setHubId("hub1").build();
        HubEventAvro hubEventAvro = mock(HubEventAvro.class);

        when(hubEventMapper.toAvro(hubEventProto)).thenReturn(hubEventAvro);

        service.publishHubEvent(hubEventProto);

        verify(kafkaTemplate).send(appConfig.getHubs(), "hub1", hubEventAvro);
        verify(hubEventMapper).toAvro(hubEventProto);
    }
}

