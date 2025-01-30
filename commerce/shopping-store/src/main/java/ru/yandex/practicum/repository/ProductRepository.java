package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.common.model.ProductCategory;
import ru.yandex.practicum.common.model.ProductState;
import ru.yandex.practicum.entity.Product;

import java.util.UUID;

/**
 * Репозиторий для работы с товарами.
 * Вместо List<Product> возвращаем Page<Product>,
 * чтобы получить реальную пагинацию и сортировку из Spring Data JPA.
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategoryAndProductState(
            ProductCategory category,
            ProductState state,
            Pageable pageable
    );
}
