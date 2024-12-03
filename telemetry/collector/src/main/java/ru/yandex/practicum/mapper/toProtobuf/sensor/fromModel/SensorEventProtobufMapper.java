package ru.yandex.practicum.mapper.toProtobuf.sensor.fromModel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.mapper.helper.TimestampProtobufMapper;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.*;

/**
 * Маппер для преобразования объектов модели SensorEvent в Protobuf (SensorEventProto).
 * <p>
 * Использует стандартные методы для преобразования полей, а также методы для работы
 * с вложенными объектами (payload).
 */
@Mapper(componentModel = "spring", uses = TimestampProtobufMapper.class)
public interface SensorEventProtobufMapper {

    /**
     * Преобразует объект модели SensorEvent в Protobuf (SensorEventProto).
     *
     * @param event Модельное событие SensorEvent.
     * @return Protobuf-событие SensorEventProto.
     */
    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "instantToTimestamp")
    default SensorEventProto toProtobuf(SensorEvent event) {
        SensorEventProto.Builder builder = SensorEventProto.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId());

        switch (event) {
            case MotionSensorEvent motionEvent -> {
                builder.setMotionSensorEvent(toMotionSensorProto(motionEvent));
            }
            case TemperatureSensorEvent temperatureEvent -> {
                builder.setTemperatureSensorEvent(toTemperatureSensorProto(temperatureEvent));
            }
            case LightSensorEvent lightEvent -> {
                builder.setLightSensorEvent(toLightSensorProto(lightEvent));
            }
            case ClimateSensorEvent climateEvent -> {
                builder.setClimateSensorEvent(toClimateSensorProto(climateEvent));
            }
            case SwitchSensorEvent switchEvent -> {
                builder.setSwitchSensorEvent(toSwitchSensorProto(switchEvent));
            }
            default -> {
                throw new IllegalArgumentException("Unsupported SensorEvent type: " + event.getClass().getSimpleName());
            }
        }

        return builder.build();
    }

    /**
     * Преобразует MotionSensorEvent в MotionSensorEventProto.
     */
    private MotionSensorProto toMotionSensorProto(MotionSensorEvent event) {
        return MotionSensorProto.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.isMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    /**
     * Преобразует TemperatureSensorEvent в TemperatureSensorEventProto.
     */
    private TemperatureSensorProto toTemperatureSensorProto(TemperatureSensorEvent event) {
        return TemperatureSensorProto.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }

    /**
     * Преобразует LightSensorEvent в LightSensorEventProto.
     */
    private LightSensorProto toLightSensorProto(LightSensorEvent event) {
        return LightSensorProto.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }

    /**
     * Преобразует ClimateSensorEvent в ClimateSensorEventProto.
     */
    private ClimateSensorProto toClimateSensorProto(ClimateSensorEvent event) {
        return ClimateSensorProto.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .build();
    }

    /**
     * Преобразует SwitchSensorEvent в SwitchSensorEventProto.
     */
    private SwitchSensorProto toSwitchSensorProto(SwitchSensorEvent event) {
        return SwitchSensorProto.newBuilder()
                .setState(event.isState())
                .build();
    }
}
