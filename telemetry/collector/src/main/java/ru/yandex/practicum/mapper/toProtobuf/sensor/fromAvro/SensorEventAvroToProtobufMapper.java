package ru.yandex.practicum.mapper.toProtobuf.sensor.fromAvro;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.helper.TimestampProtobufMapper;

/**
 * Маппер для преобразования объектов Avro SensorEventAvro в Protobuf SensorEventProto.
 * <p>
 * Использует маппер для временных меток {@link TimestampProtobufMapper}.
 */
@Mapper(componentModel = "spring", uses = TimestampProtobufMapper.class)
public interface SensorEventAvroToProtobufMapper {

    /**
     * Преобразует объект Avro SensorEventAvro в Protobuf SensorEventProto.
     *
     * @param avro Avro-событие SensorEventAvro.
     * @return Protobuf-событие SensorEventProto.
     */
    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "instantToTimestamp")
    default SensorEventProto toProtobuf(SensorEventAvro avro) {
        SensorEventProto.Builder builder = SensorEventProto.newBuilder()
                .setId(avro.getId())
                .setHubId(avro.getHubId());

        switch (avro.getPayload()) {
            case ClimateSensorAvro climatePayload ->
                    builder.setClimateSensorEvent(toClimateSensorProto(climatePayload));
            case LightSensorAvro lightPayload -> builder.setLightSensorEvent(toLightSensorProto(lightPayload));
            case MotionSensorAvro motionPayload -> builder.setMotionSensorEvent(toMotionSensorProto(motionPayload));
            case SwitchSensorAvro switchPayload -> builder.setSwitchSensorEvent(toSwitchSensorProto(switchPayload));
            case TemperatureSensorAvro temperaturePayload ->
                    builder.setTemperatureSensorEvent(toTemperatureSensorProto(temperaturePayload));
            case null, default -> throw new IllegalArgumentException("Unsupported SensorEventAvro payload type: "
                    + avro.getPayload().getClass());
        }

        return builder.build();
    }

    /**
     * Преобразует ClimateSensorAvro в ClimateSensorProto.
     */
    private ClimateSensorProto toClimateSensorProto(ClimateSensorAvro avro) {
        return ClimateSensorProto.newBuilder()
                .setTemperatureC(avro.getTemperatureC())
                .setHumidity(avro.getHumidity())
                .setCo2Level(avro.getCo2Level())
                .build();
    }

    /**
     * Преобразует LightSensorAvro в LightSensorProto.
     */
    private LightSensorProto toLightSensorProto(LightSensorAvro avro) {
        return LightSensorProto.newBuilder()
                .setLinkQuality(avro.getLinkQuality())
                .setLuminosity(avro.getLuminosity())
                .build();
    }

    /**
     * Преобразует MotionSensorAvro в MotionSensorProto.
     */
    private MotionSensorProto toMotionSensorProto(MotionSensorAvro avro) {
        return MotionSensorProto.newBuilder()
                .setLinkQuality(avro.getLinkQuality())
                .setMotion(avro.getMotion())
                .setVoltage(avro.getVoltage())
                .build();
    }

    /**
     * Преобразует SwitchSensorAvro в SwitchSensorProto.
     */
    private SwitchSensorProto toSwitchSensorProto(SwitchSensorAvro avro) {
        return SwitchSensorProto.newBuilder()
                .setState(avro.getState())
                .build();
    }

    /**
     * Преобразует TemperatureSensorAvro в TemperatureSensorProto.
     */
    private TemperatureSensorProto toTemperatureSensorProto(TemperatureSensorAvro avro) {
        return TemperatureSensorProto.newBuilder()
                .setTemperatureC(avro.getTemperatureC())
                .setTemperatureF(avro.getTemperatureF())
                .build();
    }
}
