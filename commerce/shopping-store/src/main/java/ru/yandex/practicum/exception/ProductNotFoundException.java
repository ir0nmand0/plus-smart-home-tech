package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.ProductNotFoundExceptionDto;
import java.util.UUID;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final ProductNotFoundExceptionDto exceptionDto;
    private static final String DEFAULT_MESSAGE = "Product not found";

    public ProductNotFoundException(UUID productId) {
        super(DEFAULT_MESSAGE);
        this.exceptionDto = new ProductNotFoundExceptionDto()
                .productId(productId)
                .message("Товар с ID " + productId + " не найден");
    }
}
