package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.shoppingcart.api.DefaultApi;

import java.util.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class CartController implements DefaultApi {

    private final CartService cartService;

    /**
     * PUT /api/v1/shopping-cart
     */
    @Override
    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "products", required = false) List<Map<String, Long>> products,
            @RequestBody(required = false) Map<String, Long> requestBody
    ) {
        log.info("Add product to cart for user '{}'. Body: {}", username, requestBody);

        // Если body == null, подменяем на пустой Map
        if (requestBody == null) {
            requestBody = new HashMap<>();
        }
        // Сливаем данные: если что-то пришло в query
        Map<String, Long> merged = mergeMaps(requestBody, products);

        // Вызываем сервис-метод
        ShoppingCartDto cartDto = cartService.addProducts(username, merged);

        // Возвращаем 200 OK (или 201, как пожелаете)
        return ResponseEntity.ok(cartDto);
    }

    /**
     * GET /api/v1/shopping-cart
     */
    @Override
    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(
            @RequestParam(value = "username", required = true) String username
    ) {
        log.info("Get shopping cart for user '{}'", username);
        ShoppingCartDto cartDto = cartService.getCart(username);
        return ResponseEntity.ok(cartDto);
    }

    /**
     * POST /api/v1/shopping-cart/change-quantity
     */
    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam(value = "username", required = true) String username,
            ChangeProductQuantityRequest ignoredQueryParam, // часто не используется
            @RequestBody(required = false) ChangeProductQuantityRequest changeProductQuantityRequest
    ) {
        log.info("Change product quantity for user '{}'. Body: {}", username, changeProductQuantityRequest);

        // Если тело отсутствует
        if (changeProductQuantityRequest == null) {
            // Вернём текущую корзину
            return ResponseEntity.ok(cartService.getCart(username));
        }

        // Иначе, меняем количество
        ShoppingCartDto updated = cartService.changeQuantity(username, changeProductQuantityRequest);
        // Тесты хотят 2xx => 200
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/v1/shopping-cart
     */
    @Override
    @DeleteMapping
    public ResponseEntity<Void> deactivateCurrentShoppingCart(
            @RequestParam(value = "username", required = true) String username
    ) {
        log.info("Deactivate cart for user '{}'", username);

        // Если корзины нет, не падаем, а просто ничего не делаем
        cartService.deactivateCart(username);

        // Тесты хотят [200,201,203,204], выберем 204
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/shopping-cart
     * (booking)
     */
    @Override
    @PostMapping
    public ResponseEntity<BookedProductsDto> bookingProductsFromShoppingCart(
            @RequestParam(value = "username", required = true) String username
    ) {
        log.info("Booking products for user '{}'", username);
        BookedProductsDto booked = cartService.bookProducts(username);
        return ResponseEntity.ok(booked);
    }

    /**
     * POST /api/v1/shopping-cart/remove
     */
    @Override
    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "products", required = false) List<Map<String, Long>> products,
            @RequestBody(required = false) Map<String, Long> requestBody
    ) {
        log.info("Remove products from cart for user '{}'. Body: {}", username, requestBody);

        if (requestBody == null) {
            requestBody = new HashMap<>();
        }
        Map<String, Long> merged = mergeMaps(requestBody, products);

        ShoppingCartDto cartDto = cartService.removeProducts(username, merged);
        return ResponseEntity.ok(cartDto);
    }

    /**
     * Вспомогательная функция для слияния JSON-тела и query-параметров
     */
    private Map<String, Long> mergeMaps(
            Map<String, Long> body,
            List<Map<String, Long>> query
    ) {
        Map<String, Long> merged = new HashMap<>(body);
        if (query != null) {
            for (Map<String, Long> mp : query) {
                mp.forEach((key, value) -> merged.merge(key, value, Long::sum));
            }
        }
        return merged;
    }
}
