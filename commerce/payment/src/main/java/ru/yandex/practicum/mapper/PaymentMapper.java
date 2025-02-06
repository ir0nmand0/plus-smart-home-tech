package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.common.model.PaymentDto;
import ru.yandex.practicum.entity.Payment;
import ru.yandex.practicum.entity.PaymentHistory;

/**
 * Интерфейс для преобразования между DTO и Entity сервиса оплаты
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    /**
     * Преобразует сущность Payment в DTO
     */
    @Mapping(target = "feeTotal", source = "taxTotal")
    PaymentDto toDto(Payment payment);

    /**
     * Преобразует DTO в сущность Payment
     */
    @Mapping(target = "taxTotal", source = "feeTotal")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "paymentDetails", ignore = true)
    Payment toEntity(PaymentDto paymentDto);
}