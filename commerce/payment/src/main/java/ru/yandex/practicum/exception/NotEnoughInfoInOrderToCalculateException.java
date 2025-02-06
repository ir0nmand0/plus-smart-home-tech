package ru.yandex.practicum.exception;

import lombok.Getter;

/**
 * Исключение при недостаточной информации для расчета стоимости
 */

@Getter
public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
  private final String message;

  public NotEnoughInfoInOrderToCalculateException(String message) {
    super(message);
    this.message = message;
  }
}

