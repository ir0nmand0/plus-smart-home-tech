package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.payment.api.DefaultApi;
import ru.yandex.practicum.service.PaymentService;
import ru.yandex.practicum.common.model.*;

import java.util.UUID;

/**
 * Контроллер для работы с платежами.
 * Реализует API для расчёта стоимости и управления статусами оплаты.
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("${api.version}")
public class PaymentController implements DefaultApi {

    private final PaymentService paymentService;

    @Override
    @PostMapping("${api.product-cost-path}")
    public Double productCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("Расчет стоимости товаров для заказа {}", orderDto.getOrderId());
        return paymentService.calculateProductCost(orderDto);
    }

    @Override
    @PostMapping("${api.total-cost-path}")
    public Double getTotalCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("Расчет полной стоимости заказа {}", orderDto.getOrderId());
        return paymentService.calculateTotalCost(orderDto);
    }

    @Override
    @PostMapping("${api.payment-path}")
    public PaymentDto payment(@Valid @RequestBody OrderDto orderDto) {
        log.info("Создание платежа для заказа {}", orderDto.getOrderId());
        return paymentService.createPayment(orderDto);
    }

    @Override
    @PostMapping("${api.failed-path}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void paymentFailed(@Valid @RequestBody UUID orderId) {
        log.info("Отметка платежа как неуспешного для заказа {}", orderId);
        paymentService.markPaymentFailed(orderId);
    }

    @Override
    @PostMapping("${api.refund-path}")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void paymentSuccess(@Valid @RequestBody UUID orderId) {
        log.info("Отметка платежа как успешного для заказа {}", orderId);
        paymentService.markPaymentSuccessful(orderId);
    }
}