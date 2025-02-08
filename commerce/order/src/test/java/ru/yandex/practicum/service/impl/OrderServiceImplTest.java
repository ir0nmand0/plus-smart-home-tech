package ru.yandex.practicum.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.client.CartClient;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.entity.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private CartClient cartClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID orderId;
    private String username;
    private Order order;
    private OrderDto orderDto;
    private CreateNewOrderRequestDto createNewOrderRequestDto;
    private ShoppingCartDto shoppingCartDto;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        // Инициализация базовых переменных
        orderId = UUID.randomUUID();
        username = "testUser";
        UUID shoppingCartId = UUID.randomUUID();

        // Создаем тестовый заказ (Entity)
        order = Order.builder()
                .orderId(orderId)
                .username(username)
                .shoppingCartId(shoppingCartId)
                .state(OrderState.NEW)
                .totalPrice(100.0)
                .deliveryPrice(10.0)
                .productPrice(90.0)
                .fragile(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Создаем тестовый DTO заказа с правильным типом для state
        Map<String, Long> products = new HashMap<>();
        products.put(UUID.randomUUID().toString(), 2L);

        orderDto = new OrderDto()
                .orderId(orderId)
                .shoppingCartId(shoppingCartId)
                .state(OrderDto.StateEnum.NEW)
                .totalPrice(100.0)
                .deliveryPrice(10.0)
                .productPrice(90.0)
                .products(products);

        // Создаем тестовый запрос на создание заказа
        shoppingCartDto = new ShoppingCartDto()
                .shoppingCartId(shoppingCartId)
                .products(products);

        addressDto = new AddressDto()
                .city("Test City")
                .street("Test Street")
                .house("1")
                .flat("1");

        createNewOrderRequestDto = new CreateNewOrderRequestDto(shoppingCartDto, addressDto);
    }

    @Test
    @DisplayName("Тест получения заказов пользователя")
    void getClientOrders_Success() {
        // Подготовка данных
        List<Order> orders = Collections.singletonList(order);

        // При преобразовании энтити в DTO должен использоваться маппер
        when(orderRepository.findByUsername(username)).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        // Выполнение тестируемого метода
        List<OrderDto> result = orderService.getClientOrders(username);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDto, result.get(0));
        verify(orderRepository).findByUsername(username);
        verify(orderMapper).toDto(order);
    }

    @Test
    @DisplayName("Тест создания нового заказа")
    void createNewOrder_Success() {
        // Подготовка данных
        BookedProductsDto bookedProductsDto = new BookedProductsDto();

        // Настройка маппера для правильного преобразования состояний
        when(cartClient.bookingProductsFromShoppingCart(any())).thenReturn(bookedProductsDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderDto);

        // Выполнение тестируемого метода
        OrderDto result = orderService.createNewOrder(createNewOrderRequestDto);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(orderDto, result);
        verify(cartClient).bookingProductsFromShoppingCart(any());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Тест возврата товаров")
    void returnProducts_Success() {
        // Подготовка данных
        ProductReturnRequestDto request = new ProductReturnRequestDto()
                .orderId(orderId)
                .products(new HashMap<>());

        Order updatedOrder = Order.builder()
                .orderId(orderId)
                .username(username)
                .shoppingCartId(order.getShoppingCartId())
                .state(OrderState.PRODUCT_RETURNED)
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .fragile(order.getFragile())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        OrderDto updatedOrderDto = new OrderDto()
                .orderId(orderId)
                .state(OrderDto.StateEnum.PRODUCT_RETURNED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(orderMapper.toDto(any(Order.class))).thenReturn(updatedOrderDto);

        // Выполнение тестируемого метода
        OrderDto result = orderService.returnProducts(request);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(OrderDto.StateEnum.PRODUCT_RETURNED, result.getState());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Тест обработки успешной оплаты")
    void processPayment_Success() {
        Order updatedOrder = Order.builder()
                .orderId(orderId)
                .username(username)
                .shoppingCartId(order.getShoppingCartId())
                .state(OrderState.PAID)
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .fragile(order.getFragile())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        OrderDto updatedOrderDto = new OrderDto()
                .orderId(orderId)
                .state(OrderDto.StateEnum.PAID);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(orderMapper.toDto(any(Order.class))).thenReturn(updatedOrderDto);

        OrderDto result = orderService.processPayment(orderId);

        assertNotNull(result);
        assertEquals(OrderDto.StateEnum.PAID, result.getState());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Тест получения несуществующего заказа")
    void getOrderById_NotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NoOrderFoundException.class, () -> orderService.processPayment(orderId));
        verify(orderRepository).findById(orderId);
    }
}