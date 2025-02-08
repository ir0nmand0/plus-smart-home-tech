package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.common.model.DimensionDto;
import ru.yandex.practicum.entity.Dimension;

/**
 * Маппер для преобразования между DTO и сущностями склада
 */
@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {

    @Mapping(target = "width", source = "width")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "depth", source = "depth")
    Dimension toDimensionEntity(DimensionDto dimensionDto);

    @Mapping(target = "width", source = "width")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "depth", source = "depth")
    DimensionDto toDimensionDto(Dimension dimension);

    default Double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }
}