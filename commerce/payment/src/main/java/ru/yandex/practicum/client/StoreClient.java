package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "shopping-store")
public interface StoreClient extends ru.yandex.practicum.store.client.ApiApi {
}
