package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.yandex.practicum.common.model.ProductCategoryDto;
import ru.yandex.practicum.common.model.ProductStateDto;
import ru.yandex.practicum.common.model.QuantityStateDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Сущность товара для хранения в базе данных
 */
@Entity
@Table(name = "products", schema = "shopping_store")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String productName;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategoryDto productCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuantityStateDto quantityState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStateDto productState;

    private Integer rating;

    private String imageSrc;
}
