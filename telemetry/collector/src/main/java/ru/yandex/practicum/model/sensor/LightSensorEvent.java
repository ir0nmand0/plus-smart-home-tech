package ru.yandex.practicum.model.sensor;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static ru.yandex.practicum.model.sensor.SensorEventTypeString.LIGHT_SENSOR_EVENT;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonTypeName(LIGHT_SENSOR_EVENT)
public class LightSensorEvent extends SensorEvent {
    private int linkQuality;
    private int luminosity;

    @Override
    public void accept(EventProcessorVisitor visitor) {
        visitor.visit(this);
    }
}
