package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.entity.Address;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с адресами
 */
public interface AddressRepository extends JpaRepository<Address, UUID> {

    // Поиск адресов по городу
    List<Address> findByCity(String city);

    // Поиск адресов по городу и улице
    List<Address> findByCityAndStreet(String city, String street);

    // Поиск точного адреса
    Address findByCityAndStreetAndHouseAndFlat(
            String city,
            String street,
            String house,
            String flat
    );
}

