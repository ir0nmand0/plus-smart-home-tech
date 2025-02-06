CREATE SCHEMA IF NOT EXISTS warehouse;

CREATE TABLE IF NOT EXISTS warehouse.products (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE,
    fragile BOOLEAN DEFAULT false,
    weight DOUBLE PRECISION NOT NULL CHECK (weight > 0),
    width DOUBLE PRECISION NOT NULL CHECK (width > 0),
    height DOUBLE PRECISION NOT NULL CHECK (height > 0),
    depth DOUBLE PRECISION NOT NULL CHECK (depth > 0),
    quantity BIGINT DEFAULT 0 CHECK (quantity >= 0)
);

CREATE INDEX IF NOT EXISTS idx_warehouse_product_id
    ON warehouse.products(product_id);

-- Таблица для хранения бронирований заказов
CREATE TABLE IF NOT EXISTS warehouse.order_bookings (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_id UUID,
    total_weight DOUBLE PRECISION NOT NULL,
    total_volume DOUBLE PRECISION NOT NULL,
    has_fragile_items BOOLEAN NOT NULL,
    CONSTRAINT uk_order_booking_order_id UNIQUE (order_id)
);

-- Таблица для хранения товаров в бронировании
CREATE TABLE IF NOT EXISTS warehouse.order_booking_items (
    booking_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (booking_id, product_id),
    FOREIGN KEY (booking_id) REFERENCES warehouse.order_bookings (id)
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_order_bookings_order_id
    ON warehouse.order_bookings(order_id);
CREATE INDEX IF NOT EXISTS idx_order_bookings_delivery_id
    ON warehouse.order_bookings(delivery_id);