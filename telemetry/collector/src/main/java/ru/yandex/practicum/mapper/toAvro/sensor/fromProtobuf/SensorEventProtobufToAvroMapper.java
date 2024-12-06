package ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.helper.TimestampProtobufMapper;
import ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf.payload.*;
import ru.yandex.practicum.mapper.toModel.factory.SensorEventModelFactory;

@Mapper(componentModel = "spring", uses = {
        MotionSensorProtobufMapper.class,
        TemperatureSensorProtobufMapper.class,
        LightSensorProtobufMapper.class,
        ClimateSensorProtobufMapper.class,
        SwitchSensorProtobufMapper.class,
        TimestampProtobufMapper.class,
        SensorEventModelFactory.class
})
public interface SensorEventProtobufToAvroMapper {

    /**
     * Преобразует SensorEventProto в SensorEventAvro.
     * <p>
     * Использует вложенные мапперы для обработки различных типов событий (например, {@code MotionSensorProtobufMapper}).
     *
     * @param event Protobuf-событие.
     * @return Avro-событие.
     */
    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "timestampToInstant")
    @Mapping(source = ".", target = "payload", qualifiedByName = "mapSensorPayloadToAvro")
    SensorEventAvro toAvro(SensorEventProto event);

    /**
     * Маппинг payload для Avro.
     * <p>
     * Этот метод возвращает вложенные события (payload) Protobuf для последующего преобразования
     * в соответствующие Avro-объекты с использованием вложенных мапперов.
     */
    @Named("mapSensorPayloadToAvro")
    default Object mapSensorPayloadToAvro(SensorEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT -> proto.getMotionSensorEvent();
            case TEMPERATURE_SENSOR_EVENT -> proto.getTemperatureSensorEvent();
            case LIGHT_SENSOR_EVENT -> proto.getLightSensorEvent();
            case CLIMATE_SENSOR_EVENT -> proto.getClimateSensorEvent();
            case SWITCH_SENSOR_EVENT -> proto.getSwitchSensorEvent();
            default -> null;
        };
    }
}
