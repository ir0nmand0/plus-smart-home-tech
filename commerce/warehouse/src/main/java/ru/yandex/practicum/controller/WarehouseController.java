package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements ApiApi {

    private final WarehouseService warehouseService;

    /**
     * POST /api/v1/warehouse/add
     * (addProductToWarehouse)
     */
    @Override
    @PostMapping("/add")
    public ResponseEntity<Void> addProductToWarehouse(
            @Valid @RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest
    ) {
        log.info("REST запрос на добавление товара на склад: {}", addProductToWarehouseRequest);
        warehouseService.addProduct(
                addProductToWarehouseRequest.getProductId(),
                addProductToWarehouseRequest.getQuantity()
        );
        // По спецификации можно вернуть 200 или 201. Выберем 200 как успешное добавление.
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/v1/warehouse/check
     * (checkProductQuantityEnoughForShoppingCart)
     */
    @Override
    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(
            @Valid @RequestBody ShoppingCartDto shoppingCartDto
    ) {
        log.info("REST запрос на проверку наличия товаров для корзины: {}", shoppingCartDto);
        BookedProductsDto booked = warehouseService.checkAvailability(shoppingCartDto);
        return ResponseEntity.ok(booked);
    }

    /**
     * GET /api/v1/warehouse/address
     * (getWarehouseAddress)
     */
    @Override
    @GetMapping("/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.info("REST запрос на получение адреса склада");
        AddressDto address = warehouseService.getAddress();
        return ResponseEntity.ok(address);
    }

    /**
     * PUT /api/v1/warehouse
     * (newProductInWarehouse)
     */
    @Override
    @PutMapping
    public ResponseEntity<Void> newProductInWarehouse(
            @Valid @RequestBody NewProductInWarehouseRequest newProductInWarehouseRequest
    ) {
        log.info("REST запрос на регистрацию нового товара на складе: {}", newProductInWarehouseRequest);
        warehouseService.registerProduct(
                newProductInWarehouseRequest.getProductId(),
                newProductInWarehouseRequest.getFragile(),
                newProductInWarehouseRequest.getWeight(),
                newProductInWarehouseRequest.getDimension()
        );
        // По спецификации можно вернуть 200 или 201; выберем 201 для нового товара.
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
