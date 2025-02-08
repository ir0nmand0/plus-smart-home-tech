package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.common.model.ProductCategoryDto;
import ru.yandex.practicum.common.model.QuantityStateDto;
import ru.yandex.practicum.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    // Возвращаем Page<Product>, чтобы получить всю инфу о пагинации и сортировке.
    Page<Product> getProducts(ProductCategoryDto category, Pageable pageable);
    Product getProductById(UUID id);
    Product createProduct(Product product);
    Product updateProduct(UUID id, Product product);
    boolean deleteProduct(UUID id);
    boolean updateQuantityState(UUID productId, QuantityStateDto state);
}
