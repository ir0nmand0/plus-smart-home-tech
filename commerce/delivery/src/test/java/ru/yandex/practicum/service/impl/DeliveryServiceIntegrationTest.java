package ru.yandex.practicum.service.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.common.model.OrderDto;
import ru.yandex.practicum.config.FeignClientConfiguration;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled("Отключено, так как для выполнения теста требуется поднятый Eureka и Config Server")
@SpringBootTest
@Import(FeignClientConfiguration.class)
class DeliveryServiceIntegrationTest {

    @Autowired
    private DeliveryService deliveryService;

    @MockBean
    private DeliveryRepository deliveryRepository;

    @MockBean
    private DeliveryMapper deliveryMapper;

    @Test
    @DisplayName("Интеграционный тест расчета стоимости доставки с тестовыми Feign клиентами")
    void calculateDeliveryCost_WithFeignClients() {
        // Подготовка
        UUID orderId = UUID.randomUUID();
        OrderDto orderDto = new OrderDto()
                .orderId(orderId)
                .deliveryWeight(10.0)
                .deliveryVolume(5.0)
                .fragile(true);

        Delivery delivery = Delivery.builder()
                .deliveryId(UUID.randomUUID())
                .orderId(orderId)
                .build();

        when(deliveryRepository.findByOrderId(orderId)).thenReturn(java.util.Optional.of(delivery));

        // Выполнение
        Double cost = deliveryService.calculateDeliveryCost(orderDto);

        // Проверка
        assertNotNull(cost);
        assertTrue(cost > 0);
        // FeignClientTestConfiguration возвращает тестовый адрес склада ADDRESS_1
        assertTrue(cost >= 5.0); // базовая стоимость
    }

    @Test
    @DisplayName("Интеграционный тест проверки товаров на складе при планировании доставки")
    void planDelivery_ChecksWarehouse() {
        // Подготовка
        UUID orderId = UUID.randomUUID();
        UUID deliveryId = UUID.randomUUID();

        ru.yandex.practicum.common.model.AddressDto addressDto = new ru.yandex.practicum.common.model.AddressDto()
                .country("Test")
                .city("Test")
                .street("Test")
                .house("1");

        ru.yandex.practicum.common.model.DeliveryDto deliveryDto = new ru.yandex.practicum.common.model.DeliveryDto(
                deliveryId,
                addressDto,
                addressDto,
                orderId,
                ru.yandex.practicum.common.model.DeliveryState.CREATED
        );

        Delivery delivery = Delivery.builder()
                .deliveryId(deliveryId)
                .orderId(orderId)
                .build();

        when(deliveryMapper.toEntity(any())).thenReturn(delivery);
        when(deliveryRepository.save(any())).thenReturn(delivery);
        when(deliveryMapper.toDto(any())).thenReturn(deliveryDto);

        // Выполнение
        ru.yandex.practicum.common.model.DeliveryDto result = deliveryService.planDelivery(deliveryDto);

        // Проверка
        assertNotNull(result);
        assertEquals(deliveryId, result.getDeliveryId());
        assertEquals(orderId, result.getOrderId());

        // Проверяем, что заглушки Feign клиентов отработали без ошибок
        verify(deliveryRepository).save(any());
        verify(deliveryMapper).toDto(any());
    }
}