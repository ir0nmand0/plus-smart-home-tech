package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.NoSpecifiedProductInWarehouseExceptionDto;

/**
 * Исключение для случаев, когда товар отсутствует на складе
 */
@Getter
public class ProductNotInWarehouseException extends RuntimeException {
    private final NoSpecifiedProductInWarehouseExceptionDto exceptionDto;
    private static final String MESSAGE = "Товар отсутствует на складе";

    public ProductNotInWarehouseException() {
        super(MESSAGE);
        this.exceptionDto = new NoSpecifiedProductInWarehouseExceptionDto()
                .message(MESSAGE)
                .status(NoSpecifiedProductInWarehouseExceptionDto.StatusEnum.BAD_REQUEST);
    }
}