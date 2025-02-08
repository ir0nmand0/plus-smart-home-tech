package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Сущность платежа для хранения в базе данных
 */
@Entity
@Table(name = "payments", schema = "payment_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID paymentId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private Double totalPayment;

    @Column(nullable = false)
    private Double deliveryTotal;

    @Column(nullable = false)
    private Double productTotal;

    @Column(nullable = false)
    private Double taxTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    // Hibernate преобразует paymentMethod -> payment_method
    private String paymentMethod;

    // Поле для JSON; имя столбца будет автоматически payment_details
    @JdbcTypeCode(SqlTypes.JSON)
    private String paymentDetails;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
