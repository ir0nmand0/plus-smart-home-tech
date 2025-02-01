package ru.yandex.practicum.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.*;
import ru.yandex.practicum.common.model.NotAuthorizedUserExceptionDto;
import ru.yandex.practicum.common.model.NoProductsInShoppingCartExceptionDto;
import ru.yandex.practicum.common.model.ProductInShoppingCartNotInWarehouseDto;
import ru.yandex.practicum.exception.ApiErrorResponse;

/**
 * Глобальный обработчик исключений для сервиса корзины.
 * Здесь все доменные исключения преобразуются в соответствующие HTTP-ответы.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Константы для логирования ошибок
    private static final String AUTH_ERROR = "Ошибка авторизации: {}";
    private static final String CART_ERROR = "Ошибка корзины: {}";
    private static final String WAREHOUSE_ERROR = "Ошибка склада: {}";
    private static final String CIRCUIT_BREAKER_ERROR = "Сервис временно недоступен: {}";

    /**
     * Обработка исключения NotAuthorizedUserException.
     * Возвращает DTO с информацией об ошибке авторизации.
     *
     * @param ex исключение NotAuthorizedUserException
     * @return NotAuthorizedUserExceptionDto с сообщением об ошибке
     */
    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public NotAuthorizedUserExceptionDto handleNotAuthorized(NotAuthorizedUserException ex) {
        log.error(AUTH_ERROR, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Обработка исключения NoProductsInShoppingCartException.
     * Возвращает DTO с информацией об ошибке, когда в корзине нет товаров.
     *
     * @param ex исключение NoProductsInShoppingCartException
     * @return NoProductsInShoppingCartExceptionDto с сообщением об ошибке
     */
    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NoProductsInShoppingCartExceptionDto handleNoProducts(NoProductsInShoppingCartException ex) {
        log.error(CART_ERROR, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Обработка исключения ProductInShoppingCartNotInWarehouseException.
     * Возвращает DTO с информацией об ошибке, когда товар из корзины отсутствует на складе.
     *
     * @param ex исключение ProductInShoppingCartNotInWarehouseException
     * @return ProductInShoppingCartNotInWarehouseDto с сообщением об ошибке
     */
    @ExceptionHandler(ProductInShoppingCartNotInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProductInShoppingCartNotInWarehouseDto handleProductNotInWarehouse(ProductInShoppingCartNotInWarehouseException ex) {
        log.error(WAREHOUSE_ERROR, ex.getMessage());
        return ex.getExceptionDto();
    }

    /**
     * Обработка исключения, когда срабатывает Circuit Breaker.
     * Возвращает ApiErrorResponse с информацией о недоступности сервиса.
     *
     * @param ex исключение CallNotPermittedException
     * @return ApiErrorResponse с сообщением и HTTP статусом SERVICE_UNAVAILABLE
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
