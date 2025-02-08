-- Создаём схему, если она ещё не создана
CREATE SCHEMA IF NOT EXISTS analyzer_schema;

-- Таблица сценариев умного дома
CREATE TABLE IF NOT EXISTS analyzer_schema.scenarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Уникальный идентификатор сценария
    hub_id VARCHAR NOT NULL,                           -- Идентификатор хаба, к которому относится сценарий
    name VARCHAR NOT NULL,                             -- Уникальное имя сценария в рамках хаба
    UNIQUE(hub_id, name)                               -- Гарантирует уникальность сценария в пределах хаба
);

-- Таблица сенсоров
CREATE TABLE IF NOT EXISTS analyzer_schema.sensors (
    id VARCHAR PRIMARY KEY,     -- Уникальный идентификатор сенсора
    hub_id VARCHAR NOT NULL     -- Идентификатор хаба, к которому относится сенсор
);

-- Таблица условий активации сценариев
CREATE TABLE IF NOT EXISTS analyzer_schema.conditions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Уникальный идентификатор условия
    type VARCHAR NOT NULL,                              -- Тип условия (например, "температура", "движение")
    operation VARCHAR NOT NULL,                         -- Операция сравнения (например, ">", "<", "=")
    value INTEGER DEFAULT NULL,                         -- Значение для числовых условий
    bool_value BOOLEAN DEFAULT NULL                     -- Значение для логических условий
);

-- Таблица действий, выполняемых сценариями
CREATE TABLE IF NOT EXISTS analyzer_schema.actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Уникальный идентификатор действия
    type VARCHAR NOT NULL,                              -- Тип действия (например, "включить", "выключить")
    value INTEGER NOT NULL                              -- Значение действия (например, целевая температура)
);

-- Таблица связывания сценария, сенсора и условия
CREATE TABLE IF NOT EXISTS analyzer_schema.scenario_conditions (
    scenario_id BIGINT REFERENCES analyzer_schema.scenarios(id) ON DELETE CASCADE, -- ID сценария
    sensor_id VARCHAR REFERENCES analyzer_schema.sensors(id) ON DELETE CASCADE,    -- ID сенсора
    condition_id BIGINT REFERENCES analyzer_schema.conditions(id) ON DELETE CASCADE, -- ID условия
    PRIMARY KEY (scenario_id, sensor_id, condition_id)             -- Композитный ключ для уникальности записи
);

-- Таблица связывания сценария, сенсора и действия
CREATE TABLE IF NOT EXISTS analyzer_schema.scenario_actions (
    scenario_id BIGINT REFERENCES analyzer_schema.scenarios(id) ON DELETE CASCADE, -- ID сценария
    sensor_id VARCHAR REFERENCES analyzer_schema.sensors(id) ON DELETE CASCADE,    -- ID сенсора
    action_id BIGINT REFERENCES analyzer_schema.actions(id) ON DELETE CASCADE,     -- ID действия
    PRIMARY KEY (scenario_id, sensor_id, action_id)                -- Композитный ключ для уникальности записи
);

-- Функция проверки соответствия хаба для связываемых сценария и сенсора
CREATE OR REPLACE FUNCTION analyzer_schema.check_hub_id()
RETURNS TRIGGER
LANGUAGE plpgsql
AS '
BEGIN
    IF (SELECT hub_id FROM analyzer_schema.scenarios WHERE id = NEW.scenario_id)
       != (SELECT hub_id FROM analyzer_schema.sensors WHERE id = NEW.sensor_id) THEN
        RAISE EXCEPTION ''Hub IDs do not match for scenario_id % and sensor_id %'', NEW.scenario_id, NEW.sensor_id;
    END IF;
    RETURN NEW;
END;
';

-- Триггер для проверки корректности связывания сценария и сенсора через условие
CREATE OR REPLACE TRIGGER tr_bi_scenario_conditions_hub_id_check
BEFORE INSERT ON analyzer_schema.scenario_conditions
FOR EACH ROW
EXECUTE FUNCTION analyzer_schema.check_hub_id();

-- Триггер для проверки корректности связывания сценария и сенсора через действие
CREATE OR REPLACE TRIGGER tr_bi_scenario_actions_hub_id_check
BEFORE INSERT ON analyzer_schema.scenario_actions
FOR EACH ROW
EXECUTE FUNCTION analyzer_schema.check_hub_id();
