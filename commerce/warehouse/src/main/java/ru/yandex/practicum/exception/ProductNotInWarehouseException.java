package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.NoSpecifiedProductInWarehouseExceptionDto;

@Getter
public class ProductNotInWarehouseException extends RuntimeException {
    private final NoSpecifiedProductInWarehouseExceptionDto exceptionDto;
    private static final String DEFAULT_MESSAGE = "Товар отсутствует на складе";

    public ProductNotInWarehouseException() {
        super(DEFAULT_MESSAGE);
        this.exceptionDto = new NoSpecifiedProductInWarehouseExceptionDto()
                .message(DEFAULT_MESSAGE);
    }
}
