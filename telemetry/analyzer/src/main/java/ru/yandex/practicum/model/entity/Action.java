package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Представляет действие, выполняемое при активации сценария.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "actions")
public class Action {

    /**
     * Уникальный идентификатор действия.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Тип действия (например, "включить", "выключить").
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType type;

    /**
     * Значение, связанное с действием (например, уровень яркости).
     */
    @Column(nullable = false)
    private Integer value;
}
