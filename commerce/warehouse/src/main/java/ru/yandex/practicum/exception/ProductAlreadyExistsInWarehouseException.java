package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.SpecifiedProductAlreadyInWarehouseExceptionDto;

/**
 * Исключение при попытке добавить уже существующий на складе товар
 */
@Getter
public class ProductAlreadyExistsInWarehouseException extends RuntimeException {
    private final SpecifiedProductAlreadyInWarehouseExceptionDto exceptionDto;
    private static final String MESSAGE = "Товар уже существует на складе";

    public ProductAlreadyExistsInWarehouseException() {
        super(MESSAGE);
        this.exceptionDto = new SpecifiedProductAlreadyInWarehouseExceptionDto()
                .message(MESSAGE)
                .status(SpecifiedProductAlreadyInWarehouseExceptionDto.StatusEnum.BAD_REQUEST);
    }
}