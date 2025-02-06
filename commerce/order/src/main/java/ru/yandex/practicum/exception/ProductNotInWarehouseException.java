package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class ProductNotInWarehouseException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Товар отсутствует на складе: %s";
    private final String message;

    public ProductNotInWarehouseException(String details) {
        super(String.format(MESSAGE_TEMPLATE, details));
        this.message = String.format(MESSAGE_TEMPLATE, details);
    }
}
