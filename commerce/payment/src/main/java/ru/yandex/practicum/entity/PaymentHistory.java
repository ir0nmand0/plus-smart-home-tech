package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Сущность истории платежей для хранения в базе данных
 */
@Entity
@Table(name = "payment_history", schema = "payment_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID historyId;

    @Column(nullable = false)
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus newStatus;

    @Column(nullable = false)
    private OffsetDateTime changedAt;

    private String reason;
}
