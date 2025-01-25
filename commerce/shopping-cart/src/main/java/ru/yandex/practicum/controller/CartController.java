package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.common.model.ProductDto;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.shoppingcart.api.DefaultApi;

import java.util.List;
import java.util.Map;

/**
 * Контроллер для работы с корзиной покупателя
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class CartController implements DefaultApi {
    private static final String MSG_USERNAME_EMPTY = "Username cannot be empty";
    private static final String MSG_PRODUCTS_EMPTY = "Products map cannot be empty";
    private static final String MSG_REQUEST_EMPTY = "Request cannot be empty";
    private static final String PATTERN_UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    private final CartService cartService;

    /**
     * Добавление товаров в корзину
     * @param username имя пользователя
     * @param products карта товаров для добавления
     * @param additionalProducts дополнительные товары (опционально)
     * @return обновленная корзина
     */
    @Override
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @NotBlank(message = MSG_USERNAME_EMPTY) final String username,
            @NotEmpty(message = MSG_PRODUCTS_EMPTY)
            @Valid final Map<@Pattern(regexp = PATTERN_UUID) String, @Positive Long> products,
            final List<Map<String, Long>> additionalProducts) {
        log.debug("Добавление товаров в корзину для пользователя {}: {}", username, products);
        return ResponseEntity.ok(cartService.addProducts(username, products));
    }

    /**
     * Бронирование товаров на складе
     * @param username имя пользователя
     * @return информация о бронировании
     */
    @Override
    public ResponseEntity<BookedProductsDto> bookingProductsFromShoppingCart(
            @NotBlank(message = MSG_USERNAME_EMPTY) final String username) {
        log.debug("Бронирование товаров для пользователя {}", username);
        return ResponseEntity.ok(cartService.bookProducts(username));
    }

    /**
     * Изменение количества товара в корзине
     * @param username имя пользователя
     * @param request запрос на изменение количества
     * @param additionalRequest дополнительный запрос (опционально)
     * @return обновленный товар
     */
    @Override
    public ResponseEntity<ProductDto> changeProductQuantity(
            @NotBlank(message = MSG_USERNAME_EMPTY) final String username,
            @Valid @NotNull(message = MSG_REQUEST_EMPTY) final ChangeProductQuantityRequest request,
            final ChangeProductQuantityRequest additionalRequest) {
        log.debug("Изменение количества товара {} для пользователя {}", request, username);
        return ResponseEntity.ok(cartService.changeQuantity(username, request));
    }

    /**
     * Деактивация корзины
     * @param username имя пользователя
     * @return пустой ответ с OK статусом
     */
    @Override
    public ResponseEntity<Void> deactivateCurrentShoppingCart(
            @NotBlank(message = MSG_USERNAME_EMPTY) final String username) {
        log.debug("Деактивация корзины пользователя {}", username);
        cartService.deactivateCart(username);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение корзины пользователя
     * @param username имя пользователя
     * @return текущая корзина
     */
    @Override
    public ResponseEntity<ShoppingCartDto> getShoppingCart(
            @NotBlank(message = MSG_USERNAME_EMPTY) final String username) {
        log.debug("Получение корзины пользователя {}", username);
        return ResponseEntity.ok(cartService.getCart(username));
    }

    /**
     * Удаление товаров из корзины
     * @param username имя пользователя
     * @param products карта товаров для удаления
     * @param additionalProducts дополнительные товары (опционально)
     * @return обновленная корзина
     */
    @Override
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @NotBlank(message = MSG_USERNAME_EMPTY) final String username,
            @NotEmpty(message = MSG_PRODUCTS_EMPTY)
            @Valid final Map<@Pattern(regexp = PATTERN_UUID) String, @Positive Long> products,
            final List<Map<String, Long>> additionalProducts) {
        log.debug("Удаление товаров из корзины пользователя {}: {}", username, products);
        return ResponseEntity.ok(cartService.removeProducts(username, products));
    }
}