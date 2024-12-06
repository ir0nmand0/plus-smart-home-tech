package ru.yandex.practicum.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Представляет датчик умного дома.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "sensors")
public class Sensor {

    /**
     * Уникальный идентификатор датчика.
     */
    @Id
    @EqualsAndHashCode.Include
    private String id;

    /**
     * Идентификатор хаба, к которому относится датчик.
     */
    @Column(nullable = false)
    private String hubId;
}
