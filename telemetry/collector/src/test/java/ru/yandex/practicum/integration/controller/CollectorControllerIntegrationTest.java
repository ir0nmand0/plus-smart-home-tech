package ru.yandex.practicum.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.CollectorApp;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.config.EndpointConfig;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест для контроллера CollectorController и связанного с ним сервиса EventProcessingServiceImpl.
 * <p>
 * Цель теста:
 * - Проверить успешную обработку событий сенсоров через эндпоинт, полученный из конфигурации.
 * - Проверить успешную обработку событий хабов через эндпоинт, полученный из конфигурации.
 * - Убедиться в корректной интеграции контроллера и сервиса, включая использование мапперов.
 * <p>
 * Используем интеграционный тест, чтобы загрузить полный контекст Spring и протестировать взаимодействие между компонентами.
 */
@SpringBootTest(classes = CollectorApp.class) // Запускает полный контекст Spring Boot для тестирования
@AutoConfigureMockMvc // Автоматически настраивает MockMvc для имитации HTTP-запросов
@EnableConfigurationProperties // Включает поддержку @ConfigurationProperties
@Import(KafkaProducerConfig.class) // Импортирует дополнительную конфигурацию KafkaProducerConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Использует один экземпляр тестового класса для всех тестов
@Slf4j
public class CollectorControllerIntegrationTest {

    private static final Random RANDOM = new Random();

    @Autowired
    private MockMvc mockMvc; // Используется для выполнения HTTP-запросов к контроллерам без запуска сервера

    @Autowired
    private ObjectMapper objectMapper; // Для сериализации объектов в JSON

    @Autowired
    private EndpointConfig endpointConfig;

    @Autowired
    private AppConfig appConfig; // Конфигурационные параметры приложения

    // Мокируем KafkaTemplate, чтобы избежать отправки реальных сообщений в Kafka во время тестирования
    @MockBean
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    /**
     * Настраиваем поведение мокированного KafkaTemplate перед каждым тестом.
     * Очищаем предыдущие взаимодействия и мокируем метод send().
     */
    @BeforeEach
    void setupKafkaMock() {
        // Очищаем предыдущие взаимодействия с моком
        clearInvocations(kafkaTemplate);

        // Создаем мок CompletableFuture с правильными типами
        CompletableFuture<SendResult<String, SpecificRecord>> future = mock(CompletableFuture.class);

        // Мокируем метод send() KafkaTemplate
        doReturn(future).when(kafkaTemplate).send(anyString(), anyString(), any(SpecificRecord.class));
    }

    // Тестовые данные: список событий сенсоров
    private final List<SensorEvent> sensorEvents = List.of(
            ClimateSensorEvent.builder()
                    .id("climate-sensor-%d".formatted(RANDOM.nextInt(1000)))
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .temperatureC(RANDOM.nextInt(40) - 20)
                    .humidity(RANDOM.nextInt(100))
                    .co2Level(RANDOM.nextInt(5000))
                    .build(),
            LightSensorEvent.builder()
                    .id("light-sensor-%d".formatted(RANDOM.nextInt(1000)))
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .linkQuality(RANDOM.nextInt(100))
                    .luminosity(RANDOM.nextInt(1000))
                    .build(),
            MotionSensorEvent.builder()
                    .id("motion-sensor-%d".formatted(RANDOM.nextInt(1000)))
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .linkQuality(RANDOM.nextInt(100))
                    .motion(RANDOM.nextBoolean())
                    .voltage(RANDOM.nextInt(5000))
                    .build(),
            SwitchSensorEvent.builder()
                    .id("switch-sensor-%d".formatted(RANDOM.nextInt(1000)))
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .state(RANDOM.nextBoolean())
                    .build(),
            TemperatureSensorEvent.builder()
                    .id("temp-sensor-%d".formatted(RANDOM.nextInt(1000)))
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .temperatureC(RANDOM.nextInt(40) - 20)
                    .temperatureF(RANDOM.nextInt(100) + 32)
                    .build()
    );

