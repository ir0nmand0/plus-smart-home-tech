package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.common.model.ProductCategory;
import ru.yandex.practicum.common.model.ProductDto;
import ru.yandex.practicum.common.model.SetProductQuantityStateRequest;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.service.ProductService;
import ru.yandex.practicum.store.api.ApiApi;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ProductController implements ApiApi {
    private final ProductService productService;
    private final ProductMapper productMapper;

    @Override
    public ResponseEntity<ProductDto> createNewProduct(@Valid ProductDto productDto) {
        log.debug("REST запрос на создание товара: {}", productDto);
        Product newProduct = productMapper.toEntity(productDto);
        Product savedProduct = productService.createProduct(newProduct);
        return ResponseEntity.ok(productMapper.toDto(savedProduct));
    }

    @Override
    public ResponseEntity<ProductDto> getProduct(UUID productId) {
        log.debug("REST запрос на получение товара по ID: {}", productId);
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @Override
    public ResponseEntity<List<ProductDto>> getProducts(ProductCategory category, Pageable pageable) {
        log.debug("REST запрос на получение товаров по категории: {} с пагинацией: {}", category, pageable);
        List<Product> products = productService.getProducts(category, pageable);
        return ResponseEntity.ok(products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<Boolean> removeProductFromStore(UUID body, UUID productId) {
        UUID idToRemove = productId != null ? productId : body;
        log.debug("REST запрос на удаление товара с ID: {}", idToRemove);
        return ResponseEntity.ok(productService.deleteProduct(idToRemove));
    }

    @Override
    public ResponseEntity<Boolean> setProductQuantityState(@Valid SetProductQuantityStateRequest request) {
        log.debug("REST запрос на изменение состояния количества товара: {}", request);
        return ResponseEntity.ok(productService.updateQuantityState(
                request.getProductId(),
                request.getQuantityState()));
    }

    @Override
    public ResponseEntity<ProductDto> updateProduct(@Valid ProductDto productDto) {
        log.debug("REST запрос на обновление товара: {}", productDto);
        Product updatedProduct = productService.updateProduct(
                productDto.getProductId().get(),
                productMapper.toEntity(productDto));
        return ResponseEntity.ok(productMapper.toDto(updatedProduct));
    }
}