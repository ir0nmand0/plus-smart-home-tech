-- Создание схемы для сервиса доставки
CREATE SCHEMA IF NOT EXISTS delivery_schema AUTHORIZATION delivery_user;

-- Создание ENUM типа для статуса доставки через DO блок
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'delivery_state') THEN
        CREATE TYPE delivery_schema.delivery_state AS ENUM (
            'CREATED',
            'IN_PROGRESS',
            'DELIVERED',
            'FAILED',
            'CANCELLED'
        );
    END IF;
END
$$;

-- Создание таблицы адресов
CREATE TABLE IF NOT EXISTS delivery_schema.addresses (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country    VARCHAR(100) NOT NULL,
    city       VARCHAR(100) NOT NULL,
    street     VARCHAR(200) NOT NULL,
    house      VARCHAR(20)  NOT NULL,
    flat       VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы доставок
CREATE TABLE IF NOT EXISTS delivery_schema.deliveries (
    delivery_id      UUID PRIMARY KEY,
    order_id         UUID NOT NULL,
    from_address_id  UUID NOT NULL,
    to_address_id    UUID NOT NULL,
    delivery_state   delivery_schema.delivery_state NOT NULL DEFAULT 'CREATED',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_address_id)
        REFERENCES delivery_schema.addresses(address_id),
    FOREIGN KEY (to_address_id)
        REFERENCES delivery_schema.addresses(address_id)
);

-- Создание таблицы истории доставки
CREATE TABLE IF NOT EXISTS delivery_schema.delivery_history (
    history_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    delivery_id     UUID NOT NULL,
    previous_state  delivery_schema.delivery_state,
    new_state       delivery_schema.delivery_state NOT NULL,
    changed_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (delivery_id)
        REFERENCES delivery_schema.deliveries(delivery_id)
        ON DELETE CASCADE
);

-- Создание индексов для таблиц сервиса доставки
CREATE INDEX IF NOT EXISTS idx_addresses_location ON delivery_schema.addresses(city, street);
CREATE INDEX IF NOT EXISTS idx_deliveries_order ON delivery_schema.deliveries(order_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_state ON delivery_schema.deliveries(delivery_state);
CREATE INDEX IF NOT EXISTS idx_delivery_history_delivery ON delivery_schema.delivery_history(delivery_id);

-- Создание функции обновления поля updated_at
CREATE OR REPLACE FUNCTION delivery_schema.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для обновления поля updated_at в таблице доставок
CREATE OR REPLACE TRIGGER update_deliveries_updated_at
    BEFORE UPDATE ON delivery_schema.deliveries
    FOR EACH ROW
    EXECUTE FUNCTION delivery_schema.update_updated_at_column();

-- Создание триггера для обновления поля updated_at в таблице адресов
CREATE OR REPLACE TRIGGER update_addresses_updated_at
    BEFORE UPDATE ON delivery_schema.addresses
    FOR EACH ROW
    EXECUTE FUNCTION delivery_schema.update_updated_at_column();
