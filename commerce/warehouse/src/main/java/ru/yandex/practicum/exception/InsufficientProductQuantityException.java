package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.ProductInShoppingCartLowQuantityInWarehouseDto;

@Getter
public class InsufficientProductQuantityException extends RuntimeException {
    private final ProductInShoppingCartLowQuantityInWarehouseDto exceptionDto;
    private static final String DEFAULT_MESSAGE = "Недостаточное количество товара на складе";

    public InsufficientProductQuantityException() {
        super(DEFAULT_MESSAGE);
        this.exceptionDto = new ProductInShoppingCartLowQuantityInWarehouseDto()
                .message(DEFAULT_MESSAGE);
    }
}
