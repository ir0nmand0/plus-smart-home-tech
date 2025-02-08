package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.cart.api.ShoppingCartApi;

@FeignClient(name = "shopping-cart", path = "/api/v${api.shopping-cart-version}")
public interface CartClient extends ShoppingCartApi {
}
