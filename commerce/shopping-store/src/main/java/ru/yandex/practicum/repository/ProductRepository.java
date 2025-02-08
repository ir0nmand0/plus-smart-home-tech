package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.common.model.ProductCategoryDto;
import ru.yandex.practicum.common.model.ProductStateDto;
import ru.yandex.practicum.entity.Product;

import java.util.UUID;

/**
 * Репозиторий для работы с товарами.
 * Вместо List<Product> возвращаем Page<Product>,
 * чтобы получить реальную пагинацию и сортировку из Spring Data JPA.
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategoryAndProductState(
            ProductCategoryDto category,
            ProductStateDto state,
            Pageable pageable
    );
}
