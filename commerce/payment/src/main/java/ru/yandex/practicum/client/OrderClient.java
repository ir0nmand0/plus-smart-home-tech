package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order")
public interface OrderClient extends ru.yandex.practicum.order.client.ApiApi {
}
