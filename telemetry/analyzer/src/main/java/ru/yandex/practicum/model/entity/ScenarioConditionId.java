package ru.yandex.practicum.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Составной ключ для `ScenarioCondition`.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class ScenarioConditionId implements Serializable {
    private Long scenarioId;
    private String sensorId;
    private Long conditionId;
}
