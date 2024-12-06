package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.entity.ScenarioCondition;
import ru.yandex.practicum.model.entity.ScenarioConditionId;

import java.util.List;

/**
 * Репозиторий для работы с условиями сценариев.
 */
public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {

    /**
     * Находит все условия для списка ID сценариев.
     *
     * @param ids список идентификаторов сценариев.
     * @return список условий.
     */
    List<ScenarioCondition> findAllByScenarioIdIn(List<Long> ids);
}