    // Тестовые данные: список событий хабов
    private final List<HubEvent> hubEvents = List.of(
            DeviceAddedEvent.builder()
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .id("device-%d".formatted(RANDOM.nextInt(1000)))
                    .type(DeviceType.values()[RANDOM.nextInt(DeviceType.values().length)])
                    .build(),
            DeviceRemovedEvent.builder()
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .id("device-%d".formatted(RANDOM.nextInt(1000)))
                    .build(),
            ScenarioAddedEvent.builder()
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .name("Scenario-%d".formatted(RANDOM.nextInt(1000)))
                    .conditions(List.of(
                            new ScenarioCondition("sensor-1", ConditionType.TEMPERATURE,
                                    ConditionOperation.GREATER_THAN, 25)
                    ))
                    .actions(List.of(
                            new DeviceAction("device-1", DeviceActionType.ACTIVATE, 0)
                    ))
                    .build(),
            ScenarioRemovedEvent.builder()
                    .hubId("hub-%d".formatted(RANDOM.nextInt(100)))
                    .name("Scenario-%d".formatted(RANDOM.nextInt(1000)))
                    .build()
    );

    /**
     * Отправляет событие сенсора на контроллер.
     *
     * @param event Событие сенсора.
     */
    private void sendSensorEvent(SensorEvent event) {
        send(endpointConfig.getEndpointSensor(), event);
    }

    /**
     * Отправляет событие хаба на контроллер.
     *
     * @param event Событие хаба.
     */
    private void sendHubEvent(HubEvent event) {
        send(endpointConfig.getEndpointHub(), event);
    }

    /**
     * Универсальный метод для отправки события на указанный эндпоинт и проверки статуса ответа.
     * <p>
     * Используем тип Object для параметра event, поскольку хотим, чтобы метод send() был универсальным и мог
     * принимать различные типы событий (SensorEvent, HubEvent и их подклассы). Поскольку эти типы не имеют
     * общего родительского класса (кроме Object), использование Object позволяет нам избежать дублирования кода
     * или сложной иерархии наследования.
     * <p>
     * Это безопасно, поскольку контролируем типы передаваемых объектов, и ObjectMapper способен корректно
     * сериализовать любые объекты благодаря настройкам Jackson и аннотациям в наших моделях.
     * <p>
     * Как ObjectMapper обрабатывает сериализацию с Object:
     * - ObjectMapper использует реальный тип объекта во время выполнения для определения, как его сериализовать.
     * - Хотя метод принимает Object, реальный тип (например, ClimateSensorEvent) используется.
     * - Jackson проверяет аннотации класса (такие как @JsonTypeInfo, @JsonSubTypes),
     * чтобы включить информацию о типе в JSON.
     * - Это позволяет правильно сериализовать и десериализовать полиморфные типы.
     *
     * @param endpoint Эндпоинт для отправки события.
     * @param event    Событие, которое нужно отправить.
     */
    private void send(String endpoint, Object event) {
        try {
            // Сериализуем событие в JSON
            String eventJson = objectMapper.writeValueAsString(event);
            log.info("Отправка JSON на {}: {}", endpoint, eventJson);

            // Выполняем POST-запрос к контроллеру и ожидаем статус 200 OK
            mockMvc.perform(post(endpoint)
                            .content(eventJson)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            log.error("Неожиданное исключение при отправке события на {}: {}", endpoint, e.getMessage(), e);
            fail("Неожиданное исключение при отправке события: %s".formatted(e.getMessage()));
        }
    }

    /**
     * Тест успешной обработки событий сенсоров.
     * Проверяем, что каждый тип события обрабатывается корректно и отправляется в Kafka.
     */
    @Test
    void shouldProcessSensorEventsSuccessfully() {
        // Отправляем каждое событие сенсора на контроллер
        sensorEvents.forEach(this::sendSensorEvent);

        // Проверяем, что метод send() KafkaTemplate был вызван для каждого события
        sensorEvents.forEach(event -> {
            verify(kafkaTemplate, times(1))
                    .send(eq(appConfig.getSensors()), eq(event.getId()), any(SpecificRecord.class));
        });

        // Убеждаемся, что больше взаимодействий с kafkaTemplate не было
        verifyNoMoreInteractions(kafkaTemplate);
    }

    /**
     * Тест успешной обработки событий хабов.
     * Проверяем, что каждый тип события обрабатывается корректно и отправляется в Kafka.
     */
    @Test
    void shouldProcessHubEventsSuccessfully() {
        // Отправляем каждое событие хаба на контроллер
        hubEvents.forEach(this::sendHubEvent);

        // Проверяем, что метод send() KafkaTemplate был вызван для каждого события
        hubEvents.forEach(event -> {
            verify(kafkaTemplate, times(1))
                    .send(eq(appConfig.getHubs()), eq(event.getHubId()), any(SpecificRecord.class));
        });

        // Убеждаемся, что больше взаимодействий с kafkaTemplate не было
        verifyNoMoreInteractions(kafkaTemplate);
    }
}
