package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.DeliveryHistory;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с историей доставок
 */
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, UUID> {

    // Поиск истории по id доставки
    List<DeliveryHistory> findByDeliveryId(UUID deliveryId);

    // Поиск истории по id доставки с сортировкой по времени изменения
    List<DeliveryHistory> findByDeliveryIdOrderByChangedAtDesc(UUID deliveryId);
}