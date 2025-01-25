package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.entity.Cart;
import ru.yandex.practicum.entity.CartProduct;
import ru.yandex.practicum.common.model.ShoppingCartDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {})
public interface CartMapper {
    /**
     * Преобразование корзины в DTO
     * @param cart сущность корзины
     * @return DTO корзины
     */
    @Mapping(target = "shoppingCartId", source = "id")
    @Mapping(target = "products", expression = "java(mapCartProducts(cart.getProducts()))")
    ShoppingCartDto toDto(Cart cart);

    /**
     * Преобразование товаров корзины в Map
     * @param products список товаров
     * @return Map (ID товара -> количество)
     */
    default Map<String, Long> mapCartProducts(Set<CartProduct> products) {
        if (products == null) return Collections.emptyMap();
        return products.stream()
                .collect(Collectors.toMap(
                        p -> p.getId().getProductId().toString(), // Конвертация UUID в строку
                        CartProduct::getQuantity
                ));
    }
}
