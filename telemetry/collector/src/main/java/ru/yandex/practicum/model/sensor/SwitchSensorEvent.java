package ru.yandex.practicum.model.sensor;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static ru.yandex.practicum.model.sensor.SensorEventTypeString.SWITCH_SENSOR_EVENT;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonTypeName(SWITCH_SENSOR_EVENT)
public class SwitchSensorEvent extends SensorEvent {
    private boolean state;

    @Override
    public void accept(EventProcessorVisitor visitor) {
        visitor.visit(this);
    }

}
