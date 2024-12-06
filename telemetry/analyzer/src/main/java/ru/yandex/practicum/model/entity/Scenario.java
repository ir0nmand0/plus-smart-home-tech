package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Представляет сценарий умного дома.
 * <p>
 * - Уникальное имя в рамках одного хаба.
 * - Хранит условия и действия, связанные с этим сценарием.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "scenarios",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"hub_id", "name"}) // Гарантирует уникальность имени сценария в рамках хаба
        }
)
public class Scenario {

    /**
     * Уникальный идентификатор сценария.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Идентификатор хаба, к которому относится сценарий.
     */
    @Column(nullable = false)
    private String hubId;

    /**
     * Уникальное имя сценария внутри хаба.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Список условий, связанных с этим сценарием.
     * <p>
     * - mappedBy указывает, что связь управляется полем "scenario" в классе `ScenarioCondition`.
     * - CascadeType.ALL: все операции (persist, merge, remove) над `Scenario` распространяются на `conditions`.
     * - orphanRemoval: удаляет условия, которые больше не связаны с этим сценарием.
     */
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ScenarioCondition> conditions;

    /**
     * Список действий, связанных с этим сценарием.
     */
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ScenarioAction> actions;
}
