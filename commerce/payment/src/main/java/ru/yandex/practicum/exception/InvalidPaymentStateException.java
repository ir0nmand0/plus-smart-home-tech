package ru.yandex.practicum.exception;

import lombok.Getter;

/**
 * Исключение при некорректном статусе платежа
 */

@Getter
public class InvalidPaymentStateException extends RuntimeException {
  private final String message;

  public InvalidPaymentStateException(String message) {
    super(message);
    this.message = message;
  }
}
