package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.openapitools.jackson.nullable.JsonNullable;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.common.model.ProductDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "productId", source = "id", qualifiedByName = "toJsonNullable")
    @Mapping(target = "imageSrc", source = "imageSrc", qualifiedByName = "toJsonNullable")
    ProductDto toDto(Product product);

    @Mapping(target = "id", source = "productId", qualifiedByName = "fromJsonNullable")
    @Mapping(target = "imageSrc", source = "imageSrc", qualifiedByName = "fromJsonNullable")
    Product toEntity(ProductDto productDto);

    @Named("toJsonNullable")
    default <T> JsonNullable<T> toJsonNullable(T value) {
        return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
    }

    @Named("fromJsonNullable")
    default <T> T fromJsonNullable(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent() ? nullable.get() : null;
    }
}