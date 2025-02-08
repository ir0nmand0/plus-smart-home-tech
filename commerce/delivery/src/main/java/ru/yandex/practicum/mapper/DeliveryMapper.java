package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.common.model.DeliveryDto;
import ru.yandex.practicum.entity.Address;
import ru.yandex.practicum.entity.Delivery;
import ru.yandex.practicum.common.model.AddressDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto toDto(Delivery delivery);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Delivery toEntity(DeliveryDto deliveryDto);

    AddressDto addressToAddressDto(Address address);

    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Address addressDtoToAddress(AddressDto addressDto);
}

