package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.entity.ScenarioAction;
import ru.yandex.practicum.model.entity.ScenarioActionId;

import java.util.List;

/**
 * Репозиторий для работы с действиями сценариев.
 */
public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {

    /**
     * Находит все действия для списка ID сценариев.
     *
     * @param ids список идентификаторов сценариев.
     * @return список действий.
     */
    List<ScenarioAction> findAllByScenarioIdIn(List<Long> ids);
}
