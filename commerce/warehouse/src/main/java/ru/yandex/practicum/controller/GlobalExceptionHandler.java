package ru.yandex.practicum.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.*;
import ru.yandex.practicum.common.model.SpecifiedProductAlreadyInWarehouseExceptionDto;
import ru.yandex.practicum.common.model.NoSpecifiedProductInWarehouseExceptionDto;
import ru.yandex.practicum.common.model.ProductInShoppingCartLowQuantityInWarehouseDto;

/**
 * Глобальный обработчик исключений для warehouse сервиса
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Сообщения для логирования
    private static final String PRODUCT_EXISTS = "Товар уже существует на складе: {}";
    private static final String PRODUCT_NOT_FOUND = "Товар не найден на складе: {}";
    private static final String INSUFFICIENT_QUANTITY = "Недостаточное количество товара: {}";
    private static final String VALIDATION_ERROR = "Ошибка валидации: {}";
    private static final String INTERNAL_ERROR = "Непредвиденная ошибка: ";

    // Сообщения для пользователя
    private static final String VALIDATION_MESSAGE = "Ошибка валидации данных";
    private static final String INTERNAL_ERROR_MESSAGE = "Внутренняя ошибка сервера";
    private static final String INTERNAL_ERROR_USER_MESSAGE = "Произошла непредвиденная ошибка";

    /**
     * Товар уже существует на складе -> код 400
     */
    @ExceptionHandler(ProductAlreadyExistsInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SpecifiedProductAlreadyInWarehouseExceptionDto handleProductAlreadyExists(ProductAlreadyExistsInWarehouseException ex) {
        log.error(PRODUCT_EXISTS, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Товар не найден на складе -> код 404
     */
    @ExceptionHandler(ProductNotInWarehouseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoSpecifiedProductInWarehouseExceptionDto handleProductNotFound(ProductNotInWarehouseException ex) {
        log.error(PRODUCT_NOT_FOUND, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Недостаточное количество товара -> код 400
     */
    @ExceptionHandler(InsufficientProductQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProductInShoppingCartLowQuantityInWarehouseDto handleInsufficientQuantity(InsufficientProductQuantityException ex) {
        log.error(INSUFFICIENT_QUANTITY, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Ошибки @Valid/@Validated -> код 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error(VALIDATION_ERROR, ex.getMessage());
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ApiErrorResponse.builder()
                .message(message)
                .userMessage(VALIDATION_MESSAGE)
                .status(HttpStatus.BAD_REQUEST.name())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    /**
     * Ошибки ConstraintValidator -> код 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        log.error(VALIDATION_ERROR, ex.getMessage());
        return ApiErrorResponse.builder()
                .message(ex.getMessage())
                .userMessage(VALIDATION_MESSAGE)
                .status(HttpStatus.BAD_REQUEST.name())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    /**
     * Непредвиденные ошибки -> код 500
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleAllUncaughtException(Exception ex) {
        log.error(INTERNAL_ERROR, ex);
        return ApiErrorResponse.builder()
                .message(INTERNAL_ERROR_MESSAGE)
                .userMessage(INTERNAL_ERROR_USER_MESSAGE)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }
}