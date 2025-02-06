package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.api.DefaultApi;
import ru.yandex.practicum.service.DeliveryService;
import ru.yandex.practicum.common.model.*;
import java.util.UUID;

/**
 * Контроллер для работы с доставкой.
 * Реализует API для расчёта стоимости и управления статусами доставки.
 */
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("${api.version}${api.delivery.path}")
public class DeliveryController implements DefaultApi {

    private final DeliveryService deliveryService;

    /**
     * Расчёт полной стоимости доставки заказа.
     * @param orderDto информация о заказе
     * @return стоимость доставки
     */
    @Override
    @PostMapping("${api.delivery.cost}")
    public Double deliveryCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("Calculate delivery cost for order {}", orderDto.getOrderId());
        return deliveryService.calculateDeliveryCost(orderDto);
    }

    /**
     * Отметить доставку как неудачную.
     * @param orderId идентификатор заказа
     */
    @Override
    @PostMapping("${api.delivery.failed}")
    public void deliveryFailed(@Valid @RequestBody UUID orderId) {
        log.info("Mark delivery as failed for order {}", orderId);
        deliveryService.markDeliveryFailed(orderId);
    }

    /**
     * Отметить получение товара в доставку.
     * @param orderId идентификатор заказа
     */
    @Override
    @PostMapping("${api.delivery.picked}")
    public void deliveryPicked(@Valid @RequestBody UUID orderId) {
        log.info("Mark delivery as picked for order {}", orderId);
        deliveryService.markDeliveryPicked(orderId);
    }

    /**
     * Отметить доставку как успешную.
     * @param orderId идентификатор заказа
     */
    @Override
    @PostMapping("${api.delivery.successful}")
    public void deliverySuccessful(@Valid @RequestBody UUID orderId) {
        log.info("Mark delivery as successful for order {}", orderId);
        deliveryService.markDeliverySuccessful(orderId);
    }

    /**
     * Создать новую доставку.
     * @param deliveryDto информация о доставке
     * @return созданная доставка
     */
    @Override
    @PutMapping
    public DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        log.info("Plan new delivery for order {}", deliveryDto.getOrderId());
        return deliveryService.planDelivery(deliveryDto);
    }
}