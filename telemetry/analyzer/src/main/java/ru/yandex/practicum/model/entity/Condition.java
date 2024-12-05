package ru.yandex.practicum.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Представляет условие активации сценария.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "conditions")
public class Condition {

    /**
     * Уникальный идентификатор условия.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Тип условия (например, "температура", "влажность").
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionType type;

    /**
     * Операция сравнения (например, ">", "<", "=").
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionOperation operation;

    @Enumerated(EnumType.STRING)
    private ValueType valueType; // ENUM для типа значения (BOOL или INT)

    private Boolean boolValue; // Булево значение

    private Integer intValue; // Числовое значение
}
