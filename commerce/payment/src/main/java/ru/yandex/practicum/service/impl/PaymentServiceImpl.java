package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.StoreClient;
import ru.yandex.practicum.common.model.OrderDto;
import ru.yandex.practicum.common.model.PaymentDto;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.entity.PaymentStatus;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.PaymentCalculationException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.service.PaymentService;

import java.util.Map;
import java.util.UUID;

/**
 * Реализация сервиса для работы с платежами
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private static final double TAX_RATE = 0.10; // 10% налог

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StoreClient storeClient;
    private final DeliveryClient deliveryClient;
    private final OrderClient orderClient;

    @Override
    public Double calculateProductCost(OrderDto orderDto) {
        log.info("Расчет стоимости товаров для заказа {}", orderDto.getOrderId());

        if (orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе отсутствуют товары");
        }

        try {
            double totalProductCost = 0.0;
            Map<String, Long> products = orderDto.getProducts();

            // Для каждого товара получаем цену и умножаем на количество
            for (Map.Entry<String, Long> entry : products.entrySet()) {
                String productId = entry.getKey();
                Long quantity = entry.getValue();

                // Получаем актуальную цену товара из сервиса магазина
                var productResponse = storeClient.getProduct(UUID.fromString(productId));
                var product = productResponse.getBody();

                if (product != null && product.getPrice() != null) {
                    totalProductCost += product.getPrice() * quantity;
                } else {
                    throw new PaymentCalculationException("Не удалось получить цену товара " + productId);
                }
            }

            return totalProductCost;
        } catch (Exception e) {
            throw new PaymentCalculationException("Ошибка при расчете стоимости товаров: " + e.getMessage());
        }
    }

    @Override
    public Double calculateTotalCost(OrderDto orderDto) {
        log.info("Расчет полной стоимости заказа {}", orderDto.getOrderId());

        // Рассчитываем стоимость товаров
        double productCost = calculateProductCost(orderDto);

        // Рассчитываем НДС (10% от стоимости товаров)
        double tax = productCost * TAX_RATE;

        // Получаем стоимость доставки
        Double deliveryCost = deliveryClient.deliveryCost(orderDto);

        if (deliveryCost == null) {
            throw new PaymentCalculationException("Не удалось рассчитать стоимость доставки");
        }

        // Суммируем все составляющие
        return productCost + tax + deliveryCost;
    }

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Создание платежа для заказа {}", orderDto.getOrderId());

        double productCost = calculateProductCost(orderDto);
        double tax = productCost * TAX_RATE;
        Double deliveryCost = deliveryClient.deliveryCost(orderDto);

        if (deliveryCost == null) {
            throw new PaymentCalculationException("Не удалось рассчитать стоимость доставки");
        }

        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .productTotal(productCost)
                .taxTotal(tax)
                .deliveryTotal(deliveryCost)
                .totalPayment(productCost + tax + deliveryCost)
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    @Transactional
    public void markPaymentSuccessful(UUID orderId) {
        log.info("Отметка платежа как успешного для заказа {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoPaymentFoundException("Платеж не найден для заказа: " + orderId));

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // Обновляем статус заказа
        orderClient.payment(orderId);
    }

    @Override
    @Transactional
    public void markPaymentFailed(UUID orderId) {
        log.info("Отметка платежа как неуспешного для заказа {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoPaymentFoundException("Платеж не найден для заказа: " + orderId));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        // Обновляем статус заказа
        orderClient.paymentFailed(orderId);
    }
}