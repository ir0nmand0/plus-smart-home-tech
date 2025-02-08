package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.CartClient;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.entity.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с заказами
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartClient cartClient;

    @Override
    public List<OrderDto> getClientOrders(String username) {
        log.info("Получение заказов пользователя {}", username);
        return orderRepository.findByUsername(username).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequestDto request) {
        log.info("Создание нового заказа");

        var cart = request.getShoppingCart();
        // Здесь используем клиент корзины, передавая cart.getShoppingCartId()
        BookedProductsDto bookedProducts = cartClient
                .bookingProductsFromShoppingCart(cart.getShoppingCartId().toString());

        Order order = new Order();
        order.setState(OrderState.NEW);
        order.setShoppingCartId(cart.getShoppingCartId());
        order.setFragile(false);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto returnProducts(ProductReturnRequestDto request) {
        log.info("Возврат товаров для заказа {}", request.getOrderId());
        Order order = getOrderById(request.getOrderId());
        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto processPayment(UUID orderId) {
        log.info("Обработка оплаты заказа {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto handlePaymentFailure(UUID orderId) {
        log.info("Обработка неуспешной оплаты заказа {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto processDelivery(UUID orderId) {
        log.info("Обработка доставки заказа {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderState.ON_DELIVERY);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto handleDeliveryFailure(UUID orderId) {
        log.info("Обработка неуспешной доставки заказа {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        log.info("Завершение заказа {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Расчет общей стоимости заказа {}", orderId);
        Order order = getOrderById(orderId);
        // Здесь должна быть логика расчета стоимости
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Расчет стоимости доставки для заказа {}", orderId);
        Order order = getOrderById(orderId);
        // Здесь должна быть логика расчета стоимости доставки
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto assembleOrder(UUID orderId) {
        log.info("Сборка заказа {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto handleAssemblyFailure(UUID orderId) {
        log.info("Обработка неуспешной сборки заказа {}", orderId);
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден: " + orderId));
    }
}