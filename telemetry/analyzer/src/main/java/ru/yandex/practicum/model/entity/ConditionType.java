package ru.yandex.practicum.model.entity;

public enum ConditionType {
    MOTION,         // Движение
    LUMINOSITY,     // Освещённость
    SWITCH,         // Состояние переключателя
    TEMPERATURE,    // Температура
    CO2LEVEL,       // Уровень CO2
    HUMIDITY,        // Влажность
    UNRECOGNIZED    // Для некорректных значений
}

