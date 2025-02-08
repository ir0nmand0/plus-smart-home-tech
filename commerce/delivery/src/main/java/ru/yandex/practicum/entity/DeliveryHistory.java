package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Сущность истории доставки для хранения в базе данных
 */
@Entity
@Table(name = "delivery_history", schema = "delivery_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeliveryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID historyId;

    @Column(nullable = false)
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    private DeliveryState previousState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryState newState;

    @Column(nullable = false)
    private OffsetDateTime changedAt;
}
