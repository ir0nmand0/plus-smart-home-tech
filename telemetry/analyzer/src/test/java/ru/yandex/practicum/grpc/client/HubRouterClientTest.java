package ru.yandex.practicum.grpc.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.mapper.EnumMapper;
import ru.yandex.practicum.model.entity.ActionType;

import static org.mockito.Mockito.*;

/**
 * Unit-тесты для класса HubRouterClient.
 */
@ExtendWith(MockitoExtension.class)
class HubRouterClientTest {

    @Mock
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    @Mock
    private EnumMapper enumMapper;

    @InjectMocks
    private HubRouterClient hubRouterClient;

    /**
     * Проверяет, что gRPC-клиент отправляет запрос корректно.
     */
    @Test
    void sendDeviceAction_shouldSendRequestToGrpcClient() {
        // Подготовка данных
        String sensorId = "sensor-123";
        ActionType actionType = ActionType.ACTIVATE;
        int value = 42;

        when(enumMapper.map(actionType)).thenReturn(ActionTypeProto.ACTIVATE);

        // Выполнение тестируемого метода
        hubRouterClient.sendDeviceAction(sensorId, actionType, value);

        // Проверка, что gRPC-клиент получил корректный запрос
        verify(hubRouterStub, times(1))
                .handleDeviceAction(argThat(request ->
                        request.getSensorId().equals(sensorId) &&
                                request.getType() == ActionTypeProto.ACTIVATE &&
                                request.getValue() == value
                ));
    }
}
