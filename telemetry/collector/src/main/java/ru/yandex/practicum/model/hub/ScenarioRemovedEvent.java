package ru.yandex.practicum.model.hub;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static ru.yandex.practicum.model.hub.HubEventTypeString.SCENARIO_REMOVED;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@JsonTypeName(SCENARIO_REMOVED)
public class ScenarioRemovedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    private String name;

    @Override
    public void accept(HubEventProcessorVisitor visitor) {
        visitor.visit(this);
    }
}
