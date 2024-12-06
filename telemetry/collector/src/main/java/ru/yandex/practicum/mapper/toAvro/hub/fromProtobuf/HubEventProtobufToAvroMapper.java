package ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.helper.TimestampProtobufMapper;
import ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload.DeviceAddedEventProtobufMapper;
import ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload.DeviceRemovedEventProtobufMapper;
import ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload.ScenarioAddedEventProtobufMapper;
import ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload.ScenarioRemovedEventProtobufMapper;

@Mapper(componentModel = "spring", uses = {
        DeviceAddedEventProtobufMapper.class,
        DeviceRemovedEventProtobufMapper.class,
        ScenarioAddedEventProtobufMapper.class,
        ScenarioRemovedEventProtobufMapper.class,
        TimestampProtobufMapper.class
})
public interface HubEventProtobufToAvroMapper {
    /**
     * Преобразует HubEventProto в HubEventAvro.
     * <p>
     * Использует дочерние мапперы для обработки различных типов событий.
     *
     * @param event Protobuf-событие.
     * @return Avro-событие.
     */
    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "timestampToInstant")
    @Mapping(source = ".", target = "payload", qualifiedByName = "mapHubPayloadToAvro")
    HubEventAvro toAvro(HubEventProto event);

    /**
     * Определяет, какой тип payload присутствует в Protobuf событии, и мапит его для Avro.
     * <p>
     * Используется для обработки вложенных объектов событий в зависимости от их типа.
     *
     * @param proto Protobuf-событие.
     * @return Объект payload для Avro.
     */
    @Named("mapHubPayloadToAvro")
    default Object mapHubPayloadToAvro(HubEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> proto.getDeviceAdded();
            case DEVICE_REMOVED -> proto.getDeviceRemoved();
            case SCENARIO_ADDED -> proto.getScenarioAdded();
            case SCENARIO_REMOVED -> proto.getScenarioRemoved();
            default -> null;
        };
    }
}
