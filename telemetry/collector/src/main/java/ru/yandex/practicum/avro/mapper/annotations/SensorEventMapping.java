package ru.yandex.practicum.avro.mapper.annotations;

import org.mapstruct.Mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapping(target = "id", expression = "java(event.getId())")
@Mapping(target = "hubId", expression = "java(event.getHubId())")
@Mapping(target = "timestamp", expression = "java(event.getTimestamp())")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface SensorEventMapping {
}