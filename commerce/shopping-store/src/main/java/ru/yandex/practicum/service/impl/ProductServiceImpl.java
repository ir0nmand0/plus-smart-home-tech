package ru.yandex.practicum.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.common.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.common.model.ProductCategory;
import ru.yandex.practicum.common.model.ProductState;
import ru.yandex.practicum.common.model.QuantityState;
import ru.yandex.practicum.common.model.SpecifiedProductAlreadyInWarehouseExceptionDto;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.exception.ProductAlreadyExistsInWarehouseException;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса для работы с товарами.
 * Обеспечивает основную бизнес-логику управления товарами и интеграцию со складом.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private static final String WAREHOUSE_ERROR = "Error registering product in warehouse";
    private static final String PRODUCT_EXISTS_ERROR = "Product already exists in warehouse";
    private static final String PRODUCT_DEACTIVATED = "Product {} deactivated due to warehouse registration failure";

    // Default values for warehouse registration
    private static final double DEFAULT_DIMENSION = 1.0;
    private static final double DEFAULT_WEIGHT = 1.0;
    private static final boolean DEFAULT_FRAGILE = false;

    private final ProductRepository productRepository;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProducts(ProductCategory category, Pageable pageable) {
        log.info("Поиск товаров в категории {}, с пагинацией и сортировкой: {}", category, pageable);
        return productRepository.findByProductCategoryAndProductState(
                category,
                ProductState.ACTIVE,
                pageable
        );
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);

        try {
            registerProductInWarehouse(savedProduct);
        } catch (FeignException.BadRequest e) {
            handleWarehouseError(savedProduct);
            throw createProductExistsException();
        } catch (Exception e) {
            handleWarehouseError(savedProduct);
            throw new RuntimeException(WAREHOUSE_ERROR, e);
        }

        return savedProduct;
    }

    private void registerProductInWarehouse(Product product) {
        NewProductInWarehouseRequest request = buildWarehouseRequest(product);
        warehouseClient.newProductInWarehouse(request);
    }

    private NewProductInWarehouseRequest buildWarehouseRequest(Product product) {
        return new NewProductInWarehouseRequest()
                .productId(product.getId())
                .weight(DEFAULT_WEIGHT)
                .dimension(buildDefaultDimension())
                .fragile(DEFAULT_FRAGILE);
    }

    private ru.yandex.practicum.common.model.DimensionDto buildDefaultDimension() {
        return new ru.yandex.practicum.common.model.DimensionDto()
                .width(DEFAULT_DIMENSION)
                .height(DEFAULT_DIMENSION)
                .depth(DEFAULT_DIMENSION);
    }

    private ProductAlreadyExistsInWarehouseException createProductExistsException() {
        final ProductAlreadyExistsInWarehouseException warehouseException = new ProductAlreadyExistsInWarehouseException(
                new ru.yandex.practicum.common.model.SpecifiedProductAlreadyInWarehouseExceptionDto()
                        .message(PRODUCT_EXISTS_ERROR)
                        .status(SpecifiedProductAlreadyInWarehouseExceptionDto.StatusEnum.BAD_REQUEST)
        );
        return warehouseException;
    }

    private void handleWarehouseError(Product product) {
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        log.error(PRODUCT_DEACTIVATED, product.getId());
    }

    @Override
    @Transactional
    public Product updateProduct(UUID id, Product updateProduct) {
        Product existingProduct = getProductById(id);
        updateProduct.setId(id);
        updateProduct.setProductState(existingProduct.getProductState());
        updateProduct.setQuantityState(existingProduct.getQuantityState());
        return productRepository.save(updateProduct);
    }

    @Override
    @Transactional
    public boolean deleteProduct(UUID id) {
        Product product = getProductById(id);
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public boolean updateQuantityState(UUID productId, QuantityState state) {
        Product product = getProductById(productId);
        product.setQuantityState(state);
        productRepository.save(product);
        return true;
    }
}