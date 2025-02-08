package ru.yandex.practicum.exception;

import lombok.Getter;

/**
 * Исключение при отсутствии платежа
 */

@Getter
public class NoPaymentFoundException extends RuntimeException {
  private final String message;

  public NoPaymentFoundException(String message) {
    super(message);
    this.message = message;
  }
}
