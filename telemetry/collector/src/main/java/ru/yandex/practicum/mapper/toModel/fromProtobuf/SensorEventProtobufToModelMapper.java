package ru.yandex.practicum.mapper.toModel.fromProtobuf;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensor.*;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface SensorEventProtobufToModelMapper {


    /**
     * Преобразует SensorEventProto в SensorEvent.
     * <p>
     * Использует фабрику {@code SensorEventFactory}, так как MapStruct не поддерживает автоматическую генерацию маппинга
     * для наследников абстрактного класса {@code SensorEvent}.
     *
     * @param proto Protobuf-событие.
     * @return Модельное событие.
     */
    default SensorEvent toModel(SensorEventProto proto) {
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());

        return switch (proto.getPayloadCase()) {
            case MOTION_SENSOR_EVENT -> createMotionSensorEvent(proto, timestamp);
            case TEMPERATURE_SENSOR_EVENT -> createTemperatureSensorEvent(proto, timestamp);
            case LIGHT_SENSOR_EVENT -> createLightSensorEvent(proto, timestamp);
            case CLIMATE_SENSOR_EVENT -> createClimateSensorEvent(proto, timestamp);
            case SWITCH_SENSOR_EVENT -> createSwitchSensorEvent(proto, timestamp);
            default -> throw new IllegalArgumentException("Unsupported payload type: " + proto.getPayloadCase());
        };
    }

    /**
     * Создаёт объект MotionSensorEvent.
     */
    private MotionSensorEvent createMotionSensorEvent(SensorEventProto proto, Instant timestamp) {
        return MotionSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .linkQuality(proto.getMotionSensorEvent().getLinkQuality())
                .motion(proto.getMotionSensorEvent().getMotion())
                .voltage(proto.getMotionSensorEvent().getVoltage())
                .build();
    }

    /**
     * Создаёт объект TemperatureSensorEvent.
     */
    private TemperatureSensorEvent createTemperatureSensorEvent(SensorEventProto proto, Instant timestamp) {
        return TemperatureSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .temperatureC(proto.getTemperatureSensorEvent().getTemperatureC())
                .temperatureF(proto.getTemperatureSensorEvent().getTemperatureF())
                .build();
    }

    /**
     * Создаёт объект LightSensorEvent.
     */
    private LightSensorEvent createLightSensorEvent(SensorEventProto proto, Instant timestamp) {
        return LightSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .linkQuality(proto.getLightSensorEvent().getLinkQuality())
                .luminosity(proto.getLightSensorEvent().getLuminosity())
                .build();
    }

    /**
     * Создаёт объект ClimateSensorEvent.
     */
    private ClimateSensorEvent createClimateSensorEvent(SensorEventProto proto, Instant timestamp) {
        return ClimateSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .temperatureC(proto.getClimateSensorEvent().getTemperatureC())
                .humidity(proto.getClimateSensorEvent().getHumidity())
                .co2Level(proto.getClimateSensorEvent().getCo2Level())
                .build();
    }

    /**
     * Создаёт объект SwitchSensorEvent.
     */
    private SwitchSensorEvent createSwitchSensorEvent(SensorEventProto proto, Instant timestamp) {
        return SwitchSensorEvent.builder()
                .id(proto.getId())
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .state(proto.getSwitchSensorEvent().getState())
                .build();
    }
}
