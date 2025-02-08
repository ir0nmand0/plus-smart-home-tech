package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.OrderItem;
import ru.yandex.practicum.entity.OrderItemId;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с товарами в заказе
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    // Поиск всех товаров в заказе
    List<OrderItem> findByIdOrderId(UUID orderId);

    // Поиск товара в заказе по id товара
    List<OrderItem> findByIdProductId(UUID productId);

    // Удаление всех товаров заказа
    void deleteByIdOrderId(UUID orderId);
}