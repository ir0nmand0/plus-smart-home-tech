package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.common.model.ProductCategory;
import ru.yandex.practicum.common.model.QuantityState;
import ru.yandex.practicum.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<Product> getProducts(ProductCategory category, Pageable pageable);
    Product getProductById(UUID id);
    Product createProduct(Product product);
    Product updateProduct(UUID id, Product product);
    boolean deleteProduct(UUID id);
    boolean updateQuantityState(UUID productId, QuantityState state);
}