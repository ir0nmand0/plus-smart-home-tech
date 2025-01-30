package ru.yandex.practicum.service;

import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.common.model.ProductDto;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import java.util.Map;

public interface CartService {
    ShoppingCartDto getCart(String username);
    ShoppingCartDto addProducts(String username, Map<String, Long> products);
    ShoppingCartDto removeProducts(String username, Map<String, Long> products);
    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);
    BookedProductsDto bookProducts(String username);
    void deactivateCart(String username);
}