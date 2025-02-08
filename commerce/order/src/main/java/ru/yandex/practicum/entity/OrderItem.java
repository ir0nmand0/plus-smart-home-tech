package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность элемента заказа для хранения в базе данных
 */
@Entity
@Table(name = "order_items", schema = "order_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderItem {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private OrderItemId id;

    @ManyToOne
    @MapsId("orderId")
    // Здесь явно указываем имя столбца, чтобы получить order_id, а не fromOrder_addressId
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Integer quantity;
}
