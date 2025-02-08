package ru.yandex.practicum.entity;

/**
 * Перечисление статусов платежа
 */
public enum PaymentStatus {
    PENDING,    // Ожидает оплаты
    SUCCESS,    // Успешно оплачен
    FAILED      // Ошибка в процессе оплаты
}
