package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.service.OrderService;
import ru.yandex.practicum.order.api.OrderProcessorApi;

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
@RequestMapping("/api/v${api.order-version}/order")
public class OrderController implements OrderProcessorApi {

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
    @PostMapping("/return")
    public OrderDto productReturn(@Valid @RequestParam ProductReturnRequestDto productReturnRequest) {
        log.info("Возврат заказа {}", productReturnRequest.getOrderId());
        return orderService.returnProducts(productReturnRequest);
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payment(@Valid @RequestBody UUID orderId) {
        log.info("Оплата заказа {}", orderId);
        return orderService.processPayment(orderId);
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@Valid @RequestBody UUID orderId) {
        log.info("Ошибка оплаты заказа {}", orderId);
        return orderService.handlePaymentFailure(orderId);
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto delivery(@Valid @RequestBody UUID orderId) {
        log.info("Доставка заказа {}", orderId);
        return orderService.processDelivery(orderId);
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@Valid @RequestBody UUID orderId) {
        log.info("Ошибка доставки заказа {}", orderId);
        return orderService.handleDeliveryFailure(orderId);
    }

    @Override
    @PostMapping("/completed")
    public OrderDto complete(@Valid @RequestBody UUID orderId) {
        log.info("Завершение заказа {}", orderId);
        return orderService.completeOrder(orderId);
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(@Valid @RequestBody UUID orderId) {
        log.info("Расчет общей стоимости заказа {}", orderId);
        return orderService.calculateTotalCost(orderId);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@Valid @RequestBody UUID orderId) {
        log.info("Расчет стоимости доставки для заказа {}", orderId);
        return orderService.calculateDeliveryCost(orderId);
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assembly(@Valid @RequestBody UUID orderId) {
        log.info("Сборка заказа {}", orderId);
        return orderService.assembleOrder(orderId);
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@Valid @RequestBody UUID orderId) {
        log.info("Ошибка сборки заказа {}", orderId);
        return orderService.handleAssemblyFailure(orderId);
    }
}