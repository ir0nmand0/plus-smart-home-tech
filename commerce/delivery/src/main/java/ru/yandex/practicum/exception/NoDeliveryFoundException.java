package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class NoDeliveryFoundException extends RuntimeException {
    private final String message;

    public NoDeliveryFoundException(String message) {
        super(message);
        this.message = message;
    }
}