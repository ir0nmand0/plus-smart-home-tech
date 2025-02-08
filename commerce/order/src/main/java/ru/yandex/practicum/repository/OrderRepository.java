package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.entity.OrderState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с заказами
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Поиск заказов пользователя
    List<Order> findByUsername(String username);

    // Поиск заказа по id корзины
    Optional<Order> findByShoppingCartId(UUID shoppingCartId);

    // Поиск заказов по статусу
    List<Order> findByState(OrderState state);

    // Поиск заказа по id доставки
    Optional<Order> findByDeliveryId(UUID deliveryId);

    // Поиск заказа по id платежа
    Optional<Order> findByPaymentId(UUID paymentId);
}
