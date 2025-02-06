package ru.yandex.practicum.entity;

/**
 * Перечисление статусов доставки
 */
public enum DeliveryState {
    CREATED,        // Создана
    IN_PROGRESS,    // В процессе
    DELIVERED,      // Доставлена
    FAILED,         // Не удалась
    CANCELLED       // Отменена
}
