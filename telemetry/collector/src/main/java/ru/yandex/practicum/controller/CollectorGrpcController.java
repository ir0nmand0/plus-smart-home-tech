package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.KafkaProducerService;

/**
 * Контроллер для обработки gRPC-запросов.
 * Принимает события от клиентов и передает их в сервис для дальнейшей обработки.
 */
@GrpcService
@RequiredArgsConstructor
public class CollectorGrpcController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final KafkaProducerService<SensorEventProto, HubEventProto> kafkaProducerService;

    /**
     * Прием события от датчика через gRPC.
     *
     * @param request          Событие от датчика.
     * @param responseObserver Объект для отправки ответа клиенту.
     */
    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            // Делегируем обработку в сервис
            kafkaProducerService.publishSensorEvent(request);

            // Возвращаем успешный ответ клиенту
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Обработка ошибок
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)
            ));
        }
    }

    /**
     * Прием события от хаба через gRPC.
     *
     * @param request          Событие от хаба.
     * @param responseObserver Объект для отправки ответа клиенту.
     */
    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            // Делегируем обработку в сервис
            kafkaProducerService.publishHubEvent(request);

            // Возвращаем успешный ответ клиенту
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            // Обработка ошибок
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)
            ));
        }
    }
}
