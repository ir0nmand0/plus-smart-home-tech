package ru.yandex.practicum.exception;

import lombok.Getter;

/**
 * Исключение при проблемах взаимодействия с внешними сервисами
 */

@Getter
public class ExternalServiceException extends RuntimeException {
  private final String message;

  public ExternalServiceException(String message) {
    super(message);
    this.message = message;
  }
}
