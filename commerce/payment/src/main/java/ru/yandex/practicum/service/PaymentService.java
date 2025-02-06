package ru.yandex.practicum.service;

import ru.yandex.practicum.common.model.OrderDto;
import ru.yandex.practicum.common.model.PaymentDto;
import java.util.UUID;

/**
 * Интерфейс сервиса для работы с платежами
 */
public interface PaymentService {

    /**
     * Рассчитывает стоимость товаров в заказе
     * @param orderDto информация о заказе
     * @return стоимость товаров
     */
    Double calculateProductCost(OrderDto orderDto);

    /**
     * Рассчитывает полную стоимость заказа включая доставку и налоги
     * @param orderDto информация о заказе
     * @return полная стоимость заказа
     */
    Double calculateTotalCost(OrderDto orderDto);

    /**
     * Создает новый платеж для заказа
     * @param orderDto информация о заказе
     * @return информация о созданном платеже
     */
    PaymentDto createPayment(OrderDto orderDto);

    /**
     * Отмечает платеж как успешно выполненный
     * @param orderId идентификатор заказа
     */
    void markPaymentSuccessful(UUID orderId);

    /**
     * Отмечает платеж как неуспешный
     * @param orderId идентификатор заказа
     */
    void markPaymentFailed(UUID orderId);
}