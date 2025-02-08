package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.SpecifiedProductAlreadyInWarehouseExceptionDto;

@Getter
public class ProductAlreadyExistsInWarehouseException extends RuntimeException {
    private final SpecifiedProductAlreadyInWarehouseExceptionDto exceptionDto;
    private static final String DEFAULT_MESSAGE = "Товар уже существует на складе";

    public ProductAlreadyExistsInWarehouseException() {
        super(DEFAULT_MESSAGE);
        this.exceptionDto = new SpecifiedProductAlreadyInWarehouseExceptionDto()
                .message(DEFAULT_MESSAGE);
    }
}
