package ru.yandex.practicum.model.hub;

public interface HubEventProcessorVisitor {
    void visit(DeviceAddedEvent event);

    void visit(DeviceRemovedEvent event);

    void visit(ScenarioAddedEvent event);

    void visit(ScenarioRemovedEvent event);
}
