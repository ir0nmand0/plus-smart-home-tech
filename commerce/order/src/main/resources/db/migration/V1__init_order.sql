-- Создание схемы для сервиса заказов
CREATE SCHEMA IF NOT EXISTS order_schema AUTHORIZATION order_user;

-- Создание ENUM типа для статуса заказа через DO блок
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'order_state') THEN
        CREATE TYPE order_schema.order_state AS ENUM (
            'NEW',
            'ON_PAYMENT',
            'ON_DELIVERY',
            'DONE',
            'DELIVERED',
            'ASSEMBLED',
            'PAID',
            'COMPLETED',
            'DELIVERY_FAILED',
            'ASSEMBLY_FAILED',
            'PAYMENT_FAILED',
            'PRODUCT_RETURNED',
            'CANCELED'
        );
    END IF;
END
$$;

-- Создание таблицы заказов
CREATE TABLE IF NOT EXISTS order_schema.orders (
    order_id         UUID PRIMARY KEY,
    shopping_cart_id UUID,
    delivery_id      UUID,
    payment_id       UUID,
    username         VARCHAR(255) NOT NULL,
    state            order_schema.order_state NOT NULL,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile          BOOLEAN DEFAULT false,
    total_price      DOUBLE PRECISION,
    delivery_price   DOUBLE PRECISION,
    product_price    DOUBLE PRECISION,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы позиций заказа
CREATE TABLE IF NOT EXISTS order_schema.order_items (
    order_id   UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity   INTEGER NOT NULL,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id)
        REFERENCES order_schema.orders(order_id)
        ON DELETE CASCADE,
    CONSTRAINT quantity_positive CHECK (quantity > 0)
);

-- Создание индексов для таблиц сервиса заказов
CREATE INDEX IF NOT EXISTS idx_orders_username ON order_schema.orders(username);
CREATE INDEX IF NOT EXISTS idx_orders_state ON order_schema.orders(state);
CREATE INDEX IF NOT EXISTS idx_orders_shopping_cart ON order_schema.orders(shopping_cart_id);
CREATE INDEX IF NOT EXISTS idx_orders_delivery ON order_schema.orders(delivery_id);
CREATE INDEX IF NOT EXISTS idx_orders_payment ON order_schema.orders(payment_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_schema.order_items(product_id);

-- (Опционально) Создание функции обновления поля updated_at
CREATE OR REPLACE FUNCTION order_schema.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
