package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;
import ru.yandex.practicum.common.model.OrderDto;
import ru.yandex.practicum.entity.Order;
import ru.yandex.practicum.entity.OrderItem;
import ru.yandex.practicum.entity.OrderItemId;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Интерфейс для преобразования между DTO и Entity заказа
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    /**
     * Преобразует DTO в сущность Order
     */
    @Mapping(target = "items", source = "products")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "username", ignore = true)
    Order toEntity(OrderDto orderDto);

    /**
     * Преобразует сущность Order в DTO
     */
    @Mapping(target = "products", source = "items")
    OrderDto toDto(Order order);

    /**
     * Конвертирует Map с продуктами в Set OrderItem
     */
    default Set<OrderItem> mapProducts(Map<String, Long> products) {
        if (products == null) {
            return null;
        }

        return products.entrySet().stream()
                .map(entry -> {
                    OrderItem item = new OrderItem();
                    item.setId(new OrderItemId(null, UUID.fromString(entry.getKey())));
                    item.setQuantity(entry.getValue().intValue());
                    return item;
                })
                .collect(Collectors.toSet());
    }

    /**
     * Конвертирует Set OrderItem в Map с продуктами
     */
    default Map<String, Long> mapItems(Set<OrderItem> items) {
        if (items == null) {
            return null;
        }

        return items.stream()
                .collect(Collectors.toMap(
                        item -> item.getId().getProductId().toString(),
                        item -> item.getQuantity().longValue()
                ));
    }

    /**
     * Метод для преобразования JsonNullable<UUID> в UUID.
     * MapStruct автоматически использует этот метод при маппинге.
     */
    default UUID map(JsonNullable<UUID> value) {
        return value != null && value.isPresent() ? value.get() : null;
    }

    /**
     * Метод для преобразования UUID в JsonNullable<UUID>.
     * Добавлен для симметричности маппинга.
     */
    default JsonNullable<UUID> map(UUID value) {
        return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
    }
}
