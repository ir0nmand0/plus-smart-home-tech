package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.NoSpecifiedProductInWarehouseExceptionDto;

/**
 * Исключение для случаев, когда товар отсутствует на складе.
 */
@Getter
public class ProductNotInWarehouseException extends RuntimeException {
    private final NoSpecifiedProductInWarehouseExceptionDto exceptionDto;

    public ProductNotInWarehouseException(NoSpecifiedProductInWarehouseExceptionDto exceptionDto) {
        super(exceptionDto.getMessage());
        this.exceptionDto = exceptionDto;
    }
}
