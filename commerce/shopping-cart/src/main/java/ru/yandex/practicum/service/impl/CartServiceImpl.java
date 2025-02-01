package ru.yandex.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.StoreClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.entity.Cart;
import ru.yandex.practicum.entity.CartProduct;
import ru.yandex.practicum.entity.CartProductId;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ProductInShoppingCartNotInWarehouseException;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.common.model.ProductDto;
import ru.yandex.practicum.common.model.QuantityState;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.repository.CartRepository;
import ru.yandex.practicum.service.CartService;

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
    private final StoreClient storeClient;
    private final WarehouseClient warehouseClient;

    /**
     * Получает активную корзину пользователя или создает новую, если её нет
     *
     * @param username имя пользователя
     * @return DTO корзины
     * @throws NotAuthorizedUserException если имя пользователя пустое
     */
    @Override
    @Transactional
    public ShoppingCartDto getOrCreateCartDto(String username) {
        log.info(GETTING_CART, username);
        validateUsername(username);

        // Ищем активную или создаём
        Cart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> createNewCart(username));

        return cartMapper.toDto(cart);
    }

    /**
     * Добавляет товары в корзину пользователя
     */
    @Override
    @Transactional
    public ShoppingCartDto addProducts(String username, Map<String, Long> products) {
        log.info(ADDING_PRODUCTS, username, products);
        validateUsername(username);

        Cart cart = getOrCreateCart(username);
        // Если пришел пустой map, просто вернём текущую корзину
        if (products.isEmpty()) {
            return cartMapper.toDto(cart);
        }

        products.forEach((productIdStr, quantity) -> {
            try {
                UUID productId = UUID.fromString(productIdStr);
                verifyProductAvailability(productId, cart, quantity);
                addProductToCart(cart, productId, quantity);
            } catch (IllegalArgumentException e) {
                throw new ProductInShoppingCartNotInWarehouseException("Invalid product ID format: " + productIdStr);
            }
        });

        return cartMapper.toDto(cartRepository.save(cart));
    }

    /**
     * Удаляет товары из корзины пользователя
     */
    @Override
    @Transactional
    public ShoppingCartDto removeProducts(String username, Map<String, Long> products) {
        log.info(REMOVING_PRODUCTS, username, products);
        validateUsername(username);

        // Если корзины нет, создадим новую (или вернём пустую)
        Cart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> createNewCart(username));

        if (products.isEmpty()) {
            // Нечего удалять, вернём как есть
            return cartMapper.toDto(cart);
        }

        // Удаляем
        products.keySet().forEach(productIdStr ->
                removeProductFromCart(cart, UUID.fromString(productIdStr))
        );

        return cartMapper.toDto(cartRepository.save(cart));
    }

    /**
     * Изменяет количество товара в корзине
     */
    @Override
    @Transactional
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.info(CHANGING_QUANTITY, username, request);
        validateUsername(username);

        // Ищем активную или создаём
        Cart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> createNewCart(username));

        // Если request=null или newQuantity=0, можно поменять или вернуть корзину
        // (Учтём, что проверка Store/Warehouse всё ещё срабатывает.)
        verifyProductAvailability(request.getProductId(), cart, request.getNewQuantity());
        updateProductQuantity(cart, request.getProductId(), request.getNewQuantity());
        cartRepository.save(cart);

        // Возвращаем всю корзину (тесты ждут ShoppingCartDto)
        return cartMapper.toDto(cart);
    }

    /**
     * Бронирование товаров
     */
    @Override
    @Transactional
    public BookedProductsDto bookProducts(String username) {
        log.info(BOOKING_PRODUCTS, username);
        validateUsername(username);

        // Ищем активную или создаём
        Cart cart = cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> createNewCart(username));

        // Если корзина пустая — в старом коде бросали NoProductsInShoppingCartException
        // Оставим как есть или уберём:
        if (cart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException();
        }

        ShoppingCartDto cartDto = cartMapper.toDto(cart);
        try {
            return warehouseClient.checkProductQuantityEnoughForShoppingCart(cartDto).getBody();
        } catch (Exception e) {
            log.error(ERROR_WAREHOUSE, e.getMessage());
            throw new ProductInShoppingCartNotInWarehouseException();
        }
    }

    /**
     * Деактивация корзины
     */
    @Override
    @Transactional
    public void deactivateCart(String username) {
        log.info(DEACTIVATING_CART, username);
        validateUsername(username);

        Optional<Cart> cartOpt = cartRepository.findByUsernameAndActiveTrue(username);
        if (cartOpt.isEmpty()) {
            return;
        }

        Cart cart = cartOpt.get();
        cart.setActive(false);
        cartRepository.save(cart);
    }

    // ================= ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =================

    /**
     * Проверка имени пользователя
     */
    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException();
        }
    }

    /**
     * Получить или создать новую корзину
     */
    private Cart getOrCreateCart(String username) {
        return cartRepository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> createNewCart(username));
    }

    /**
     * Создание новой корзины
     */
    private Cart createNewCart(String username) {
        Cart newCart = Cart.builder()
                .username(username)
                .active(true)
                .products(new HashSet<>())
                .build();
        return cartRepository.save(newCart);
    }

    /**
     * Проверяем наличие товара через Store + Warehouse
     */
    private void verifyProductAvailability(UUID productId, Cart cart, Long quantity) {
        // Если корзина ещё не в БД
        if (cart.getId() == null) {
            cartRepository.save(cart);
        }
        // 1) Проверяем наличие в магазине
        ProductDto product = storeClient.getProduct(productId).getBody();
        if (product == null) {
            throw new ProductInShoppingCartNotInWarehouseException(
                    "Product with ID " + productId + " not found in store (empty body)"
            );
        }
        if (product.getQuantityState() == QuantityState.ENDED) {
            throw new ProductInShoppingCartNotInWarehouseException(
                    "Product with ID " + productId + " ended in store"
            );
        }

        // 2) Проверяем склад
        ShoppingCartDto tempCart = new ShoppingCartDto()
                .shoppingCartId(cart.getId())
                .products(Map.of(productId.toString(), quantity));

        try {
            warehouseClient.checkProductQuantityEnoughForShoppingCart(tempCart).getBody();
        } catch (Exception e) {
            log.error(ERROR_WAREHOUSE, e);
            throw new ProductInShoppingCartNotInWarehouseException(
                    "No enough products in warehouse for " + productId
            );
        }
    }

    /**
     * Добавить продукт в корзину
     */
    private void addProductToCart(Cart cart, UUID productId, Long quantity) {
        CartProduct cp = CartProduct.builder()
                .id(new CartProductId(cart.getId(), productId))
                .cart(cart)
                .quantity(quantity)
                .build();
        cart.getProducts().add(cp);
    }

    /**
     * Удалить продукт из корзины
     */
    private void removeProductFromCart(Cart cart, UUID productId) {
        cart.getProducts().removeIf(cp -> cp.getId().getProductId().equals(productId));
    }

    /**
     * Обновить количество существующего товара
     */
    private void updateProductQuantity(Cart cart, UUID productId, Long newQuantity) {
        cart.getProducts().stream()
                .filter(cp -> cp.getId().getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        cp -> cp.setQuantity(newQuantity),
                        // Если нет товара — возможно, в старом коде кидали NoProductsInShoppingCartException
                        () -> { throw new NoProductsInShoppingCartException(); }
                );
    }
}
