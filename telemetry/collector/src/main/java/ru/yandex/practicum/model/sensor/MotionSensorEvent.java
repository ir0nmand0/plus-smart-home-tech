package ru.yandex.practicum.model.sensor;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static ru.yandex.practicum.model.sensor.SensorEventTypeString.MOTION_SENSOR_EVENT;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonTypeName(MOTION_SENSOR_EVENT)
public class MotionSensorEvent extends SensorEvent {
    private int linkQuality;
    private boolean motion;
    private int voltage;

    @Override
    public void accept(EventProcessorVisitor visitor) {
        visitor.visit(this);
    }
}
