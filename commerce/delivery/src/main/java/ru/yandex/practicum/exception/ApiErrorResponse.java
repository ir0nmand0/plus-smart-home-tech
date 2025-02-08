package ru.yandex.practicum.exception;

import lombok.Builder;

/**
 * DTO для передачи информации об ошибке клиенту.
 */
@Builder
public record ApiErrorResponse(
        String message,
        String userMessage,
        String status,
        int statusCode
) {
}
