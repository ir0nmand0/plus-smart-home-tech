package ru.yandex.practicum.service;

import ru.yandex.practicum.common.model.*;
import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с заказами
 */
public interface OrderService {
    /**
     * Получить заказы пользователя
     * @param username имя пользователя
     * @return список заказов пользователя
     */
    List<OrderDto> getClientOrders(String username);

    /**
     * Создать новый заказ
     * @param request запрос на создание заказа
     * @return информация о созданном заказе
     */
    OrderDto createNewOrder(CreateNewOrderRequestDto request);

    /**
     * Обработать возврат товаров
     * @param request запрос на возврат товаров
     * @return обновленная информация о заказе
     */
    OrderDto returnProducts(ProductReturnRequestDto request);

    /**
     * Обработать оплату заказа
     * @param orderId идентификатор заказа
     * @return обновленная информация о заказе
     */
    OrderDto processPayment(UUID orderId);

    /**
     * Обработать неудачную оплату
     * @param orderId идентификатор заказа
     * @return обновленная информация о заказе
     */
    OrderDto handlePaymentFailure(UUID orderId);

    /**
     * Обработать доставку заказа
     * @param orderId идентификатор заказа
     * @return обновленная информация о заказе
     */
    OrderDto processDelivery(UUID orderId);

    /**
     * Обработать неудачную доставку
     * @param orderId идентификатор заказа
     * @return обновленная информация о заказе
     */
    OrderDto handleDeliveryFailure(UUID orderId);

    /**
     * Завершить заказ
     * @param orderId идентификатор заказа
     * @return обновленная информация о заказе
     */
    OrderDto completeOrder(UUID orderId);

    /**
     * Рассчитать общую стоимость заказа
     * @param orderId идентификатор заказа
     * @return информация о заказе с рассчитанной стоимостью
     */
    OrderDto calculateTotalCost(UUID orderId);

    /**
     * Рассчитать стоимость доставки
     * @param orderId идентификатор заказа
     * @return информация о заказе с рассчитанной стоимостью доставки
     */
    OrderDto calculateDeliveryCost(UUID orderId);

    /**
     * Собрать заказ
     * @param orderId идентификатор заказа
     * @return обновленная информация о заказе
     */
    OrderDto assembleOrder(UUID orderId);

    /**
     * Обработать неудачную сборку заказа
     * @param orderId идентификатор заказа
     * @return обновленная информация о заказе
     */
    OrderDto handleAssemblyFailure(UUID orderId);
}
