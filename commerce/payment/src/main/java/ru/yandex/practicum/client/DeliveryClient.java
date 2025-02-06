package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "delivery")
public interface DeliveryClient extends ru.yandex.practicum.delivery.client.ApiApi {
}
