package ru.yandex.practicum.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * Аспект для логирования выполнения методов в пакетах service и controller.
 * Соответствует стандартам Java 21 и Spring Boot 3.x.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Pointcut, соответствующий всем методам в пакетах service и controller.
     * Охватывает все методы внутри этих пакетов и их подпакетов.
     */
    @Pointcut("execution(* ru.yandex.practicum.aggregator.service..*(..)) "
            + "|| execution(* ru.yandex.practicum.aggregator.consumer..*(..))")
    public void applicationMethods() {
        // Метод пустой, так как он служит только для определения pointcut.
    }

    /**
     * Метод с аннотацией @Around для логирования деталей выполнения методов.
     * Логирует вход в метод, аргументы, время выполнения, результат и исключения.
     *
     * @param joinPoint точка соединения, представляющая метод, который обрабатывается аспектом.
     * @return результат выполнения метода.
     * @throws Throwable если исходный метод выбрасывает исключение.
     */
    @Around("applicationMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // Получаем имя метода и аргументы
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        // Логируем вход в метод с аргументами
        log.info("Вход в метод: {} с аргументами: {}", methodName, Arrays.toString(args));

        // Записываем время начала выполнения
        Instant startTime = Instant.now();

        try {
            // Выполняем метод
            Object result = joinPoint.proceed();

            // Вычисляем затраченное время
            Duration duration = Duration.between(startTime, Instant.now());

            // Логируем успешное завершение метода с результатом и временем выполнения
            log.info("Метод {} успешно выполнен за {} мс. Результат: {}",
                    methodName, duration.toMillis(), result);

            return result;
        } catch (Throwable ex) {
            // Вычисляем затраченное время даже при возникновении исключения
            Duration duration = Duration.between(startTime, Instant.now());

            // Логируем исключение с деталями метода и временем выполнения
            log.error("Исключение в методе {} после {} мс. Сообщение: {}",
                    methodName, duration.toMillis(), ex.getMessage(), ex);

            // Повторно выбрасываем исключение для дальнейшей обработки
            throw ex;
        }
    }
}
