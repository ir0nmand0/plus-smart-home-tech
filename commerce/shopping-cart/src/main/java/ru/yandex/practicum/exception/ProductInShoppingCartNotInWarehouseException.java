package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.ProductInShoppingCartNotInWarehouseDto;

/**
 * Исключение когда товар из корзины отсутствует на складе
 */
@Getter
public class ProductInShoppingCartNotInWarehouseException extends RuntimeException {
    private final ProductInShoppingCartNotInWarehouseDto exceptionDto;
    private static final String message = "Product from shopping cart not found in warehouse";

    public ProductInShoppingCartNotInWarehouseException() {
        super(message);
        this.exceptionDto = new ProductInShoppingCartNotInWarehouseDto()
                .message(message)
                .status(ProductInShoppingCartNotInWarehouseDto.StatusEnum.BAD_REQUEST);
    }

    // Новый конструктор с кастомным сообщением
    public ProductInShoppingCartNotInWarehouseException(String message) {
        super(message);
        this.exceptionDto = new ProductInShoppingCartNotInWarehouseDto()
                .message(message)
                .status(ProductInShoppingCartNotInWarehouseDto.StatusEnum.BAD_REQUEST);
    }
}