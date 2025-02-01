package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.common.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.common.model.AddressDto;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.api.ApiApi;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}${api.warehouse.path}")
public class WarehouseController implements ApiApi {

    private final WarehouseService warehouseService;

    /**
     * POST /api/v1/warehouse/add
     * (addProductToWarehouse)
     */
    @Override
    @PostMapping("${api.warehouse.add-path}")
    @ResponseStatus(HttpStatus.OK)
    public void addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest
    ) {
        log.info("REST запрос на добавление товара на склад: {}", addProductToWarehouseRequest);
        warehouseService.addProduct(
                addProductToWarehouseRequest.getProductId(),
                addProductToWarehouseRequest.getQuantity()
        );
        // По спецификации можно вернуть 200 или 201. Выберем 200 как успешное добавление.
    }

    /**
     * POST /api/v1/warehouse/check
     * (checkProductQuantityEnoughForShoppingCart)
     */
    @Override
    @PostMapping("${api.warehouse.check-path}")
    @ResponseStatus(HttpStatus.OK)
    public BookedProductsDto  checkProductQuantityEnoughForShoppingCart(
            @Valid @RequestBody ShoppingCartDto shoppingCartDto
    ) {
        log.info("REST запрос на проверку наличия товаров для корзины: {}", shoppingCartDto);
        return warehouseService.checkAvailability(shoppingCartDto);
    }

    /**
     * GET /api/v1/warehouse/address
     * (getWarehouseAddress)
     */
    @Override
    @GetMapping("${api.warehouse.address-path}")
    @ResponseStatus(HttpStatus.OK)
    public AddressDto getWarehouseAddress() {
        log.info("REST запрос на получение адреса склада");
        return warehouseService.getAddress();
    }

    /**
     * PUT /api/v1/warehouse
     * (newProductInWarehouse)
     */
    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void newProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequest newProductInWarehouseRequest
    ) {
        log.info("REST запрос на регистрацию нового товара на складе: {}", newProductInWarehouseRequest);
        warehouseService.registerProduct(
                newProductInWarehouseRequest.getProductId(),
                newProductInWarehouseRequest.getFragile(),
                newProductInWarehouseRequest.getWeight(),
                newProductInWarehouseRequest.getDimension()
        );
    }
}
