package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.NoSpecifiedProductInWarehouseExceptionDto;

/**
 * Исключение для случаев, когда товар отсутствует на складе.
 * HTTP‑статус теперь не задается в исключении – формирование ответа происходит в GlobalExceptionHandler.
 */
@Getter
public class ProductNotInWarehouseException extends RuntimeException {
    private final NoSpecifiedProductInWarehouseExceptionDto exceptionDto;

    public ProductNotInWarehouseException(String message) {
        super(message);
        // Создаем DTO только с сообщением, без установки статуса
        this.exceptionDto = new NoSpecifiedProductInWarehouseExceptionDto()
                .message(message);
    }
}
