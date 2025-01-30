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

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "shoppingCartId", source = "id")
    @Mapping(target = "products", expression = "java(mapCartProducts(cart.getProducts()))")
    ShoppingCartDto toDto(Cart cart);

    default Map<String, Long> mapCartProducts(Set<CartProduct> products) {
        if (products == null) {
            return new HashMap<>();
        }
        return products.stream()
                .filter(p -> p != null && p.getId() != null && p.getId().getProductId() != null)
                .collect(Collectors.toMap(
                        p -> p.getId().getProductId().toString(),
                        CartProduct::getQuantity,
                        (v1, v2) -> v1, // В случае дубликатов берем первое значение
                        HashMap::new
                ));
    }
}
