package ru.yandex.practicum.mapper.toModel.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;

@Component
public class HubEventModelFactory {

    public DeviceAddedEvent createDeviceAddedEvent() {
        return new DeviceAddedEvent();
    }

    public DeviceRemovedEvent createDeviceRemovedEvent() {
        return new DeviceRemovedEvent();
    }

    public ScenarioAddedEvent createScenarioAddedEvent() {
        return new ScenarioAddedEvent();
    }

    public ScenarioRemovedEvent createScenarioRemovedEvent() {
        return new ScenarioRemovedEvent();
    }
}
