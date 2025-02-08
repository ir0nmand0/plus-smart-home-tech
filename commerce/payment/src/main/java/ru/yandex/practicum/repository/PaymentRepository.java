package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.entity.PaymentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с платежами
 */
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Поиск платежа по id заказа
    Optional<Payment> findByOrderId(UUID orderId);

    // Поиск платежей по статусу
    List<Payment> findByStatus(PaymentStatus status);

    // Поиск платежа по id заказа и статусу
    Optional<Payment> findByOrderIdAndStatus(UUID orderId, PaymentStatus status);
}
