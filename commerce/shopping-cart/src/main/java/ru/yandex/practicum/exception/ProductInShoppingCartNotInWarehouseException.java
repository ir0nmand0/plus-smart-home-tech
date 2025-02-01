package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.ProductInShoppingCartNotInWarehouseDto;

/**
 * Исключение, когда товар из корзины отсутствует на складе.
 */
@Getter
public class ProductInShoppingCartNotInWarehouseException extends RuntimeException {
    private final ProductInShoppingCartNotInWarehouseDto exceptionDto;
    private static final String DEFAULT_MESSAGE = "Product from shopping cart not found in warehouse";

    public ProductInShoppingCartNotInWarehouseException() {
        super(DEFAULT_MESSAGE);
        this.exceptionDto = new ProductInShoppingCartNotInWarehouseDto()
                .message(DEFAULT_MESSAGE);
    }

    public ProductInShoppingCartNotInWarehouseException(String message) {
        super(message);
        this.exceptionDto = new ProductInShoppingCartNotInWarehouseDto()
                .message(message);
    }
}
