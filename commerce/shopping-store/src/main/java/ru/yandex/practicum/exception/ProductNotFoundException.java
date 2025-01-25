package ru.yandex.practicum.exception;

import lombok.Getter;
import ru.yandex.practicum.common.model.ProductNotFoundExceptionDto;

import java.util.UUID;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final ProductNotFoundExceptionDto exceptionDto;
    private static final String message = "Product not found";

    public ProductNotFoundException(UUID productId) {
        super(message);
        this.exceptionDto = new ProductNotFoundExceptionDto()
                .productId(productId)
                .message("Товар с ID " + productId + " не найден")
                .status(ProductNotFoundExceptionDto.StatusEnum.NOT_FOUND);
    }
}