package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.common.model.DimensionDto;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.AddressDto;
import ru.yandex.practicum.entity.WarehouseProduct;
import ru.yandex.practicum.exception.*;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.service.WarehouseService;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Реализация сервиса для работы со складом.
 * Обеспечивает основную бизнес-логику:
 * - добавление количества товаров
 * - регистрация новых товаров
 * - проверка наличия для корзины
 * - получение адреса склада
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {
    // Предопределенные адреса склада по ТЗ
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    private final WarehouseProductRepository warehouseProductRepository;
    private final WarehouseProductMapper warehouseProductMapper;

    /**
     * Добавляет количество существующего товара на склад
     */
    @Override
    @Transactional
    public void addProduct(UUID productId, Long quantity) {
        log.debug("Добавление {} единиц товара с ID {} на склад", quantity, productId);
        WarehouseProduct product = getProductById(productId);
        product.setQuantity(product.getQuantity() + quantity);
        warehouseProductRepository.save(product);
    }

    /**
     * Регистрирует новый товар на складе с начальным количеством 0
     */
    @Override
    @Transactional
    public void registerProduct(UUID productId, Boolean fragile, Double weight, DimensionDto dimensionDto) {
        log.debug("Регистрация нового товара на складе: ID {}", productId);
        checkProductNotExists(productId);

        WarehouseProduct product = WarehouseProduct.builder()
                .productId(productId)
                .quantity(0L)
                .fragile(fragile)
                .weight(weight)
                .dimension(warehouseProductMapper.toDimensionEntity(dimensionDto))
                .build();

        warehouseProductRepository.save(product);
    }

    /**
     * Проверяет наличие достаточного количества товаров для корзины.
     * Вычисляет общий вес, объем и наличие хрупких товаров.
     */
    @Override
    public BookedProductsDto checkAvailability(ShoppingCartDto cart) {
        log.debug("Проверка наличия товаров для корзины: {}", cart);

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<String, Long> entry : cart.getProducts().entrySet()) {
            WarehouseProduct product = getProductById(UUID.fromString(entry.getKey()));
            checkSufficientQuantity(product, entry.getValue());

            totalWeight += product.getWeight() * entry.getValue();
            totalVolume += product.getDimension().calculateVolume() * entry.getValue();
            hasFragile |= product.getFragile();
        }

        return new BookedProductsDto()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile);
    }

    /**
     * Возвращает случайно выбранный адрес склада
     */
    @Override
    public AddressDto getAddress() {
        log.debug("Получение адреса склада");
        return new AddressDto()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS);
    }

    /**
     * Вспомогательные методы
     */
    private WarehouseProduct getProductById(UUID productId) {
        return warehouseProductRepository.findByProductId(productId)
                .orElseThrow(ProductNotInWarehouseException::new);
    }

    private void checkProductNotExists(UUID productId) {
        if (warehouseProductRepository.existsByProductId(productId)) {
            throw new ProductAlreadyExistsInWarehouseException();
        }
    }

    private void checkSufficientQuantity(WarehouseProduct product, Long requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientProductQuantityException();
        }
    }
}