package ru.yandex.practicum.model.hub;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConditionValueDeserializer extends StdDeserializer<ConditionValue> {

    protected ConditionValueDeserializer() {
        super(ConditionValue.class);
    }

    @Override
    public ConditionValue deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Проверяем, обёрнуто ли значение в объект
        if (node.isObject()) {
            if (node.has("value")) {
                JsonNode valueNode = node.get("value");

                // Проверяем тип значения внутри объекта
                if (valueNode.isBoolean()) {
                    return new BoolValue(valueNode.asBoolean());
                } else if (valueNode.isInt()) {
                    return new IntValue(valueNode.asInt());
                }
            }
        }

        // Обрабатываем простые значения
        if (node.isBoolean()) {
            return new BoolValue(node.asBoolean());
        } else if (node.isInt()) {
            return new IntValue(node.asInt());
        }

        throw new IllegalArgumentException("Unsupported value type: " + node);
    }
}
