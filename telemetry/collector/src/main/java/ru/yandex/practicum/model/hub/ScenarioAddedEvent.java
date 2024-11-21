package ru.yandex.practicum.model.hub;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static ru.yandex.practicum.model.hub.HubEventTypeString.SCENARIO_ADDED;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@JsonTypeName(SCENARIO_ADDED)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank
    @Size(min = 3)
    @EqualsAndHashCode.Include
    private String name;
    @NotEmpty
    private List<ScenarioCondition> conditions;
    @NotEmpty
    private List<DeviceAction> actions;

    @Override
    public void accept(HubEventProcessorVisitor visitor) {
        visitor.visit(this);
    }
}
