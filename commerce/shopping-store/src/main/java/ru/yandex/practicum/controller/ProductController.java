package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.common.model.PageOfProductDto;
import ru.yandex.practicum.common.model.ProductCategory;
import ru.yandex.practicum.common.model.ProductDto;
import ru.yandex.practicum.common.model.QuantityState;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.mapper.PageOfProductDtoMapper;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.service.ProductService;
import ru.yandex.practicum.store.api.ApiApi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с товарами в магазине.
 * Реализует интерфейс ApiApi (генерируется из OpenAPI).
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ProductController implements ApiApi {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final PageOfProductDtoMapper pageOfProductDtoMapper;

    /**
     * PUT /api/v1/shopping-store
     * (createNewProduct)
     */
    @Override
    @PutMapping
    public ResponseEntity<ProductDto> createNewProduct(
            @Valid @RequestBody ProductDto productDto
    ) {
        log.info("Создание товара: {}", productDto);
        Product newProduct = productMapper.toEntity(productDto);
        Product saved = productService.createProduct(newProduct);
        // Возвращаем 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toDto(saved));
    }

    /**
     * GET /api/v1/shopping-store/{productId}
     * (getProduct)
     */
    @Override
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable("productId") UUID productId
    ) {
        log.info("Получение товара по ID: {}", productId);
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    /**
     * GET /api/v1/shopping-store
     * (getProducts)
     */
    @Override
    @GetMapping
    public ResponseEntity<PageOfProductDto> getProducts(
            @NotNull @Valid @RequestParam(value = "category", required = true) ProductCategory category,
            @Valid @RequestParam(value = "page", required = false) Integer page,
            @Valid @RequestParam(value = "size", required = false) Integer size,
            @Valid @RequestParam(value = "sort", required = false) String sort
    ) {
        log.info("GET products: category={}, page={}, size={}, sort={}", category, page, size, sort);

        int pageSafe = (page != null) ? page : 0;
        int sizeSafe = (size != null) ? size : 10;

        // 1) Сформировать Pageable
        Sort sortSpec = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            sortSpec = Sort.by(Sort.Direction.ASC, sort);
        }
        Pageable pageable = PageRequest.of(pageSafe, sizeSafe, sortSpec);

        // 2) Вызываем сервис, получаем Page<Product>
        Page<Product> pageResult = productService.getProducts(category, pageable);

        // 3) Превращаем Page<Product> -> PageOfProductDto
        PageOfProductDto dto = pageOfProductDtoMapper.toPageDto(pageResult);

        return ResponseEntity.ok(dto);
    }

    /**
     * POST /api/v1/shopping-store/removeProductFromStore
     * (removeProductFromStore)
     */
    @Override
    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Boolean> removeProductFromStore(
            @Valid @RequestBody UUID body,
            @Valid @RequestParam(value = "productId", required = false) UUID productId
    ) {
        UUID idToRemove = (productId != null) ? productId : body;
        log.info("Удаление товара с ID: {}", idToRemove);
        boolean success = productService.deleteProduct(idToRemove);
        return ResponseEntity.ok(success);
    }

    /**
     * POST /api/v1/shopping-store/quantityState
     * (setProductQuantityState)
     */
    @Override
    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> setProductQuantityState(
            @NotNull @Valid @RequestParam(value = "productId", required = true) UUID productId,
            @NotNull @Valid @RequestParam(value = "quantityState", required = true) QuantityState quantityState
    ) {
        log.info("Изменение статуса товара: productId={}, quantityState={}", productId, quantityState);
        boolean updated = productService.updateQuantityState(productId, quantityState);
        return ResponseEntity.ok(updated);
    }

    /**
     * POST /api/v1/shopping-store
     * (updateProduct)
     */
    @Override
    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(
            @Valid @RequestBody ProductDto productDto
    ) {
        log.info("Обновление товара: {}", productDto);

        if (productDto.getProductId() == null || !productDto.getProductId().isPresent()) {
            throw new IllegalArgumentException("Product ID is required for update");
        }

        UUID id = productDto.getProductId().get();
        Product toUpdate = productMapper.toEntity(productDto);
        Product updated = productService.updateProduct(id, toUpdate);

        return ResponseEntity.ok(productMapper.toDto(updated));
    }
}
