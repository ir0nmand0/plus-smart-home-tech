package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.entity.Sensor;

/**
 * Репозиторий для работы с сущностью Sensor.
 */
public interface SensorRepository extends JpaRepository<Sensor, String> {

    /**
     * Проверяет, существует ли сенсор с указанным ID и принадлежит ли он заданному hubId.
     *
     * @param id    идентификатор сенсора.
     * @param hubId идентификатор хаба.
     * @return true, если сенсор существует и принадлежит хабу; иначе false.
     */
    boolean existsByIdAndHubId(String id, String hubId);
}

