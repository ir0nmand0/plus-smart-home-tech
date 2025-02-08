package ru.yandex.practicum.service;

import ru.yandex.practicum.common.model.BookedProductsDto;
import ru.yandex.practicum.common.model.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.common.model.ShoppingCartDto;
import java.util.Map;

public interface CartService {
    ShoppingCartDto getOrCreateCartDto(String username);
    ShoppingCartDto addProducts(String username, Map<String, Long> products);
    ShoppingCartDto removeProducts(String username, Map<String, Long> products);
    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequestDto request);
    BookedProductsDto bookProducts(String username);
    void deactivateCart(String username);
}