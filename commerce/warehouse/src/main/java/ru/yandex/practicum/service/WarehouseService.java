package ru.yandex.practicum.service;

import ru.yandex.practicum.common.model.DimensionDto;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.AddressDto;
import ru.yandex.practicum.exception.*;
import java.util.UUID;

/**
 * Сервис для работы со складом
 */
public interface WarehouseService {

    /**
     * Добавление товара на склад
     * @param productId ID товара
     * @param quantity количество для добавления
     * @throws ProductNotInWarehouseException если товар не зарегистрирован на складе
     */
    void addProduct(UUID productId, Long quantity);

    /**
     * Регистрация нового товара на складе
     * @param productId ID товара
     * @param fragile признак хрупкости
     * @param weight вес товара
     * @param dimension размеры товара
     * @throws ProductAlreadyExistsInWarehouseException если товар уже зарегистрирован
     */
    void registerProduct(UUID productId, Boolean fragile, Double weight, DimensionDto dimension);

    /**
     * Проверка наличия товаров для корзины
     * @param cart корзина с товарами
     * @return информация о возможности бронирования
     * @throws InsufficientProductQuantityException если товаров недостаточно
     */
    BookedProductsDto checkAvailability(ShoppingCartDto cart);

    /**
     * Получение случайного адреса склада
     * @return адрес склада
     */
    AddressDto getAddress();
}