package ru.yandex.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.common.model.ProductCategory;
import ru.yandex.practicum.common.model.ProductState;
import ru.yandex.practicum.common.model.QuantityState;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с товарами
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Поиск активных товаров по категории с пагинацией
     * @param category категория товаров
     * @param state статус товара
     * @param pageable параметры пагинации
     * @return список активных товаров в заданной категории
     */
    List<Product> findByProductCategoryAndProductState(ProductCategory category, ProductState state, Pageable pageable);
}