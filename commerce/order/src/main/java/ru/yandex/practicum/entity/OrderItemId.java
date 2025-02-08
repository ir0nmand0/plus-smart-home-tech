package ru.yandex.practicum.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.util.UUID;

/**
 * Составной ключ для элемента заказа
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderItemId implements java.io.Serializable {
    private UUID orderId;
    private UUID productId;
}
