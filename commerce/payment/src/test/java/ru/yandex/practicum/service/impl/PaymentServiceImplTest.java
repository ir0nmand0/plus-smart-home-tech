package ru.yandex.practicum.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.StoreClient;
import ru.yandex.practicum.common.model.OrderDto;
import ru.yandex.practicum.common.model.PaymentDto;
import ru.yandex.practicum.common.model.ProductDto;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.entity.PaymentStatus;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exception.PaymentCalculationException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    // Объявляем mock-объекты для всех зависимостей
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StoreClient storeClient;
    @Mock
    private DeliveryClient deliveryClient;
    @Mock
    private OrderClient orderClient;

    // Внедряем mock-объекты в тестируемый сервис
    @InjectMocks
    private PaymentServiceImpl paymentService;

    // Объявляем общие переменные для тестов
    private OrderDto orderDto;
    private UUID orderId;
    private static final double TAX_RATE = 0.10;

    @BeforeEach
    void setUp() {
        // Инициализируем тестовые данные перед каждым тестом
        orderId = UUID.randomUUID();
        orderDto = new OrderDto();
        orderDto.setOrderId(orderId);
        Map<String, Long> products = new HashMap<>();
        products.put(UUID.randomUUID().toString(), 2L);
        orderDto.setProducts(products);
    }

    @Test
    @DisplayName("Тест успешного расчета стоимости продуктов")
    void calculateProductCost_Success() {
        // Подготавливаем тестовые данные
        ProductDto productDto = new ProductDto();
        productDto.setPrice(100.0);

        // Настраиваем поведение мока StoreClient
        when(storeClient.getProduct(any()))
                .thenReturn(productDto);

        // Вызываем тестируемый метод
        Double result = paymentService.calculateProductCost(orderDto);

        // Проверяем результат
        assertEquals(200.0, result); // 100.0 * 2 (количество)
        verify(storeClient).getProduct(any());
    }

    @Test
    @DisplayName("Тест расчета стоимости при пустом списке продуктов")
    void calculateProductCost_EmptyProducts() {
        // Подготавливаем тестовые данные с пустым списком продуктов
        orderDto.setProducts(new HashMap<>());

        // Проверяем, что метод выбрасывает ожидаемое исключение
        assertThrows(NotEnoughInfoInOrderToCalculateException.class,
                () -> paymentService.calculateProductCost(orderDto));
    }

    @Test
    @DisplayName("Тест расчета полной стоимости заказа")
    void calculateTotalCost_Success() {
        // Подготавливаем тестовые данные
        ProductDto productDto = new ProductDto();
        productDto.setPrice(100.0);
        Double deliveryCost = 50.0;

        // Настраиваем поведение моков
        when(storeClient.getProduct(any()))
                .thenReturn(productDto);
        when(deliveryClient.deliveryCost(any()))
                .thenReturn(deliveryCost);

        // Вызываем тестируемый метод
        Double result = paymentService.calculateTotalCost(orderDto);

        // Проверяем результат
        // Стоимость товаров (200.0) + НДС (20.0) + доставка (50.0)
        assertEquals(270.0, result);
    }

    @Test
    @DisplayName("Тест создания платежа")
    void createPayment_Success() {
        // Подготавливаем тестовые данные
        ProductDto productDto = new ProductDto();
        productDto.setPrice(100.0);
        Double deliveryCost = 50.0;
        Payment payment = Payment.builder()
                .orderId(orderId)
                .productTotal(200.0)
                .taxTotal(20.0)
                .deliveryTotal(50.0)
                .totalPayment(270.0)
                .status(PaymentStatus.PENDING)
                .build();
        PaymentDto expectedPaymentDto = new PaymentDto();

        // Настраиваем поведение моков
        when(storeClient.getProduct(any()))
                .thenReturn(productDto);
        when(deliveryClient.deliveryCost(any()))
                .thenReturn(deliveryCost);
        when(paymentRepository.save(any()))
                .thenReturn(payment);
        when(paymentMapper.toDto(any()))
                .thenReturn(expectedPaymentDto);

        // Вызываем тестируемый метод
        PaymentDto result = paymentService.createPayment(orderDto);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(expectedPaymentDto, result);
        verify(paymentRepository).save(any());
        verify(paymentMapper).toDto(any());
    }

    @Test
    @DisplayName("Тест успешной отметки платежа как выполненного")
    void markPaymentSuccessful_Success() {
        // Подготавливаем тестовые данные
        Payment payment = Payment.builder()
                .orderId(orderId)
                .status(PaymentStatus.PENDING)
                .build();

        // Настраиваем поведение моков
        when(paymentRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any()))
                .thenReturn(payment);

        // Вызываем тестируемый метод
        paymentService.markPaymentSuccessful(orderId);

        // Проверяем результат
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        verify(paymentRepository).save(payment);
        verify(orderClient).payment(orderId);
    }

    @Test
    @DisplayName("Тест отметки платежа как неуспешного")
    void markPaymentFailed_Success() {
        // Подготавливаем тестовые данные
        Payment payment = Payment.builder()
                .orderId(orderId)
                .status(PaymentStatus.PENDING)
                .build();

        // Настраиваем поведение моков
        when(paymentRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any()))
                .thenReturn(payment);

        // Вызываем тестируемый метод
        paymentService.markPaymentFailed(orderId);

        // Проверяем результат
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        verify(paymentRepository).save(payment);
        verify(orderClient).paymentFailed(orderId);
    }

    @Test
    @DisplayName("Тест попытки отметить несуществующий платеж как выполненный")
    void markPaymentSuccessful_PaymentNotFound() {
        // Настраиваем поведение мока
        when(paymentRepository.findByOrderId(orderId))
                .thenReturn(Optional.empty());

        // Проверяем, что метод выбрасывает ожидаемое исключение
        assertThrows(NoPaymentFoundException.class,
                () -> paymentService.markPaymentSuccessful(orderId));
    }

    @Test
    @DisplayName("Тест ошибки при расчете стоимости доставки")
    void calculateTotalCost_DeliveryCostError() {
        // Подготавливаем тестовые данные
        ProductDto productDto = new ProductDto();
        productDto.setPrice(100.0);

        // Настраиваем поведение моков
        when(storeClient.getProduct(any()))
                .thenReturn(productDto);
        when(deliveryClient.deliveryCost(any()))
                .thenReturn(null);

        // Проверяем, что метод выбрасывает ожидаемое исключение
        assertThrows(PaymentCalculationException.class,
                () -> paymentService.calculateTotalCost(orderDto));
    }
}