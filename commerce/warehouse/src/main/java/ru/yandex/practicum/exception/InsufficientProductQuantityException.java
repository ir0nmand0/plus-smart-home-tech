package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.ProductInShoppingCartLowQuantityInWarehouseDto;

/**
 * Исключение при недостаточном количестве товара на складе
 */
@Getter
public class InsufficientProductQuantityException extends RuntimeException {
    private final ProductInShoppingCartLowQuantityInWarehouseDto exceptionDto;
    private static final String MESSAGE = "Недостаточное количество товара на складе";

    public InsufficientProductQuantityException() {
        super(MESSAGE);
        this.exceptionDto = new ProductInShoppingCartLowQuantityInWarehouseDto()
                .message(MESSAGE)
                .status(ProductInShoppingCartLowQuantityInWarehouseDto.StatusEnum.BAD_REQUEST);
    }
}