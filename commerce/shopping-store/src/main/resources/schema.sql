CREATE SCHEMA IF NOT EXISTS shopping_store;

CREATE TABLE IF NOT EXISTS shopping_store.products (
    id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE PRECISION NOT NULL CHECK (price > 0),
    product_category VARCHAR(50) NOT NULL
    CHECK (product_category IN ('CONTROL', 'SENSORS', 'LIGHTING')),
    quantity_state VARCHAR(20) NOT NULL
    CHECK (quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    product_state VARCHAR(20) NOT NULL
    DEFAULT 'ACTIVE'
    CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),
    rating INT
    DEFAULT 5
    CHECK (rating BETWEEN 1 AND 5),
    image_src TEXT
);

CREATE INDEX IF NOT EXISTS idx_products_category
    ON shopping_store.products (product_category);
CREATE INDEX IF NOT EXISTS idx_products_state
    ON shopping_store.products (product_state);
CREATE INDEX IF NOT EXISTS idx_products_quantity_state
    ON shopping_store.products (quantity_state);
