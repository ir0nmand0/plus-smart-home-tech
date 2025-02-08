package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.entity.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.service.DeliveryService;
import ru.yandex.practicum.common.model.OrderDto;
import ru.yandex.practicum.common.model.AddressDto;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private static final double BASE_COST = 5.0;
    private static final String ADDRESS_1 = "ADDRESS_1";
    private static final String ADDRESS_2 = "ADDRESS_2";
    private static final double ADDRESS_1_MULTIPLIER = 1.0;
    private static final double ADDRESS_2_MULTIPLIER = 2.0;
    private static final double FRAGILE_MULTIPLIER = 0.2;
    private static final double WEIGHT_MULTIPLIER = 0.3;
    private static final double VOLUME_MULTIPLIER = 0.2;
    private static final double DIFFERENT_STREET_MULTIPLIER = 0.2;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    public Double calculateDeliveryCost(OrderDto orderDto) {
        log.info("Calculating delivery cost for order: {}", orderDto.getOrderId());

        double cost = BASE_COST;

        // Получаем адрес склада
        AddressDto addressResponse = warehouseClient.getWarehouseAddress();
        AddressDto warehouseAddress = addressResponse;
        if (warehouseAddress == null) {
            throw new NoDeliveryFoundException("Warehouse address not found");
        }

        // Применяем множитель в зависимости от адреса склада
        if (warehouseAddress.getStreet().contains(ADDRESS_2)) {
            cost = cost * ADDRESS_2_MULTIPLIER + BASE_COST;
        } else if (warehouseAddress.getStreet().contains(ADDRESS_1)) {
            cost = cost * ADDRESS_1_MULTIPLIER + BASE_COST;
        }

        // Учитываем хрупкость
        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            cost = cost + (cost * FRAGILE_MULTIPLIER);
        }

        // Добавляем стоимость за вес
        if (orderDto.getDeliveryWeight() != null) {
            cost = cost + (orderDto.getDeliveryWeight() * WEIGHT_MULTIPLIER);
        }

        // Добавляем стоимость за объем
        if (orderDto.getDeliveryVolume() != null) {
            cost = cost + (orderDto.getDeliveryVolume() * VOLUME_MULTIPLIER);
        }

        // Проверяем улицу доставки
        Delivery delivery = findDeliveryByOrderId(orderDto.getOrderId());
        DeliveryDto deliveryDto = deliveryMapper.toDto(delivery);

        if (!warehouseAddress.getStreet().equals(deliveryDto.getToAddress().getStreet())) {
            cost = cost + (cost * DIFFERENT_STREET_MULTIPLIER);
        }

        // Округляем до двух знаков после запятой
        return Math.round(cost * 100.0) / 100.0;
    }

    @Override
    @Transactional
    public void markDeliveryFailed(UUID orderId) {
        log.info("Marking delivery as failed for order: {}", orderId);

        Delivery delivery = findDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        // Уведомляем сервис заказов
        orderClient.deliveryFailed(orderId);
    }

    @Override
    @Transactional
    public void markDeliveryPicked(UUID orderId) {
        log.info("Marking delivery as picked for order: {}", orderId);

        Delivery delivery = findDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        // Обновляем статус заказа на "ASSEMBLED"
        orderClient.assembly(orderId);

        // Связываем с внутренней учетной системой склада через запрос на отгрузку
        ShippedToDeliveryRequestDto shippedRequest = new ShippedToDeliveryRequestDto()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId());

        warehouseClient.shippedToDelivery(shippedRequest);
    }

    @Override
    @Transactional
    public void markDeliverySuccessful(UUID orderId) {
        log.info("Marking delivery as successful for order: {}", orderId);

        Delivery delivery = findDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        // Уведомляем сервис заказов
        orderClient.complete(orderId);
    }

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Planning new delivery for order: {}", deliveryDto.getOrderId());

        // Проверяем наличие товаров на складе перед планированием доставки
        List<OrderDto> ordersResponse = orderClient.getClientOrders(deliveryDto.getOrderId().toString());
        List<OrderDto> orders = ordersResponse;
        if (orders == null || orders.isEmpty()) {
            throw new NoDeliveryFoundException("Order not found: " + deliveryDto.getOrderId());
        }

        OrderDto order = orders.get(0);
        if (!order.getShoppingCartId().isPresent()) {
            throw new NoDeliveryFoundException("Shopping cart ID not found");
        }
        UUID cartId = order.getShoppingCartId().get();

        ShoppingCartDto cart = new ShoppingCartDto()
                .shoppingCartId(cartId)
                .products(order.getProducts());

        BookedProductsDto bookedResponse = warehouseClient.checkProductQuantityEnoughForShoppingCart(cart);
        BookedProductsDto bookedProducts = bookedResponse;
        if (bookedProducts == null) {
            throw new NoDeliveryFoundException("Failed to verify products availability");
        }

        // Сохраняем доставку с начальным статусом
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);

        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toDto(saved);
    }

    private Delivery findDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery not found for order: " + orderId));
    }
}