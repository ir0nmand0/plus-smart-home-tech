package ru.yandex.practicum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Составной ключ для связи корзины и товара
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartProductId implements Serializable {
    @Column
    private UUID cartId;

    @Column
    private UUID productId;
}