package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.entity.DeliveryState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с доставками
 */
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    // Поиск доставки по id заказа
    Optional<Delivery> findByOrderId(UUID orderId);

    // Поиск доставок по статусу
    List<Delivery> findByDeliveryState(DeliveryState state);

    // Поиск по id заказа и статусу
    Optional<Delivery> findByOrderIdAndDeliveryState(UUID orderId, DeliveryState state);
}