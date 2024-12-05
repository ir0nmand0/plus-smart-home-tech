package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Связь между сценарием, датчиком и условием.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "scenario_conditions")
public class ScenarioCondition {

    /**
     * Составной ключ связи.
     */
    @EmbeddedId
    private ScenarioConditionId id;

    /**
     * Сценарий, к которому относится условие.
     */
    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    /**
     * Датчик, связанный с этим условием.
     */
    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    /**
     * Условие активации сценария.
     */
    @ManyToOne
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id")
    private Condition condition;
}
