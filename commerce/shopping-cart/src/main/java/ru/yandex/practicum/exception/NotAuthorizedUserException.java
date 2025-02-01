package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.NotAuthorizedUserExceptionDto;

/**
 * Исключение при отсутствии авторизации пользователя.
 */
@Getter
public class NotAuthorizedUserException extends RuntimeException {
    private final NotAuthorizedUserExceptionDto exceptionDto;
    private static final String DEFAULT_MESSAGE = "User authentication required";

    public NotAuthorizedUserException() {
        super(DEFAULT_MESSAGE);
        this.exceptionDto = new NotAuthorizedUserExceptionDto()
                .message(DEFAULT_MESSAGE);
    }
}
