package ru.yandex.practicum.mapper.helper;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.Objects;

/**
 * Маппер для преобразования между Protobuf Timestamp и Java Instant.
 */
@Mapper(componentModel = "spring")
public interface TimestampProtobufMapper {

    /**
     * Преобразует Protobuf Timestamp в Java Instant.
     *
     * @param timestamp Protobuf Timestamp.
     * @return Java Instant или null, если входной параметр null.
     */
    @Named("timestampToInstant")
    default Instant timestampToInstant(Timestamp timestamp) {
        return Objects.nonNull(timestamp) ? Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()) : null;
    }

    /**
     * Преобразует Java Instant в Protobuf Timestamp.
     *
     * @param instant Java Instant.
     * @return Protobuf Timestamp или null, если входной параметр null.
     */
    @Named("instantToTimestamp")
    default Timestamp instantToTimestamp(Instant instant) {
        return Objects.nonNull(instant)
                ? Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build()
                : null;
    }
}
