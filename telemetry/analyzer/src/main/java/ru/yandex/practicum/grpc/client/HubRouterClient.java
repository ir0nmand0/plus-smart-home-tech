package ru.yandex.practicum.grpc.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.mapper.EnumMapper;
import ru.yandex.practicum.model.entity.ActionType;

/**
 * gRPC-клиент для отправки действий на устройства хаба.
 */
@Component
public class HubRouterClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;
    private final EnumMapper enumMapper;

    public HubRouterClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub,
                           EnumMapper enumMapper) {
        this.hubRouterStub = hubRouterStub;
        this.enumMapper = enumMapper;
    }

    /**
     * Отправляет команду устройству через gRPC.
     *
     * @param sensorId ID сенсора.
     * @param type     Тип действия.
     * @param value    Значение (опционально).
     */
    public void sendDeviceAction(String sensorId, ActionType type, Integer value) {
        DeviceActionRequest.Builder requestBuilder = DeviceActionRequest.newBuilder()
                .setSensorId(sensorId)
                .setType(enumMapper.map(type));

        if (value != null) {
            requestBuilder.setValue(value);
        }

        hubRouterStub.handleDeviceAction(requestBuilder.build());
    }
}
