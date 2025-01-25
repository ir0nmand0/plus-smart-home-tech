package ru.yandex.practicum.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.common.model.NotAuthorizedUserExceptionDto;
import ru.yandex.practicum.common.model.NoProductsInShoppingCartExceptionDto;
import ru.yandex.practicum.common.model.ProductInShoppingCartNotInWarehouseDto;
import ru.yandex.practicum.exception.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String AUTH_ERROR = "Ошибка авторизации: {}";
    private static final String CART_ERROR = "Ошибка корзины: {}";
    private static final String WAREHOUSE_ERROR = "Ошибка склада: {}";
    private static final String VALIDATION_ERROR = "Ошибка валидации: {}";
    private static final String CIRCUIT_BREAKER_ERROR = "Сервис временно недоступен: {}";

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public NotAuthorizedUserExceptionDto handleNotAuthorized(NotAuthorizedUserException ex) {
        log.error(AUTH_ERROR, ex.getMessage());
        return ex.getExceptionDto();
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NoProductsInShoppingCartExceptionDto handleNoProducts(NoProductsInShoppingCartException ex) {
        log.error(CART_ERROR, ex.getMessage());
        return ex.getExceptionDto();
    }

    @ExceptionHandler(ProductInShoppingCartNotInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProductInShoppingCartNotInWarehouseDto handleProductNotInWarehouse(ProductInShoppingCartNotInWarehouseException ex) {
        log.error(WAREHOUSE_ERROR, ex.getMessage());
        return ex.getExceptionDto();
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