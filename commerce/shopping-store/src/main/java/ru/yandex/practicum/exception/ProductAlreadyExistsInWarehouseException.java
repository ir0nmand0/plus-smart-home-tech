package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.SpecifiedProductAlreadyInWarehouseExceptionDto;
/**
 * Исключение для случаев, когда товар уже существует на складе.
 */
@Getter
public class ProductAlreadyExistsInWarehouseException extends RuntimeException {
    private final SpecifiedProductAlreadyInWarehouseExceptionDto exceptionDto;

    public ProductAlreadyExistsInWarehouseException(SpecifiedProductAlreadyInWarehouseExceptionDto exceptionDto) {
        super(exceptionDto.getMessage());
        this.exceptionDto = exceptionDto;
    }
}