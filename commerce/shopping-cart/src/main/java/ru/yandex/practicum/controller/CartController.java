package ru.yandex.practicum.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.service.CartService;
import ru.yandex.practicum.cart.api.ShoppingCartApi;

import java.util.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v${api.shopping-cart-version}/shopping-cart")
public class CartController implements ShoppingCartApi {

    private final CartService cartService;

    /**
     * PUT /api/v1/shopping-cart
     */
    @Override
    @PutMapping
    public ShoppingCartDto addProductToShoppingCart(@RequestParam String username,
            @RequestParam(required = false) List<Map<String, Long>> products,
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
        return cartService.addProducts(username, merged);
    }

    /**
     * GET /api/v1/shopping-cart
     */
    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("Get shopping cart for user '{}'", username);
        return cartService.getOrCreateCartDto(username);
    }

    /**
     * POST /api/v1/shopping-cart/change-quantity
     */
    @Override
    @PostMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeProductQuantity(@NotNull @Valid @RequestParam String username,
            @Valid @RequestBody(required = false) ChangeProductQuantityRequestDto changeProductQuantityRequest
    ) {
        log.info("Change product quantity for user '{}'. Body: {}", username, changeProductQuantityRequest);

        if (changeProductQuantityRequest == null) {
            return cartService.getOrCreateCartDto(username);
        }

        return cartService.changeQuantity(username, changeProductQuantityRequest);
    }

    /**
     * DELETE /api/v1/shopping-cart
     */
    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateCurrentShoppingCart(@RequestParam String username) {
        log.info("Deactivate cart for user '{}'", username);

        // Если корзины нет, не падаем, а просто ничего не делаем
        cartService.deactivateCart(username);
    }

    /**
     * POST /api/v1/shopping-cart
     * (booking)
     */
    @Override
    @PostMapping("/booking")
    public BookedProductsDto bookingProductsFromShoppingCart(@RequestParam String username) {
        log.info("Booking products for user '{}'", username);
        return cartService.bookProducts(username);
    }

    /**
     * POST /api/v1/shopping-cart/remove
     */
    @Override
    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto removeFromShoppingCart(@RequestParam String username,
            @RequestParam(required = false) List<Map<String, Long>> products,
            @RequestBody(required = false) Map<String, Long> requestBody
    ) {
        log.info("Remove products from cart for user '{}'. Body: {}", username, requestBody);

        if (requestBody == null) {
            requestBody = new HashMap<>();
        }
        Map<String, Long> merged = mergeMaps(requestBody, products);

        return cartService.removeProducts(username, merged);
    }

    /**
     * Вспомогательная функция для слияния JSON-тела и query-параметров
     */
    private Map<String, Long> mergeMaps(Map<String, Long> body, List<Map<String, Long>> query
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
