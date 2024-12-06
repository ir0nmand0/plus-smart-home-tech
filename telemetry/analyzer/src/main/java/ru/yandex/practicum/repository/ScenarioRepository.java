package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.entity.Scenario;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Scenario.
 */
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    /**
     * Находит все сценарии, связанные с указанным hubId.
     *
     * @param hubId идентификатор хаба.
     * @return список сценариев.
     */
    List<Scenario> findByHubId(String hubId);

    /**
     * Находит сценарий по hubId и имени.
     *
     * @param hubId идентификатор хаба.
     * @param name  имя сценария.
     * @return опциональный сценарий.
     */
    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    /**
     * Возвращает все сценарии, связанные с заданным hubId.
     *
     * @param hubId идентификатор хаба.
     * @return список всех сценариев.
     */
    List<Scenario> findAllByHubId(String hubId);
}

