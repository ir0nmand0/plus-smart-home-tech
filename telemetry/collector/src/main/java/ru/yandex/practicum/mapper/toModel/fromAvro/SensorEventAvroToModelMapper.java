package ru.yandex.practicum.mapper.toModel.fromAvro;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.toModel.factory.SensorEventModelFactory;
import ru.yandex.practicum.model.sensor.*;

/**
 * Маппер для преобразования SensorEventAvro в SensorEvent.
 * <p>
 * Для `toModel` используется {@link SensorEventModelFactory}, так как невозможно
 * создать наследников абстрактного класса `SensorEvent` без явного указания фабрики.
 */
@Mapper(componentModel = "spring", uses = SensorEventModelFactory.class)
public interface SensorEventAvroToModelMapper {

    /**
     * Преобразует SensorEventAvro в SensorEvent.
     *
     * @param avro Avro-событие.
     * @return Модельное событие.
     */
    default SensorEvent toModel(SensorEventAvro avro) {
        return switch (avro.getPayload()) {
            case ClimateSensorAvro payload -> createClimateSensorEvent(payload, avro);
            case LightSensorAvro payload -> createLightSensorEvent(payload, avro);
            case MotionSensorAvro payload -> createMotionSensorEvent(payload, avro);
            case SwitchSensorAvro payload -> createSwitchSensorEvent(payload, avro);
            case TemperatureSensorAvro payload -> createTemperatureSensorEvent(payload, avro);
            case null, default ->
                    throw new IllegalArgumentException("Unsupported payload type: " + avro.getPayload().getClass());
        };
    }

    private ClimateSensorEvent createClimateSensorEvent(ClimateSensorAvro payload, SensorEventAvro avro) {
        return ClimateSensorEvent.builder()
                .id(avro.getId())
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .temperatureC(payload.getTemperatureC())
                .humidity(payload.getHumidity())
                .co2Level(payload.getCo2Level())
                .build();
    }

    private LightSensorEvent createLightSensorEvent(LightSensorAvro payload, SensorEventAvro avro) {
        return LightSensorEvent.builder()
                .id(avro.getId())
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .linkQuality(payload.getLinkQuality())
                .luminosity(payload.getLuminosity())
                .build();
    }

    private MotionSensorEvent createMotionSensorEvent(MotionSensorAvro payload, SensorEventAvro avro) {
        return MotionSensorEvent.builder()
                .id(avro.getId())
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .linkQuality(payload.getLinkQuality())
                .motion(payload.getMotion())
                .voltage(payload.getVoltage())
                .build();
    }

    private SwitchSensorEvent createSwitchSensorEvent(SwitchSensorAvro payload, SensorEventAvro avro) {
        return SwitchSensorEvent.builder()
                .id(avro.getId())
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .state(payload.getState())
                .build();
    }

    private TemperatureSensorEvent createTemperatureSensorEvent(TemperatureSensorAvro payload, SensorEventAvro avro) {
        return TemperatureSensorEvent.builder()
                .id(avro.getId())
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .temperatureC(payload.getTemperatureC())
                .temperatureF(payload.getTemperatureF())
                .build();
    }
}
