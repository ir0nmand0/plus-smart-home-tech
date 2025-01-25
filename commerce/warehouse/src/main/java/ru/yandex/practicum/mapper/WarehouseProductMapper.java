package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.common.model.DimensionDto;
import ru.yandex.practicum.entity.Dimension;

/**
 * Маппер для преобразования между DTO и сущностями склада
 */
@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {

    /**
     * Преобразование DTO размеров в сущность
     */
    Dimension toDimensionEntity(DimensionDto dimensionDto);

    /**
     * Преобразование сущности размеров в DTO
     */
    DimensionDto toDimensionDto(Dimension dimension);
}