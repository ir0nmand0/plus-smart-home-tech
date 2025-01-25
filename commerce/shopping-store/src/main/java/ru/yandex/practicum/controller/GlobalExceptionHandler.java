package ru.yandex.practicum.controller;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.common.model.ProductNotFoundExceptionDto;
import ru.yandex.practicum.common.model.SpecifiedProductAlreadyInWarehouseExceptionDto;
import ru.yandex.practicum.common.model.NoSpecifiedProductInWarehouseExceptionDto;
import ru.yandex.practicum.exception.ApiErrorResponse;
import ru.yandex.practicum.exception.ProductAlreadyExistsInWarehouseException;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.ProductNotInWarehouseException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String PRODUCT_NOT_FOUND = "Товар не найден: {}";
    private static final String PRODUCT_EXISTS = "Товар уже существует на складе: {}";
    private static final String PRODUCT_NOT_IN_WAREHOUSE = "Товар отсутствует на складе: {}";
    private static final String VALIDATION_ERROR = "Ошибка валидации: {}";
    private static final String INTERNAL_ERROR = "Внутренняя ошибка сервера";
    private static final String USER_VALIDATION_ERROR = "Ошибка валидации данных";
    private static final String USER_INTERNAL_ERROR = "Произошла непредвиденная ошибка";
    private static final String WAREHOUSE_SERVICE_UNAVAILABLE = "Сервис склада недоступен";
    private static final String WAREHOUSE_TIMEOUT = "Превышено время ожидания ответа от сервиса склада";
    private static final String CIRCUIT_BREAKER_ERROR = "Сервис склада временно недоступен";

    /**
     * Обработка ошибки "Товар не найден"
     */
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProductNotFoundExceptionDto handleNotFound(ProductNotFoundException ex) {
        log.error(PRODUCT_NOT_FOUND, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Обработка ошибки "Товар уже существует на складе"
     */
    @ExceptionHandler(ProductAlreadyExistsInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SpecifiedProductAlreadyInWarehouseExceptionDto handleProductAlreadyExists(ProductAlreadyExistsInWarehouseException ex) {
        log.error(PRODUCT_EXISTS, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Обработка ошибки "Товар отсутствует на складе"
     */
    @ExceptionHandler(ProductNotInWarehouseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoSpecifiedProductInWarehouseExceptionDto handleProductNotInWarehouse(ProductNotInWarehouseException ex) {
        log.error(PRODUCT_NOT_IN_WAREHOUSE, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Обработка ошибок валидации данных
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationErrors(Exception ex) {
        log.error(VALIDATION_ERROR, ex.getMessage());
        String message = ex instanceof MethodArgumentNotValidException ?
                ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().get(0).getDefaultMessage() :
                ex.getMessage();

        return buildErrorResponse(message, USER_VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработка ошибки недоступности сервиса склада
     */
    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse handleServiceUnavailable(FeignException.ServiceUnavailable ex) {
        log.error(WAREHOUSE_SERVICE_UNAVAILABLE, ex);
        return buildErrorResponse(WAREHOUSE_SERVICE_UNAVAILABLE, USER_INTERNAL_ERROR, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Обработка таймаута при обращении к сервису склада
     */
    @ExceptionHandler(FeignException.GatewayTimeout.class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    public ApiErrorResponse handleGatewayTimeout(FeignException.GatewayTimeout ex) {
        log.error(WAREHOUSE_TIMEOUT, ex);
        return buildErrorResponse(WAREHOUSE_TIMEOUT, USER_INTERNAL_ERROR, HttpStatus.GATEWAY_TIMEOUT);
    }

    /**
     * Обработка срабатывания Circuit Breaker
     */
    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse handleCircuitBreaker(CallNotPermittedException ex) {
        log.error(CIRCUIT_BREAKER_ERROR, ex);
        return buildErrorResponse(CIRCUIT_BREAKER_ERROR, USER_INTERNAL_ERROR, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Обработка непредвиденных ошибок
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleAllUncaughtException(Exception ex) {
        log.error(INTERNAL_ERROR, ex);
        return buildErrorResponse(INTERNAL_ERROR, USER_INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Вспомогательный метод для создания ответа об ошибке
     */
    private ApiErrorResponse buildErrorResponse(String message, String userMessage, HttpStatus status) {
        return ApiErrorResponse.builder()
                .message(message)
                .userMessage(userMessage)
                .status(status.name())
                .statusCode(status.value())
                .build();
    }
}
