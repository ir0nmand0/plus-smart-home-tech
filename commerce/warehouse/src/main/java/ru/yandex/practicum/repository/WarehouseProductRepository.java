package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.WarehouseProduct;
import java.util.UUID;
import java.util.Optional;

/**
 * Репозиторий для работы с товарами на складе
 */
public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, UUID> {

    /**
     * Поиск товара по его ID в каталоге
     */
    Optional<WarehouseProduct> findByProductId(UUID productId);

    /**
     * Проверка существования товара по его ID в каталоге
     */
    boolean existsByProductId(UUID productId);
}