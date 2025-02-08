package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class NotAuthorizedUserException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Неавторизованный пользователь: %s";
    private final String message;

    public NotAuthorizedUserException(String details) {
        super(String.format(MESSAGE_TEMPLATE, details));
        this.message = String.format(MESSAGE_TEMPLATE, details);
    }
}
