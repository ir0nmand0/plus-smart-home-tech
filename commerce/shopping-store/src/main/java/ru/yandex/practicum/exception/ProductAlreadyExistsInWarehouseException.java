package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.SpecifiedProductAlreadyInWarehouseExceptionDto;

/**
 * Исключение для случаев, когда товар уже существует на складе.
 * Теперь HTTP‑статус не задаётся внутри исключения – это делается в GlobalExceptionHandler.
 */
@Getter
public class ProductAlreadyExistsInWarehouseException extends RuntimeException {
    private final SpecifiedProductAlreadyInWarehouseExceptionDto exceptionDto;

    public ProductAlreadyExistsInWarehouseException(String message) {
        super(message);
        // Создаем DTO только с сообщением, без статуса
        this.exceptionDto = new SpecifiedProductAlreadyInWarehouseExceptionDto()
                .message(message);
    }
}
