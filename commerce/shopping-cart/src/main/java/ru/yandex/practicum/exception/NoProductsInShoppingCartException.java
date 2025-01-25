package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.NoProductsInShoppingCartExceptionDto;

/**
 * Исключение при отсутствии товаров в корзине
 */
@Getter
public class NoProductsInShoppingCartException extends RuntimeException {
    private final NoProductsInShoppingCartExceptionDto exceptionDto;
    private static final String message = "Shopping cart is empty";

    public NoProductsInShoppingCartException() {
        super(message);
        this.exceptionDto = new NoProductsInShoppingCartExceptionDto()
                .message(message)
                .status(NoProductsInShoppingCartExceptionDto.StatusEnum.BAD_REQUEST);
    }
}