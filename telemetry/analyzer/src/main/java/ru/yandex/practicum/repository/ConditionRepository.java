package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.entity.Condition;

/**
 * Репозиторий для работы с сущностью Condition.
 */
public interface ConditionRepository extends JpaRepository<Condition, Long> {
    // Дополнительные методы можно добавить по мере необходимости
}

