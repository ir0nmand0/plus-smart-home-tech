package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.entity.OrderBooking;
import ru.yandex.practicum.entity.WarehouseProduct;
import ru.yandex.practicum.exception.*;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.service.WarehouseService;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Реализация сервиса для работы со складом интернет-магазина.
 * Предоставляет функциональность для:
 * <ul>
 * <li>Управления товарами (добавление новых, изменение количества)</li>
 * <li>Проверки наличия товаров</li>
 * <li>Сборки заказов</li>
 * <li>Работы с доставкой</li>
 * <li>Обработки возвратов</li>
 * </ul>
 *
 * @version 1.0
 * @since 2024-02-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    // Предопределенные адреса склада
    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    private final WarehouseProductRepository warehouseProductRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final WarehouseProductMapper warehouseProductMapper;

    /**
     * Добавляет новый товар в систему складского учета.
     * При добавлении нового товара устанавливается начальное количество 0.
     *
     * @param productId уникальный идентификатор товара
     * @param fragile признак хрупкости товара
     * @param weight вес единицы товара
     * @param dimensionDto размеры товара (ширина, высота, глубина)
     * @throws ProductAlreadyExistsInWarehouseException если товар с таким ID уже существует на складе
     */
    @Override
    @Transactional
    public void registerProduct(UUID productId, Boolean fragile, Double weight, DimensionDto dimensionDto) {
        log.info("Регистрация нового товара на складе: ID {}", productId);
        checkProductNotExists(productId);

        WarehouseProduct product = WarehouseProduct.builder()
                .productId(productId)
                .quantity(0L)
                .fragile(fragile)
                .weight(weight)
                .dimension(warehouseProductMapper.toDimensionEntity(dimensionDto))
                .build();

        warehouseProductRepository.save(product);
    }

    /**
     * Увеличивает количество существующего товара на складе.
     *
     * @param productId уникальный идентификатор товара
     * @param quantity количество единиц товара для добавления (должно быть положительным)
     * @throws ProductNotInWarehouseException если товар не найден на складе
     */
    @Override
    @Transactional
    public void addProduct(UUID productId, Long quantity) {
        log.info("Добавление {} единиц товара с ID {} на склад", quantity, productId);
        WarehouseProduct product = getProductById(productId);
        product.setQuantity(product.getQuantity() + quantity);
        warehouseProductRepository.save(product);
    }

    /**
     * Проверяет наличие всех товаров из корзины в достаточном количестве.
     * Вычисляет общий вес и объем заказа, определяет наличие хрупких товаров.
     *
     * @param cart корзина с товарами для проверки
     * @return информация о возможности бронирования с характеристиками заказа
     * @throws ProductNotInWarehouseException если товар не найден на складе
     * @throws InsufficientProductQuantityException если количество товара недостаточно
     */
    @Override
    public BookedProductsDto checkAvailability(ShoppingCartDto cart) {
        log.info("Проверка наличия товаров для корзины: {}", cart);

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<String, Long> entry : cart.getProducts().entrySet()) {
            WarehouseProduct product = getProductById(UUID.fromString(entry.getKey()));
            checkSufficientQuantity(product, entry.getValue());

            totalWeight += product.getWeight() * entry.getValue();
            totalVolume += product.getDimension().calculateVolume() * entry.getValue();
            hasFragile |= product.getFragile();
        }

        return new BookedProductsDto()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile);
    }

    /**
     * Собирает товары для заказа.
     * Процесс включает:
     * <ul>
     * <li>Первичную проверку наличия всех товаров</li>
     * <li>Повторную проверку и резервацию товаров</li>
     * <li>Уменьшение доступного остатка</li>
     * <li>Создание записи о бронировании</li>
     * </ul>
     *
     * @param request запрос на сборку, содержащий ID заказа и список товаров с количеством
     * @return информация о собранном заказе (вес, объем, наличие хрупких товаров)
     * @throws ProductNotInWarehouseException если товар не найден на складе
     * @throws InsufficientProductQuantityException если количество товара недостаточно
     */
    @Override
    @Transactional
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequestDto request) {
        log.info("Сборка товаров для заказа: {}", request);

        Map<UUID, Long> bookedItems = new HashMap<>();
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        // Первичная проверка наличия товаров
        for (Map.Entry<String, Long> entry : request.getProducts().entrySet()) {
            UUID productId = UUID.fromString(entry.getKey());
            Long quantity = entry.getValue();

            WarehouseProduct product = getProductById(productId);
            checkSufficientQuantity(product, quantity);
        }

        // Повторная проверка и резервация товаров
        for (Map.Entry<String, Long> entry : request.getProducts().entrySet()) {
            UUID productId = UUID.fromString(entry.getKey());
            Long quantity = entry.getValue();

            WarehouseProduct product = getProductById(productId);
            // Повторная проверка наличия
            checkSufficientQuantity(product, quantity);

            // Уменьшаем количество на складе
            product.setQuantity(product.getQuantity() - quantity);
            warehouseProductRepository.save(product);

            bookedItems.put(productId, quantity);
            totalWeight += product.getWeight() * quantity;
            totalVolume += product.getDimension().calculateVolume() * quantity;
            hasFragile |= product.getFragile();
        }

        // Создаем запись о бронировании
        OrderBooking booking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .bookedItems(bookedItems)
                .totalWeight(totalWeight)
                .totalVolume(totalVolume)
                .hasFragileItems(hasFragile)
                .build();

        orderBookingRepository.save(booking);

        return new BookedProductsDto()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile);
    }

    /**
     * Обновляет информацию о заказе при передаче в доставку.
     * Метод вызывается сервисом доставки после успешного создания доставки.
     * Добавляет идентификатор доставки к информации о бронировании заказа.
     *
     * @param request информация о заказе и присвоенном ему идентификаторе доставки
     * @throws IllegalStateException если бронирование заказа не найдено
     */
    @Override
    @Transactional
    public void shipToDelivery(ShippedToDeliveryRequestDto request) {
        log.info("Передача товаров в доставку (вызов из сервиса доставки): {}", request);

        // Поиск бронирования по ID заказа
        OrderBooking booking = orderBookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new IllegalStateException(
                        "Бронирование не найдено для заказа: " + request.getOrderId()));

        // Обновление информации о доставке
        booking.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(booking);
    }

    /**
     * Обрабатывает возврат товаров на склад.
     * Увеличивает доступное количество для каждого возвращаемого товара.
     *
     * @param returns карта, где ключ - идентификатор товара, значение - возвращаемое количество
     * @throws ProductNotInWarehouseException если возвращаемый товар не найден на складе
     */
    @Override
    @Transactional
    public void returnProducts(Map<String, Long> returns) {
        log.info("Возврат товаров на склад: {}", returns);

        for (Map.Entry<String, Long> entry : returns.entrySet()) {
            UUID productId = UUID.fromString(entry.getKey());
            Long quantity = entry.getValue();

            WarehouseProduct product = getProductById(productId);
            product.setQuantity(product.getQuantity() + quantity);
            warehouseProductRepository.save(product);
        }
    }

    /**
     * Возвращает текущий адрес склада.
     * Адрес выбирается случайным образом из списка доступных адресов.
     *
     * @return объект с информацией об адресе склада
     */
    @Override
    public AddressDto getAddress() {
        log.info("Получение адреса склада");
        return new AddressDto()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS);
    }

    /**
     * Получает информацию о товаре по его идентификатору.
     *
     * @param productId уникальный идентификатор товара
     * @return информация о товаре
     * @throws ProductNotInWarehouseException если товар не найден
     */
    private WarehouseProduct getProductById(UUID productId) {
        return warehouseProductRepository.findByProductId(productId)
                .orElseThrow(ProductNotInWarehouseException::new);
    }

    /**
     * Проверяет отсутствие товара на складе.
     * Используется при регистрации новых товаров.
     *
     * @param productId уникальный идентификатор товара
     * @throws ProductAlreadyExistsInWarehouseException если товар уже существует
     */
    private void checkProductNotExists(UUID productId) {
        if (warehouseProductRepository.existsByProductId(productId)) {
            throw new ProductAlreadyExistsInWarehouseException();
        }
    }

    /**
     * Проверяет достаточность количества товара на складе.
     *
     * @param product информация о товаре
     * @param requestedQuantity запрашиваемое количество
     * @throws InsufficientProductQuantityException если количества недостаточно
     */
    private void checkSufficientQuantity(WarehouseProduct product, Long requestedQuantity) {
        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientProductQuantityException();
        }
    }
}