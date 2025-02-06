package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class NoOrderFoundException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Заказ не найден: %s";
    private final String message;

    public NoOrderFoundException(String details) {
        super(String.format(MESSAGE_TEMPLATE, details));
        this.message = String.format(MESSAGE_TEMPLATE, details);
    }
}
