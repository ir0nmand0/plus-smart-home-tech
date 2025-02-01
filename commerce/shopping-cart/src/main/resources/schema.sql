CREATE SCHEMA IF NOT EXISTS shopping_cart;

-- Таблица корзин
CREATE TABLE IF NOT EXISTS shopping_cart.carts (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Таблица товаров в корзине
CREATE TABLE IF NOT EXISTS shopping_cart.cart_products (
    cart_id UUID NOT NULL REFERENCES shopping_cart.carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    CONSTRAINT pk_cart_products PRIMARY KEY (cart_id, product_id)
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_carts_username
    ON shopping_cart.carts(username);
CREATE INDEX IF NOT EXISTS idx_cart_products_cart
    ON shopping_cart.cart_products(cart_id);
