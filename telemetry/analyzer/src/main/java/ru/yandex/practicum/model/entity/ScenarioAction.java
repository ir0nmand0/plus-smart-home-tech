package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Связь между сценарием, датчиком и действием.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "scenario_actions")
public class ScenarioAction {

    /**
     * Составной ключ связи.
     */
    @EmbeddedId
    private ScenarioActionId id;

    /**
     * Сценарий, к которому относится действие.
     */
    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    /**
     * Датчик, связанный с этим действием.
     */
    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    /**
     * Действие, выполняемое сценарием.
     */
    @ManyToOne
    @MapsId("actionId")
    @JoinColumn(name = "action_id")
    private Action action;
}
