package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.order.api.OrderProcessorApi;

@FeignClient(name = "order", path = "/api/v${api.order-version}")
public interface OrderClient extends OrderProcessorApi {
}
