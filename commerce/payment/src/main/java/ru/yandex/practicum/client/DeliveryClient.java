package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.delivery.api.DeliveryGatewayApi;

@FeignClient(name = "delivery", path = "/api/v${api.delivery-version}")
public interface DeliveryClient extends DeliveryGatewayApi {
}
