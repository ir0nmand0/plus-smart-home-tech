package ru.yandex.practicum.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для проверки функциональности LoggingAspect.
 * Использует Mockito для мокирования зависимостей и перенаправление System.err
 * для захвата и проверки лог-сообщений при использовании slf4j-simple.
 */
@ExtendWith(MockitoExtension.class) // Рекомендуемый способ инициализации моков с JUnit 5
class LoggingAspectTest {

    /**
     * Константы для повторяющихся строк, используемых в тестах.
     */
    private static final String METHOD_NAME = "testMethod";
    private static final String MOCK_RESULT = "result";
    private static final String EXCEPTION_MESSAGE = "Test exception";
    private static final String[] MOCK_ARGS = {"arg1", "42"};
    private static final String METHOD_ARGS_STRING = "[arg1, 42]";

    private static final String LOG_LEVEL_INFO = "INFO";
    private static final String LOG_LEVEL_ERROR = "ERROR";

    private static final String METHOD_ENTER_LOG = "Вход в метод: " + METHOD_NAME + " с аргументами: " + METHOD_ARGS_STRING;
    private static final String METHOD_SUCCESS_LOG = "Метод " + METHOD_NAME + " успешно выполнен за";
    private static final String METHOD_RESULT_LOG = "мс. Результат: " + MOCK_RESULT;
    private static final String METHOD_EXCEPTION_LOG = "Исключение в методе " + METHOD_NAME + " после";
    private static final String METHOD_EXCEPTION_MESSAGE = "мс. Сообщение: " + EXCEPTION_MESSAGE;

    /**
     * Мокаем ProceedingJoinPoint для имитации точки соединения вокруг метода.
     * Это позволяет нам контролировать поведение метода, который обрабатывается аспектом.
     */
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    /**
     * Мокаем Signature для получения информации о методе.
     * Используется для предоставления информации о подписи метода, такой как его имя.
     */
    @Mock
    private Signature signature;

    /**
     * Экземпляр аспекта, который будем тестировать.
     * Это объект класса LoggingAspect, содержащий логику логирования.
     */
    private LoggingAspect loggingAspect;

    /**
     * Оригинальный поток System.err для восстановления после тестов.
     * Мы сохраняем его, чтобы вернуть в исходное состояние после перенаправления.
     */
    private final PrintStream originalErr = System.err;

    /**
     * Поток для захвата вывода System.err.
     * Используется для хранения лог-сообщений, которые записывает slf4j-simple.
     */
    private ByteArrayOutputStream errContent;

    /**
     * Метод, выполняющийся перед каждым тестом.
     * - Создает экземпляр LoggingAspect.
     * - Настраивает моки для signature и proceedingJoinPoint.
     * - Перенаправляет System.err на ByteArrayOutputStream для захвата логов.
     * <p>
     * Инициализация моков происходит автоматически благодаря аннотации @ExtendWith(MockitoExtension.class).
     */
    @BeforeEach
    void setUp() {
        /**
         * Инициализация моков с помощью MockitoExtension:
         * Благодаря аннотации @ExtendWith(MockitoExtension.class), моки, аннотированные @Mock,
         * автоматически инициализируются перед каждым тестом.
         * Это рекомендуется в JUnit 5 и упрощает код, устраняя необходимость вызывать MockitoAnnotations.openMocks(this).
         */

        // Создаем экземпляр аспекта, который будем тестировать
        loggingAspect = new LoggingAspect();

        // Настраиваем моки:
        // При вызове proceedingJoinPoint.getSignature() будет возвращен мок signature
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        // При вызове signature.toShortString() будет возвращено имя метода
        when(signature.toShortString()).thenReturn(METHOD_NAME);

        // Захватываем вывод System.err в ByteArrayOutputStream для проверки логов
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
    }

