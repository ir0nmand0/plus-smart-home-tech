package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.entity.Action;

/**
 * Репозиторий для работы с сущностью Action.
 */
public interface ActionRepository extends JpaRepository<Action, Long> {
    // Дополнительные методы можно добавить по мере необходимости
}

