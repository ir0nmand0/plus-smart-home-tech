package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Товар в корзине покупателя
 */
@Entity
@Table(name = "cart_products", schema = "shopping_cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CartProduct {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private CartProductId id;

    @Column(nullable = false)
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    private Cart cart;
}