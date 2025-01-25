package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.common.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import ru.yandex.practicum.common.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.AddressDto;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.api.ApiApi;

/**
 * Контроллер для работы со складом
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class WarehouseController implements ApiApi {

    private final WarehouseService warehouseService;

    /**
     * Добавление количества существующего товара на склад
     *
     * @param request запрос с ID товара и количеством для добавления
     * @return пустой ответ с кодом 200 при успехе
     */
    @Override
    public ResponseEntity<Void> addProductToWarehouse(@Valid AddProductToWarehouseRequest request) {
        log.debug("REST запрос на добавление товара на склад: {}", request);
        warehouseService.addProduct(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    /**
     * Проверка наличия достаточного количества товаров для корзины
     *
     * @param cart корзина с товарами для проверки
     * @return информация о возможности бронирования
     */
    @Override
    public ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(@Valid ShoppingCartDto cart) {
        log.debug("REST запрос на проверку наличия товаров для корзины: {}", cart);
        return ResponseEntity.ok(warehouseService.checkAvailability(cart));
    }

    /**
     * Получение адреса склада для расчета доставки
     *
     * @return текущий адрес склада
     */
    @Override
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.debug("REST запрос на получение адреса склада");
        return ResponseEntity.ok(warehouseService.getAddress());
    }

    /**
     * Регистрация нового товара на складе
     *
     * @param request информация о новом товаре
     * @return пустой ответ с кодом 200 при успехе
     */
    @Override
    public ResponseEntity<Void> newProductInWarehouse(@Valid NewProductInWarehouseRequest request) {
        log.debug("REST запрос на регистрацию нового товара на складе: {}", request);
        warehouseService.registerProduct(
                request.getProductId(),
                request.getFragile(),
                request.getWeight(),
                request.getDimension());
        return ResponseEntity.ok().build();
    }
}