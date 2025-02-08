package ru.yandex.practicum.service;

import ru.yandex.practicum.common.model.DeliveryDto;
import ru.yandex.practicum.common.model.OrderDto;
import java.util.UUID;

public interface DeliveryService {
    Double calculateDeliveryCost(OrderDto orderDto);
    void markDeliveryFailed(UUID orderId);
    void markDeliveryPicked(UUID orderId);
    void markDeliverySuccessful(UUID orderId);
    DeliveryDto planDelivery(DeliveryDto deliveryDto);
}
