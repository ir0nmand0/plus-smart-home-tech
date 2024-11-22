package ru.yandex.practicum.model.hub;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.sensor.DeviceType;

import static ru.yandex.practicum.model.hub.HubEventTypeString.DEVICE_ADDED;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonTypeName(DEVICE_ADDED)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    @EqualsAndHashCode.Include
    private String id;
    @NotNull
    private DeviceType type;

    @Override
    public void accept(HubEventProcessorVisitor visitor) {
        visitor.visit(this);
    }
}
