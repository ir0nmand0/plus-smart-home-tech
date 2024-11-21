package ru.yandex.practicum.model.hub;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

import static ru.yandex.practicum.model.hub.HubEventTypeString.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = DEVICE_ADDED),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = DEVICE_REMOVED),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = SCENARIO_ADDED),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = SCENARIO_REMOVED)
})
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class HubEvent {
    @NotBlank
    private String hubId;

    @NotNull
    @Builder.Default
    private Instant timestamp = Instant.now();

    public abstract void accept(HubEventProcessorVisitor visitor);
}
