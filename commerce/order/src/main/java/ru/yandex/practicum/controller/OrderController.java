package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для работы с заказами.
 * Реализует API для управления заказами в системе.
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("${api.version}${api.order-path}")
public class OrderController implements ru.yandex.practicum.order.api.DefaultApi {

    private final OrderService orderService;

    @Override
    @GetMapping
    public List<OrderDto> getClientOrders(
            @NotNull @Valid @RequestParam("username") String username) {
        log.info("Получение заказов пользователя {}", username);
        return orderService.getClientOrders(username);
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequestDto createNewOrderRequestDto) {
        log.info("Создание нового заказа");
        return orderService.createNewOrder(createNewOrderRequestDto);
    }

    @Override
    @PostMapping("${api.return-path}")
    public OrderDto productReturn(@Valid @RequestParam ProductReturnRequestDto productReturnRequest) {
        log.info("Возврат заказа {}", productReturnRequest.getOrderId());
        return orderService.returnProducts(productReturnRequest);
    }

    @Override
    @PostMapping("${api.payment-path}")
    public OrderDto payment(@Valid @RequestBody UUID orderId) {
        log.info("Оплата заказа {}", orderId);
        return orderService.processPayment(orderId);
    }

    @Override
    @PostMapping("${api.payment-failed-path}")
    public OrderDto paymentFailed(@Valid @RequestBody UUID orderId) {
        log.info("Ошибка оплаты заказа {}", orderId);
        return orderService.handlePaymentFailure(orderId);
    }

    @Override
    @PostMapping("${api.delivery-path}")
    public OrderDto delivery(@Valid @RequestBody UUID orderId) {
        log.info("Доставка заказа {}", orderId);
        return orderService.processDelivery(orderId);
    }

    @Override
    @PostMapping("${api.delivery-failed-path}")
    public OrderDto deliveryFailed(@Valid @RequestBody UUID orderId) {
        log.info("Ошибка доставки заказа {}", orderId);
        return orderService.handleDeliveryFailure(orderId);
    }

    @Override
    @PostMapping("${api.completed-path}")
    public OrderDto complete(@Valid @RequestBody UUID orderId) {
        log.info("Завершение заказа {}", orderId);
        return orderService.completeOrder(orderId);
    }

    @Override
    @PostMapping("${api.calculate-total-cost-path}")
    public OrderDto calculateTotalCost(@Valid @RequestBody UUID orderId) {
        log.info("Расчет общей стоимости заказа {}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    @PostMapping("${api.calculate-delivery-cost-path}")
    public OrderDto calculateDeliveryCost(@Valid @RequestBody UUID orderId) {
        log.info("Расчет стоимости доставки для заказа {}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    @PostMapping("${api.assembly-path}")
    public OrderDto assembly(@Valid @RequestBody UUID orderId) {
        log.info("Сборка заказа {}", orderId);
        return orderService.assembleOrder(orderId);
    }

    @Override
    @PostMapping("${api.assembly-failed-path}")
    public OrderDto assemblyFailed(@Valid @RequestBody UUID orderId) {
        log.info("Ошибка сборки заказа {}", orderId);
        return orderService.handleAssemblyFailure(orderId);
    }
}