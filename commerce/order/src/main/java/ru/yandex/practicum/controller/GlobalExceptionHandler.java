package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.common.model.*;
import ru.yandex.practicum.exception.*;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String ORDER_ERROR = "Ошибка заказа: {}";
    private static final String CIRCUIT_BREAKER_ERROR = "Сервис временно недоступен: {}";
    private static final String SERVICE_UNAVAILABLE_MESSAGE = "Сервис временно недоступен";
    private static final String RETRY_LATER_MESSAGE = "Пожалуйста, повторите попытку позже";

    @ExceptionHandler(NoProductsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NoProductsInShoppingCartExceptionDto handleNoProducts(NoProductsException ex) {
        log.error(ORDER_ERROR, ex.getMessage());
        return new NoProductsInShoppingCartExceptionDto(ex.getMessage(),
                NoProductsInShoppingCartExceptionDto.StatusEnum.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProductInShoppingCartNotInWarehouseDto handleProductNotInWarehouse(ProductNotInWarehouseException ex) {
        log.error(ORDER_ERROR, ex.getMessage());
        return new ProductInShoppingCartNotInWarehouseDto(ex.getMessage(),
                ProductInShoppingCartNotInWarehouseDto.StatusEnum.BAD_REQUEST);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public NotAuthorizedUserExceptionDto handleNotAuthorizedUser(NotAuthorizedUserException ex) {
        log.error(ORDER_ERROR, ex.getMessage());
        NotAuthorizedUserExceptionDto dto = new NotAuthorizedUserExceptionDto();
        dto.setMessage(ex.getMessage());
        dto.setHttpStatus(HttpStatusEnum._401_UNAUTHORIZED);
        return dto;
    }

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoOrderFoundExceptionDto handleOrderNotFound(NoOrderFoundException ex) {
        log.error(ORDER_ERROR, ex.getMessage());
        NoOrderFoundExceptionDto dto = new NoOrderFoundExceptionDto();
        dto.setMessage(ex.getMessage());
        dto.setHttpStatus(HttpStatusEnum._404_NOT_FOUND);
        return dto;
    }

    @ExceptionHandler(CallNotPermittedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse handleCircuitBreaker(CallNotPermittedException ex) {
        log.error(CIRCUIT_BREAKER_ERROR, ex.getMessage());
        return ApiErrorResponse.builder()
                .message(SERVICE_UNAVAILABLE_MESSAGE)
                .userMessage(RETRY_LATER_MESSAGE)
                .status(HttpStatus.SERVICE_UNAVAILABLE.name())
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .build();
    }
}