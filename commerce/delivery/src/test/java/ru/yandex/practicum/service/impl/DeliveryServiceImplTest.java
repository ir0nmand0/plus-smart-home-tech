package ru.yandex.practicum.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.entity.Address;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.entity.DeliveryState;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.yandex.practicum.common.model.DeliveryStateDto.CREATED;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private DeliveryMapper deliveryMapper;
    @Mock
    private OrderClient orderClient;
    @Mock
    private WarehouseClient warehouseClient;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private UUID orderId;
    private UUID deliveryId;
    private Address fromAddress;
    private Address toAddress;
    private Delivery delivery;
    private DeliveryDto deliveryDto;
    private AddressDto fromAddressDto;
    private AddressDto toAddressDto;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        // Инициализация базовых переменных
        orderId = UUID.randomUUID();
        deliveryId = UUID.randomUUID();

        // Создаем тестовые адреса (Entity)
        fromAddress = Address.builder()
                .addressId(UUID.randomUUID())
                .country("Россия")
                .city("Москва")
                .street("ADDRESS_1")
                .house("1")
                .flat("1")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        toAddress = Address.builder()
                .addressId(UUID.randomUUID())
                .country("Россия")
                .city("Москва")
                .street("Test Street")
                .house("2")
                .flat("2")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Создаем тестовую доставку (Entity)
        delivery = Delivery.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .deliveryState(DeliveryState.CREATED)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Создаем тестовые DTO адресов
        fromAddressDto = new AddressDto()
                .country("Россия")
                .city("Москва")
                .street("ADDRESS_1")
                .house("1")
                .flat("1");

        toAddressDto = new AddressDto()
                .country("Россия")
                .city("Москва")
                .street("Test Street")
                .house("2")
                .flat("2");

        // Создаем тестовое DTO доставки
        deliveryDto = new DeliveryDto(
                deliveryId,
                fromAddressDto,
                toAddressDto,
                orderId,
                CREATED
        );

        // Создаем тестовый заказ
        orderDto = new OrderDto()
                .orderId(orderId)
                .deliveryWeight(10.0)
                .deliveryVolume(5.0)
                .fragile(true);
    }

    @Test
    @DisplayName("Тест успешного расчета стоимости доставки")
    void calculateDeliveryCost_Success() {
        // Настраиваем поведение моков
        when(warehouseClient.getWarehouseAddress()).thenReturn(fromAddressDto);
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));
        when(deliveryMapper.toDto(delivery)).thenReturn(deliveryDto);

        // Вызываем тестируемый метод
        Double cost = deliveryService.calculateDeliveryCost(orderDto);

        // Проверяем результат
        assertNotNull(cost);
        assertTrue(cost > 0);
        verify(warehouseClient).getWarehouseAddress();
        verify(deliveryRepository).findByOrderId(orderId);
    }

    @Test
    @DisplayName("Тест маркировки доставки как неуспешной")
    void markDeliveryFailed_Success() {
        // Настраиваем поведение моков
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        // Вызываем тестируемый метод
        deliveryService.markDeliveryFailed(orderId);

        // Проверяем результат
        assertEquals(DeliveryState.FAILED, delivery.getDeliveryState());
        verify(orderClient).deliveryFailed(orderId);
        verify(deliveryRepository).save(delivery);
    }

    @Test
    @DisplayName("Тест маркировки доставки как полученной")
    void markDeliveryPicked_Success() {
        // Настраиваем поведение моков
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        // Вызываем тестируемый метод
        deliveryService.markDeliveryPicked(orderId);

        // Проверяем результат
        assertEquals(DeliveryState.IN_PROGRESS, delivery.getDeliveryState());
        verify(orderClient).assembly(orderId);
        verify(warehouseClient).shippedToDelivery(any());
        verify(deliveryRepository).save(delivery);
    }

    @Test
    @DisplayName("Тест маркировки доставки как успешной")
    void markDeliverySuccessful_Success() {
        // Настраиваем поведение моков
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        // Вызываем тестируемый метод
        deliveryService.markDeliverySuccessful(orderId);

        // Проверяем результат
        assertEquals(DeliveryState.DELIVERED, delivery.getDeliveryState());
        verify(orderClient).complete(orderId);
        verify(deliveryRepository).save(delivery);
    }

    @Test
    @DisplayName("Тест планирования новой доставки")
    void planDelivery_Success() {
        // Подготавливаем тестовые данные
        OrderDto orderResponse = new OrderDto()
                .orderId(orderId)
                .shoppingCartId(Optional.of(UUID.randomUUID()).orElse(null));
        List<OrderDto> orders = Collections.singletonList(orderResponse);

        // Настраиваем поведение моков
        when(orderClient.getClientOrders(orderId.toString())).thenReturn(orders);
        when(warehouseClient.checkProductQuantityEnoughForShoppingCart(any())).thenReturn(new BookedProductsDto());
        when(deliveryMapper.toEntity(any(DeliveryDto.class))).thenReturn(delivery);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(deliveryMapper.toDto(any(Delivery.class))).thenReturn(deliveryDto);

        // Вызываем тестируемый метод
        DeliveryDto result = deliveryService.planDelivery(deliveryDto);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(deliveryDto, result);
        verify(orderClient).getClientOrders(orderId.toString());
        verify(warehouseClient).checkProductQuantityEnoughForShoppingCart(any());
        verify(deliveryRepository).save(any(Delivery.class));
        verify(deliveryMapper).toDto(any(Delivery.class));
    }

    @Test
    @DisplayName("Тест получения несуществующей доставки")
    void getDeliveryByOrderId_NotFound() {
        // Настраиваем поведение мока
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        // Проверяем, что метод выбрасывает ожидаемое исключение
        assertThrows(NoDeliveryFoundException.class,
                () -> deliveryService.markDeliverySuccessful(orderId));
        verify(deliveryRepository).findByOrderId(orderId);
    }
}