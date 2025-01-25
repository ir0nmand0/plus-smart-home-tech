package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.entity.Cart;
import ru.yandex.practicum.entity.CartProduct;
import ru.yandex.practicum.entity.CartProductId;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductInShoppingCartNotInWarehouseException;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.common.model.ProductDto;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.repository.CartRepository;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.common.model.QuantityState;
import ru.yandex.practicum.common.model.ShoppingCartDto;

import java.util.*;

/**
 * Реализация сервиса для работы с корзиной покупателя.
 * Обеспечивает основную бизнес-логику управления корзиной:
 * - создание и получение корзины
 * - добавление и удаление товаров
 * - изменение количества товаров
 * - бронирование товаров на складе
 * - деактивация корзины
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private static final String GETTING_CART = "Получение корзины для пользователя {}";
    private static final String ADDING_PRODUCTS = "Добавление товаров в корзину пользователя {}: {}";
    private static final String REMOVING_PRODUCTS = "Удаление товаров из корзины пользователя {}: {}";
    private static final String CHANGING_QUANTITY = "Изменение количества товара для пользователя {}: {}";
    private static final String BOOKING_PRODUCTS = "Бронирование товаров для пользователя {}";
    private static final String DEACTIVATING_CART = "Деактивация корзины пользователя {}";
    private static final String CART_NOT_FOUND = "Активная корзина не найдена для пользователя {}";
    private static final String ERROR_WAREHOUSE = "Ошибка при проверке наличия товара на складе: {}";

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ru.yandex.practicum.store.client.ApiApi storeClient;
    private final ru.yandex.practicum.warehouse.client.ApiApi warehouseClient;

    /**
     * Получает активную корзину пользователя или создает новую, если её нет
     *
     * @param username имя пользователя
     * @return DTO корзины
     * @throws NotAuthorizedUserException если имя пользователя пустое
     */
    @Override
    @Transactional
    public ShoppingCartDto getCart(String username) {
        log.debug(GETTING_CART, username);
        validateUsername(username);
        Cart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> createNewCart(username));
        return cartMapper.toDto(cart);
    }

    /**
     * Добавляет товары в корзину пользователя
     *
     * @param username имя пользователя
     * @param products карта товаров (UUID -> количество)
     * @return обновленная корзина
     * @throws NotAuthorizedUserException если имя пользователя пустое
     * @throws ProductInShoppingCartNotInWarehouseException если товара нет на складе
     */
    @Override
    @Transactional
    public ShoppingCartDto addProducts(String username, Map<String, Long> products) {
        log.debug(ADDING_PRODUCTS, username, products);
        validateUsername(username);
        Cart cart = getOrCreateCart(username);

        products.forEach((productIdStr, quantity) -> {
            try {
                UUID productId = UUID.fromString(productIdStr); // Конвертация строки в UUID
                verifyProductAvailability(productId, cart, quantity);
                addProductToCart(cart, productId, quantity);
            } catch (IllegalArgumentException e) {
                throw new ProductInShoppingCartNotInWarehouseException("Invalid product ID format");
            }
        });

        return cartMapper.toDto(cartRepository.save(cart));
    }

    /**
     * Удаляет товары из корзины пользователя
     *
     * @param username имя пользователя
     * @param products карта товаров для удаления
     * @return обновленная корзина
     * @throws NotAuthorizedUserException если имя пользователя пустое
     * @throws NoProductsInShoppingCartException если корзина пуста
     */
    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String username, Map<String, Long> products) {
        log.debug(REMOVING_PRODUCTS, username, products);
        validateUsername(username);
        Cart cart = findActiveCart(username);

        products.keySet().forEach(productId ->
                removeProductFromCart(cart, UUID.fromString(productId)));

        if (cart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException();
        }

        return cartMapper.toDto(cartRepository.save(cart));
    }

    /**
     * Изменяет количество товара в корзине
     *
     * @param username имя пользователя
     * @param request запрос на изменение количества
     * @return информация об обновленном товаре
     * @throws NotAuthorizedUserException если имя пользователя пустое
     * @throws NoProductsInShoppingCartException если товар не найден в корзине
     */
    @Override
    @Transactional
    public ProductDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.debug(CHANGING_QUANTITY, username, request);
        validateUsername(username);
        Cart cart = findActiveCart(username);

        // Используем UUID из запроса напрямую
        verifyProductAvailability(request.getProductId(), cart, request.getNewQuantity());

        updateProductQuantity(cart, request.getProductId(), request.getNewQuantity());
        cartRepository.save(cart);

        return storeClient.getProduct(request.getProductId()).getBody(); // Конвертация в строку для клиента
    }

    /**
     * Бронирует товары из корзины на складе
     *
     * @param username имя пользователя
     * @return информация о забронированных товарах
     * @throws NotAuthorizedUserException если имя пользователя пустое
     * @throws ProductInShoppingCartNotInWarehouseException если товара нет на складе
     */
    @Override
    @Transactional
    public BookedProductsDto bookProducts(String username) {
        log.debug(BOOKING_PRODUCTS, username);
        validateUsername(username);
        Cart cart = findActiveCart(username);

        if (cart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException();
        }

        ShoppingCartDto cartDto = cartMapper.toDto(cart);
        return warehouseClient.checkProductQuantityEnoughForShoppingCart(cartDto).getBody();
    }

    /**
     * Деактивирует корзину пользователя
     *
     * @param username имя пользователя
     * @throws NotAuthorizedUserException если имя пользователя пустое
     * @throws NoProductsInShoppingCartException если корзина не найдена
     */
    @Override
    @Transactional
    public void deactivateCart(String username) {
        log.debug(DEACTIVATING_CART, username);
        validateUsername(username);
        Cart cart = findActiveCart(username);
        cart.setActive(false);
        cartRepository.save(cart);
    }

    /**
     * Получает или создает новую корзину для пользователя
     */
    private Cart getOrCreateCart(String username) {
        return cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> createNewCart(username));
    }

    /**
     * Создает новую корзину для пользователя
     */
    private Cart createNewCart(String username) {
        Cart cart = Cart.builder()
                .username(username)
                .active(true)
                .products(new HashSet<>())
                .build();
        return cartRepository.save(cart);
    }

    /**
     * Находит активную корзину пользователя
     * @throws NoProductsInShoppingCartException если корзина не найдена
     */
    private Cart findActiveCart(String username) {
        return cartRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> {
                    log.error(CART_NOT_FOUND, username);
                    return new NoProductsInShoppingCartException();
                });
    }

    /**
     * Проверяет валидность имени пользователя
     * @throws NotAuthorizedUserException если имя пустое
     */
    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException();
        }
    }

    /**
     * Проверяет наличие товара в магазине и на складе
     * @throws ProductInShoppingCartNotInWarehouseException если товара нет на складе
     */
    private void verifyProductAvailability(UUID productId, Cart cart, Long quantity) {
        // Сохраняем корзину, если она новая
        if (cart.getId() == null) {
            cart = cartRepository.save(cart);
        }

        ProductDto product = storeClient.getProduct(productId).getBody();
        if (product.getQuantityState() == QuantityState.ENDED) {
            throw new ProductInShoppingCartNotInWarehouseException();
        }

        ShoppingCartDto tempCart = new ShoppingCartDto()
                .shoppingCartId(cart.getId())
                .products(Map.of(productId.toString(), quantity));

        try {
            warehouseClient.checkProductQuantityEnoughForShoppingCart(tempCart);
        } catch (Exception e) {
            log.error(ERROR_WAREHOUSE, e.getMessage());
            throw new ProductInShoppingCartNotInWarehouseException();
        }
    }

    /**
     * Добавляет товар в корзину
     */
    private void addProductToCart(Cart cart, UUID productId, Long quantity) {
        CartProduct cartProduct = CartProduct.builder()
                .id(new CartProductId(cart.getId(), productId))
                .cart(cart)
                .quantity(quantity)
                .build();
        cart.getProducts().add(cartProduct);
    }

    /**
     * Удаляет товар из корзины
     */
    private void removeProductFromCart(Cart cart, UUID productId) {
        cart.getProducts().removeIf(item ->
                item.getId().getProductId().equals(productId));
    }

    /**
     * Обновляет количество товара в корзине
     * @throws NoProductsInShoppingCartException если товар не найден
     */
    private void updateProductQuantity(Cart cart, UUID productId, Long newQuantity) {
        cart.getProducts().stream()
                .filter(item -> item.getId().getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        p -> p.setQuantity(newQuantity),
                        () -> { throw new NoProductsInShoppingCartException(); }
                );
    }
}