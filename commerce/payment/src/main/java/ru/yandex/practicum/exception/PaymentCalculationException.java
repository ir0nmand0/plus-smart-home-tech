package ru.yandex.practicum.exception;

import lombok.Getter;

/**
 * Исключение при ошибке в расчетах платежа
 */

@Getter
public class PaymentCalculationException extends RuntimeException {
  private final String message;

  public PaymentCalculationException(String message) {
    super(message);
    this.message = message;
  }
}
