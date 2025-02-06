package ru.yandex.practicum.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.exception.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

/**
 * Глобальный обработчик исключений для сервиса доставки.
 * Преобразует доменные исключения в HTTP-ответы.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DELIVERY_ERROR = "Ошибка доставки: {}";
    private static final String CIRCUIT_BREAKER_ERROR = "Сервис временно недоступен: {}";

    /**
     * Обработка отсутствия доставки
     */
    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoDeliveryFoundExceptionDto handleDeliveryNotFound(NoDeliveryFoundException ex) {
        log.error(DELIVERY_ERROR, ex.getMessage());
        return new NoDeliveryFoundExceptionDto()
                .message(ex.getMessage())
                .httpStatus(HttpStatusEnum._404_NOT_FOUND);
    }

    /**
     * Обработка недоступности сервиса (Circuit Breaker)
     */
    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse handleCircuitBreaker(CallNotPermittedException ex) {
        log.error(CIRCUIT_BREAKER_ERROR, ex.getMessage());
        return ApiErrorResponse.builder()
                .message("Сервис временно недоступен")
                .userMessage("Пожалуйста, повторите попытку позже")
                .status(HttpStatus.SERVICE_UNAVAILABLE.name())
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .build();
    }
}

