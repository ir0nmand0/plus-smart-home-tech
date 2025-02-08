package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.store.api.StorefrontApi;

@FeignClient(name = "shopping-store", path = "/api/v${api.shopping-store-version}")
public interface StoreClient extends StorefrontApi {
}
