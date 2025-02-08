package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Сущность доставки для хранения в базе данных
 */
@Entity
@Table(name = "deliveries", schema = "delivery_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID deliveryId;

    @Column(nullable = false)
    private UUID orderId;

    // Для внешних ключей явно указываем имена столбцов,
    // чтобы получить from_address_id и to_address_id (а не fromAddress_addressId и т.п.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_address_id", nullable = false)
    private Address fromAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_address_id", nullable = false)
    private Address toAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryState deliveryState;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
