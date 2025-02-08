package ru.yandex.practicum.exception;

import lombok.Getter;

/**
 * Исключение при проблемах с сохранением платежа
 */

@Getter
public class PaymentPersistenceException extends RuntimeException {
  private final String message;

  public PaymentPersistenceException(String message) {
    super(message);
    this.message = message;
  }
}
