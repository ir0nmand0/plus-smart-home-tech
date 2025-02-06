package ru.yandex.practicum.entity;

/**
 * Перечисление статусов заказа
 */
public enum OrderState {
    NEW,                // Новый
    ON_PAYMENT,         // Ожидает оплаты
    ON_DELIVERY,        // Ожидает доставки
    DONE,              // Выполнен
    DELIVERED,         // Доставлен
    ASSEMBLED,         // Собран
    PAID,             // Оплачен
    COMPLETED,         // Завершён
    DELIVERY_FAILED,   // Неудачная доставка
    ASSEMBLY_FAILED,   // Неудачная сборка
    PAYMENT_FAILED,    // Неудачная оплата
    PRODUCT_RETURNED,  // Возврат товаров
    CANCELED           // Отменён
}
