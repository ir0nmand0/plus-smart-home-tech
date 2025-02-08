package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.api.WarehouseApi;
import java.util.Map;

/**
 * Контроллер для управления складом.
 * Обеспечивает REST API для всех операций со складом:
 * - регистрация новых товаров
 * - добавление товаров
 * - проверка наличия
 * - сборка заказов
 * - передача в доставку
 * - обработка возвратов
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v${api.warehouse-version}/warehouse")
public class WarehouseController implements WarehouseApi {

    private final WarehouseService warehouseService;

    /**
     * Добавление нового товара на склад
     * PUT /api/v1/warehouse
     *
     * @param newProductInWarehouseRequest информация о новом товаре
     */
    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void newProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequestDto newProductInWarehouseRequest
    ) {
        log.info("REST запрос на регистрацию нового товара на складе: {}", newProductInWarehouseRequest);
        warehouseService.registerProduct(
                newProductInWarehouseRequest.getProductId(),
                newProductInWarehouseRequest.getFragile(),
                newProductInWarehouseRequest.getWeight(),
                newProductInWarehouseRequest.getDimension()
        );
    }

    /**
     * Добавление количества существующего товара
     * POST /api/v1/warehouse/add
     *
     * @param addProductToWarehouseRequest информация о добавляемом количестве
     */
    @Override
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public void addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequestDto addProductToWarehouseRequest
    ) {
        log.info("REST запрос на добавление товара на склад: {}", addProductToWarehouseRequest);
        warehouseService.addProduct(
                addProductToWarehouseRequest.getProductId(),
                addProductToWarehouseRequest.getQuantity()
        );
    }

    /**
     * Проверка наличия товаров для корзины
     * POST /api/v1/warehouse/check
     *
     * @param shoppingCartDto информация о товарах в корзине
     * @return информация о возможности бронирования
     */
    @Override
    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(
            @Valid @RequestBody ShoppingCartDto shoppingCartDto
    ) {
        log.info("REST запрос на проверку наличия товаров для корзины: {}", shoppingCartDto);
        return warehouseService.checkAvailability(shoppingCartDto);
    }

    /**
     * Получение адреса склада
     * GET /api/v1/warehouse/address
     *
     * @return адрес склада
     */
    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("REST запрос на получение адреса склада");
        return warehouseService.getAddress();
    }

    /**
     * Сборка товаров для заказа
     * POST /api/v1/warehouse/assembly
     *
     * @param assemblyProductsForOrderRequest информация о заказе и товарах
     * @return информация о собранных товарах
     */
    @Override
    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(
            @Valid @RequestBody AssemblyProductsForOrderRequestDto assemblyProductsForOrderRequest
    ) {
        log.info("REST запрос на сборку товаров для заказа: {}", assemblyProductsForOrderRequest);
        return warehouseService.assemblyProducts(assemblyProductsForOrderRequest);
    }

    /**
     * Передача товаров в доставку
     * POST /api/v1/warehouse/shipped
     *
     * @param shippedToDeliveryRequest информация о заказе и доставке
     */
    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(
            @Valid @RequestBody ShippedToDeliveryRequestDto shippedToDeliveryRequest
    ) {
        log.info("REST запрос на передачу товаров в доставку: {}", shippedToDeliveryRequest);
        warehouseService.shipToDelivery(shippedToDeliveryRequest);
    }

    /**
     * Обработка возврата товаров
     * POST /api/v1/warehouse/return
     *
     * @param requestBody карта товаров и их количества для возврата
     */
    @Override
    @PostMapping("/return")
    public void acceptReturn(
            @Valid @RequestBody Map<String, Long> requestBody
    ) {
        log.info("REST запрос на возврат товаров на склад: {}", requestBody);
        warehouseService.returnProducts(requestBody);
    }
}