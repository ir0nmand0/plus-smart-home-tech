package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.warehouse.api.WarehouseApi;

@FeignClient(name = "warehouse", path = "/api/v${api.warehouse-version}")
public interface WarehouseClient extends WarehouseApi {
}
