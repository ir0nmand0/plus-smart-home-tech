package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank
    @EqualsAndHashCode.Include
    private String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    public void accept(HubEventProcessorVisitor visitor) {
        visitor.process(this);
    }
}