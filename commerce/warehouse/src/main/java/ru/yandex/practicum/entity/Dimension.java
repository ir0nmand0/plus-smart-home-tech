package ru.yandex.practicum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Встраиваемая сущность для хранения размеров товара
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dimension {

    @Column(nullable = false)
    private Double width;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Double depth;

    /**
     * Вычисляет объем товара
     */
    public Double calculateVolume() {
        return width * height * depth;
    }
}