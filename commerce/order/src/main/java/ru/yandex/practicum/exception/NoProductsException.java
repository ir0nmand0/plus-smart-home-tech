package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class NoProductsException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Товары не найдены в корзине: %s";
    private final String message;

    public NoProductsException(String details) {
        super(String.format(MESSAGE_TEMPLATE, details));
        this.message = String.format(MESSAGE_TEMPLATE, details);
    }
}


