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