    /**
     * Метод, выполняющийся после каждого теста.
     * - Восстанавливает оригинальный поток System.err.
     * Это важно для того, чтобы другие тесты или части системы не были затронуты перенаправлением.
     */
    @AfterEach
    void tearDown() {
        // Восстанавливаем оригинальный поток System.err
        System.setErr(originalErr);
    }

    /**
     * Тестирует поведение аспекта при успешном выполнении метода.
     * - Проверяет, что логируются вход в метод и успешное завершение с результатом.
     * - Проверяет, что оригинальный метод вызывается.
     * - Проверяет, что результат метода соответствует ожиданиям.
     * - Проверяет, что лог-сообщения содержат ожидаемые тексты.
     *
     * @throws Throwable если возникает исключение при выполнении метода
     */
    @Test
    void logAroundMethod_SuccessfulExecution() throws Throwable {
        // Мокаем аргументы метода
        when(proceedingJoinPoint.getArgs()).thenReturn(MOCK_ARGS);

        // Мокаем возвращаемое значение метода
        when(proceedingJoinPoint.proceed()).thenReturn(MOCK_RESULT);

        // Вызываем метод аспекта, передавая мокаемый ProceedingJoinPoint
        Object result = loggingAspect.logAround(proceedingJoinPoint);

        // Проверяем, что оригинальный метод был вызван один раз
        verify(proceedingJoinPoint, times(1)).proceed();

        // Проверяем, что результат метода соответствует ожидаемому
        assertEquals(MOCK_RESULT, result);

        // Получаем захваченные лог-сообщения из errContent
        String logs = errContent.toString();

        // Проверяем, что лог-сообщения содержат информацию о входе в метод
        assertTrue(logs.contains(LOG_LEVEL_INFO) && logs.contains(METHOD_ENTER_LOG),
                "Лог не содержит информацию о входе в метод");

        // Проверяем, что лог-сообщения содержат информацию об успешном завершении метода
        assertTrue(logs.contains(LOG_LEVEL_INFO) && logs.contains(METHOD_SUCCESS_LOG),
                "Лог не содержит информацию об успешном завершении метода");
        assertTrue(logs.contains(METHOD_RESULT_LOG),
                "Лог не содержит информацию о результате метода");
    }

    /**
     * Тестирует поведение аспекта при выбрасывании исключения в методе.
     * - Проверяет, что логируются вход в метод и информация об исключении.
     * - Проверяет, что оригинальный метод вызывается и выбрасывает исключение.
     * - Проверяет, что лог-сообщения содержат ожидаемые тексты об ошибке.
     *
     * @throws Throwable если возникает исключение при выполнении метода
     */
    @Test
    void logAroundMethod_ExceptionThrown() throws Throwable {
        // Мокаем аргументы метода
        when(proceedingJoinPoint.getArgs()).thenReturn(MOCK_ARGS);

        // Мокаем выброс исключения при вызове proceed()
        when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException(EXCEPTION_MESSAGE));

        // Вызываем метод аспекта и ожидаем выброс RuntimeException
        Exception exception = assertThrows(RuntimeException.class, () -> {
            loggingAspect.logAround(proceedingJoinPoint);
        });

        // Проверяем, что оригинальный метод был вызван один раз
        verify(proceedingJoinPoint, times(1)).proceed();

        // Проверяем, что выброшенное исключение имеет ожидаемое сообщение
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());

        // Получаем захваченные лог-сообщения из errContent
        String logs = errContent.toString();

        // Проверяем, что лог-сообщения содержат информацию о входе в метод
        assertTrue(logs.contains(LOG_LEVEL_INFO) && logs.contains(METHOD_ENTER_LOG),
                "Лог не содержит информацию о входе в метод");

        // Проверяем, что лог-сообщения содержат информацию об исключении
        assertTrue(logs.contains(LOG_LEVEL_ERROR) && logs.contains(METHOD_EXCEPTION_LOG),
                "Лог не содержит информацию об исключении");
        assertTrue(logs.contains(METHOD_EXCEPTION_MESSAGE),
                "Лог не содержит сообщение об исключении");
    }
}
