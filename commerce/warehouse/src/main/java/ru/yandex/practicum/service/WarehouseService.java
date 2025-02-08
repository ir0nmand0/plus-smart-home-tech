package ru.yandex.practicum.service;

import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.exception.*;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис для работы со складом.
 * Предоставляет методы для:
 * - управления товарами (добавление, регистрация)
 * - проверки наличия
 * - сборки заказов
 * - работы с доставкой
 * - обработки возвратов
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

    /**
     * Сборка товаров для заказа.
     * Метод получает список товаров и идентификатор заказа. Выполняется повторная проверка
     * наличия заказанных товаров в нужном количестве, уменьшается их доступный остаток
     * и создаётся сущность "Забронированные для заказа товары" (OrderBooking).
     *
     * @param request запрос на сборку товаров, содержащий ID заказа и список товаров
     * @return информация о собранных товарах (вес, объем, наличие хрупких товаров)
     * @throws InsufficientProductQuantityException если товаров недостаточно
     * @throws ProductNotInWarehouseException если товар отсутствует на складе
     */
    BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequestDto request);

    /**
     * Передача товаров в доставку.
     * Метод вызывается из сервиса доставки и обновляет информацию о собранном заказе
     * в базе данных склада: добавляет идентификатор доставки в сущность OrderBooking
     * и во внутреннее хранилище собранных товаров заказа.
     *
     * @param request запрос на передачу в доставку, содержащий ID заказа и ID доставки
     * @throws IllegalStateException если бронирование для заказа не найдено
     */
    void shipToDelivery(ShippedToDeliveryRequestDto request);

    /**
     * Возврат товара на склад.
     * Метод принимает список товаров с количеством и увеличивает доступный остаток
     * для каждого товара.
     *
     * @param returns карта товаров (ключ - ID товара) и их количества для возврата
     * @throws ProductNotInWarehouseException если товар отсутствует на складе
     */
    void returnProducts(Map<String, Long> returns);
}