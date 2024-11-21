package ru.yandex.practicum.model.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

import static ru.yandex.practicum.model.sensor.SensorEventTypeString.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = LIGHT_SENSOR_EVENT),
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = CLIMATE_SENSOR_EVENT),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = MOTION_SENSOR_EVENT),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = TEMPERATURE_SENSOR_EVENT),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = SWITCH_SENSOR_EVENT)
})
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class SensorEvent {
    @NotBlank
    private String id;

    @NotBlank
    private String hubId;

    @NotNull
    @Builder.Default
    private Instant timestamp = Instant.now();

    public abstract void accept(EventProcessorVisitor visitor);
}
