package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.PaymentHistory;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с историей платежей
 */
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, UUID> {

    // Поиск истории по id платежа
    List<PaymentHistory> findByPaymentId(UUID paymentId);

    // Поиск истории по id платежа с сортировкой по времени изменения
    List<PaymentHistory> findByPaymentIdOrderByChangedAtDesc(UUID paymentId);
}