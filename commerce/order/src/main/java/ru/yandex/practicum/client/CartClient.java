package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-cart")
public interface CartClient extends ru.yandex.practicum.shoppingcart.client.ApiApi {
}
