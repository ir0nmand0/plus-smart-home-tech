package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.exception.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

/**
 * Глобальный обработчик исключений для сервиса оплаты.
 * Преобразует доменные исключения в HTTP-ответы.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PAYMENT_ERROR = "Ошибка платежа: {}";
    private static final String CIRCUIT_BREAKER_ERROR = "Сервис временно недоступен: {}";

    @ExceptionHandler(NoPaymentFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoPaymentFoundExceptionDto handlePaymentNotFound(NoPaymentFoundException ex) {
        log.error(PAYMENT_ERROR, ex.getMessage());
        return new NoPaymentFoundExceptionDto()
                .message(ex.getMessage())
                .httpStatus(HttpStatusEnum._404_NOT_FOUND);
    }

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NotEnoughInfoInOrderToCalculateExceptionDto handleNotEnoughInfo(NotEnoughInfoInOrderToCalculateException ex) {
        log.error(PAYMENT_ERROR, ex.getMessage());
        return new NotEnoughInfoInOrderToCalculateExceptionDto()
                .message(ex.getMessage())
                .httpStatus(HttpStatusEnum._400_BAD_REQUEST);
    }

    @ExceptionHandler(PaymentCalculationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handlePaymentCalculation(PaymentCalculationException ex) {
        log.error(PAYMENT_ERROR, ex.getMessage());
        return ApiErrorResponse.builder()
                .message(ex.getMessage())
                .userMessage("Произошла ошибка при расчете платежа")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

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