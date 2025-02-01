package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Сущность товара на складе
 */
@Entity
@Table(name = "products", schema = "warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WarehouseProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID productId;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Boolean fragile;

    @Column(nullable = false)
    private Double weight;

    @Embedded
    private Dimension dimension;
}