package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Сущность для хранения информации о забронированных для заказа товарах
 */
@Entity
@Table(name = "order_bookings", schema = "warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @ElementCollection
    @CollectionTable(
            name = "order_booking_items",
            schema = "warehouse",
            joinColumns = @JoinColumn(name = "booking_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    @Builder.Default
    private Map<UUID, Long> bookedItems = new HashMap<>();

    @Column(nullable = false)
    private Double totalWeight;

    @Column(nullable = false)
    private Double totalVolume;

    @Column(nullable = false)
    private Boolean hasFragileItems;
}