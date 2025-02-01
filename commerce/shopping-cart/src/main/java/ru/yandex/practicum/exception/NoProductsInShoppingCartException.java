package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.NoProductsInShoppingCartExceptionDto;

/**
 * Исключение при отсутствии товаров в корзине.
 */
@Getter
public class NoProductsInShoppingCartException extends RuntimeException {
    private final NoProductsInShoppingCartExceptionDto exceptionDto;
    private static final String DEFAULT_MESSAGE = "Shopping cart is empty";

    public NoProductsInShoppingCartException() {
        super(DEFAULT_MESSAGE);
        this.exceptionDto = new NoProductsInShoppingCartExceptionDto()
                .message(DEFAULT_MESSAGE);
    }
}
