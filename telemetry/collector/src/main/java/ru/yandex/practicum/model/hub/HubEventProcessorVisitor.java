package ru.yandex.practicum.model.hub;

public interface HubEventProcessorVisitor {
    void process(DeviceAddedEvent event);

    void process(DeviceRemovedEvent event);

    void process(ScenarioAddedEvent event);

    void process(ScenarioRemovedEvent event);
}
