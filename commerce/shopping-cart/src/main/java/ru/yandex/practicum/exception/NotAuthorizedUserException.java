package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.NotAuthorizedUserExceptionDto;

/**
 * Исключение при отсутствии авторизации пользователя
 */
@Getter
public class NotAuthorizedUserException extends RuntimeException {
    private final NotAuthorizedUserExceptionDto exceptionDto;
    private static final String message = "User authentication required";

    public NotAuthorizedUserException() {
        super(message);
        this.exceptionDto = new NotAuthorizedUserExceptionDto()
                .message(message)
                .status(NotAuthorizedUserExceptionDto.StatusEnum.UNAUTHORIZED);
    }
}