package ru.yandex.practicum.model.hub;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static ru.yandex.practicum.model.hub.HubEventTypeString.DEVICE_REMOVED;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonTypeName(DEVICE_REMOVED)
public class DeviceRemovedEvent extends HubEvent {
    @NotBlank
    @EqualsAndHashCode.Include
    private String id;

    @Override
    public void accept(HubEventProcessorVisitor visitor) {
        visitor.visit(this);
    }
}