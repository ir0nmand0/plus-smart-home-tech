package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.sensor.DeviceType;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    @EqualsAndHashCode.Exclude
    private String id;
    @NotBlank
    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    public void accept(HubEventProcessorVisitor visitor) {
        visitor.process(this);
    }
}
