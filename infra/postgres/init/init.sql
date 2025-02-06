/*
 * Инициализационный скрипт для создания всех баз данных, пользователей и схем микросервисов.
 *
 * ВАЖНО: Spring Boot имеет проблемы с обработкой DO секций в SQL скриптах.
 * Для использования в Spring Boot необходимо создать отдельные schema.sql 
 * файлы для каждого сервиса без использования DO блоков.
 * Данный скрипт предназначен для прямого выполнения в PostgreSQL.
 */

-- Создание баз данных с проверкой существования
SELECT 'CREATE DATABASE shopping_cart' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'shopping_cart')\gexec
SELECT 'CREATE DATABASE shopping_store' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'shopping_store')\gexec
SELECT 'CREATE DATABASE warehouse' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'warehouse')\gexec
SELECT 'CREATE DATABASE analyzer' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'analyzer')\gexec
SELECT 'CREATE DATABASE order_service' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'order_service')\gexec
SELECT 'CREATE DATABASE payment_service' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'payment_service')\gexec
SELECT 'CREATE DATABASE delivery_service' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'delivery_service')\gexec

-- Создание пользователей с проверкой существования
DO $$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'cart_user') THEN
       CREATE USER cart_user WITH PASSWORD 'cart_pass';
   END IF;
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'store_user') THEN
       CREATE USER store_user WITH PASSWORD 'store_pass';
   END IF;
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'warehouse_user') THEN
       CREATE USER warehouse_user WITH PASSWORD 'warehouse_pass';
   END IF;
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'analyzer_user') THEN
       CREATE USER analyzer_user WITH PASSWORD 'analyzer_pass';
   END IF;
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'order_user') THEN
       CREATE USER order_user WITH PASSWORD 'order_pass';
   END IF;
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'payment_user') THEN
       CREATE USER payment_user WITH PASSWORD 'payment_pass';
   END IF;
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'delivery_user') THEN
       CREATE USER delivery_user WITH PASSWORD 'delivery_pass';
   END IF;
END
$$;

-- Назначение владельцев БД
ALTER DATABASE shopping_cart OWNER TO cart_user;
ALTER DATABASE shopping_store OWNER TO store_user;
ALTER DATABASE warehouse OWNER TO warehouse_user;
ALTER DATABASE analyzer OWNER TO analyzer_user;
ALTER DATABASE order_service OWNER TO order_user;
ALTER DATABASE payment_service OWNER TO payment_user;
ALTER DATABASE delivery_service OWNER TO delivery_user;

-- Создание схем
CREATE SCHEMA IF NOT EXISTS cart_schema AUTHORIZATION cart_user;
CREATE SCHEMA IF NOT EXISTS store_schema AUTHORIZATION store_user;
CREATE SCHEMA IF NOT EXISTS warehouse_schema AUTHORIZATION warehouse_user;
CREATE SCHEMA IF NOT EXISTS analyzer_schema AUTHORIZATION analyzer_user;
CREATE SCHEMA IF NOT EXISTS order_schema AUTHORIZATION order_user;
CREATE SCHEMA IF NOT EXISTS payment_schema AUTHORIZATION payment_user;
CREATE SCHEMA IF NOT EXISTS delivery_schema AUTHORIZATION delivery_user;

-- Настройка прав и search_path
GRANT ALL ON SCHEMA cart_schema TO cart_user;
GRANT ALL ON SCHEMA store_schema TO store_user;
GRANT ALL ON SCHEMA warehouse_schema TO warehouse_user;
GRANT ALL ON SCHEMA analyzer_schema TO analyzer_user;
GRANT ALL ON SCHEMA order_schema TO order_user;
GRANT ALL ON SCHEMA payment_schema TO payment_user;
GRANT ALL ON SCHEMA delivery_schema TO delivery_user;

ALTER USER cart_user SET search_path TO cart_schema, public;
ALTER USER store_user SET search_path TO store_schema, public;
ALTER USER warehouse_user SET search_path TO warehouse_schema, public;
ALTER USER analyzer_user SET search_path TO analyzer_schema, public;
ALTER USER order_user SET search_path TO order_schema, public;
ALTER USER payment_user SET search_path TO payment_schema, public;
ALTER USER delivery_user SET search_path TO delivery_schema, public;

-- Отзыв прав на public схему
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;

-- Установка дефолтных прав в схемах
ALTER DEFAULT PRIVILEGES FOR USER cart_user IN SCHEMA cart_schema
   GRANT ALL ON TABLES TO cart_user;
ALTER DEFAULT PRIVILEGES FOR USER cart_user IN SCHEMA cart_schema
   GRANT ALL ON SEQUENCES TO cart_user;

ALTER DEFAULT PRIVILEGES FOR USER store_user IN SCHEMA store_schema
   GRANT ALL ON TABLES TO store_user;
ALTER DEFAULT PRIVILEGES FOR USER store_user IN SCHEMA store_schema
   GRANT ALL ON SEQUENCES TO store_user;

ALTER DEFAULT PRIVILEGES FOR USER warehouse_user IN SCHEMA warehouse_schema
   GRANT ALL ON TABLES TO warehouse_user;
ALTER DEFAULT PRIVILEGES FOR USER warehouse_user IN SCHEMA warehouse_schema
   GRANT ALL ON SEQUENCES TO warehouse_user;

ALTER DEFAULT PRIVILEGES FOR USER analyzer_user IN SCHEMA analyzer_schema
   GRANT ALL ON TABLES TO analyzer_user;
ALTER DEFAULT PRIVILEGES FOR USER analyzer_user IN SCHEMA analyzer_schema
   GRANT ALL ON SEQUENCES TO analyzer_user;

ALTER DEFAULT PRIVILEGES FOR USER order_user IN SCHEMA order_schema
   GRANT ALL ON TABLES TO order_user;
ALTER DEFAULT PRIVILEGES FOR USER order_user IN SCHEMA order_schema
   GRANT ALL ON SEQUENCES TO order_user;

ALTER DEFAULT PRIVILEGES FOR USER payment_user IN SCHEMA payment_schema
   GRANT ALL ON TABLES TO payment_user;
ALTER DEFAULT PRIVILEGES FOR USER payment_user IN SCHEMA payment_schema
   GRANT ALL ON SEQUENCES TO payment_user;

ALTER DEFAULT PRIVILEGES FOR USER delivery_user IN SCHEMA delivery_schema
   GRANT ALL ON TABLES TO delivery_user;
ALTER DEFAULT PRIVILEGES FOR USER delivery_user IN SCHEMA delivery_schema
   GRANT ALL ON SEQUENCES TO delivery_user;

-- Установка владельца схемы
ALTER SCHEMA cart_schema OWNER TO cart_user;
ALTER SCHEMA store_schema OWNER TO store_user;
ALTER SCHEMA warehouse_schema OWNER TO warehouse_user;
ALTER SCHEMA analyzer_schema OWNER TO analyzer_user;
ALTER SCHEMA order_schema OWNER TO order_user;
ALTER SCHEMA payment_schema OWNER TO payment_user;
ALTER SCHEMA delivery_schema OWNER TO delivery_user;

-- Создание ENUM типов
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'order_state') THEN
        CREATE TYPE order_schema.order_state AS ENUM (
            'NEW',               -- Новый заказ
            'ON_PAYMENT',        -- Ожидает оплаты
            'ON_DELIVERY',       -- Ожидает доставки
            'DONE',             -- Выполнен
            'DELIVERED',         -- Доставлен
            'ASSEMBLED',         -- Собран
            'PAID',             -- Оплачен
            'COMPLETED',         -- Завершён
            'DELIVERY_FAILED',   -- Неудачная доставка
            'ASSEMBLY_FAILED',   -- Неудачная сборка
            'PAYMENT_FAILED',    -- Неудачная оплата
            'PRODUCT_RETURNED',  -- Возврат товаров
            'CANCELED'          -- Отменён
        );
    END IF;
END
$$;

DO $$
BEGIN
    CREATE TYPE payment_schema.payment_status AS ENUM (
        'PENDING',   -- Ожидает оплаты
        'SUCCESS',   -- Успешно оплачен
        'FAILED'     -- Ошибка в процессе оплаты
    );
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

DO $$
BEGIN
    CREATE TYPE delivery_schema.delivery_state AS ENUM (
        'CREATED',
        'IN_PROGRESS',
        'DELIVERED',
        'FAILED',
        'CANCELLED'
    );
EXCEPTION
    WHEN duplicate_object THEN NULL;
END
$$;

-- Создание таблиц для сервиса заказов
CREATE TABLE IF NOT EXISTS order_schema.orders (
    order_id         UUID PRIMARY KEY,
    shopping_cart_id UUID,
    delivery_id      UUID,
    payment_id       UUID,
    username         VARCHAR(255) NOT NULL,
    state            order_schema.order_state NOT NULL,
    delivery_weight  DOUBLE PRECISION,
    delivery_volume  DOUBLE PRECISION,
    fragile         BOOLEAN DEFAULT false,
    total_price     DOUBLE PRECISION,
    delivery_price  DOUBLE PRECISION,
    product_price   DOUBLE PRECISION,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

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

-- Создание таблиц для сервиса оплаты
CREATE TABLE IF NOT EXISTS payment_schema.payments (
    payment_id      UUID PRIMARY KEY,
    order_id        UUID NOT NULL,
    total_payment   DOUBLE PRECISION NOT NULL,
    delivery_total  DOUBLE PRECISION NOT NULL,
    product_total   DOUBLE PRECISION NOT NULL,
    tax_total       DOUBLE PRECISION NOT NULL,
    status          payment_schema.payment_status NOT NULL DEFAULT 'PENDING',
    payment_method  VARCHAR(50),
    payment_details JSONB,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT positive_amounts CHECK (
        total_payment >= 0
        AND delivery_total >= 0
        AND product_total >= 0
        AND tax_total >= 0
    )
);

CREATE TABLE IF NOT EXISTS payment_schema.payment_history (
    history_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id      UUID NOT NULL,
    previous_status payment_schema.payment_status,
    new_status      payment_schema.payment_status NOT NULL,
    changed_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason          TEXT,
    FOREIGN KEY (payment_id)
        REFERENCES payment_schema.payments(payment_id)
        ON DELETE CASCADE
);

-- Создание таблиц для сервиса доставки
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

-- Создание индексов для всех сервисов
CREATE INDEX IF NOT EXISTS idx_orders_username ON order_schema.orders(username);
CREATE INDEX IF NOT EXISTS idx_orders_state ON order_schema.orders(state);
CREATE INDEX IF NOT EXISTS idx_orders_shopping_cart ON order_schema.orders(shopping_cart_id);
CREATE INDEX IF NOT EXISTS idx_orders_delivery ON order_schema.orders(delivery_id);
CREATE INDEX IF NOT EXISTS idx_orders_payment ON order_schema.orders(payment_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_schema.order_items(product_id);

CREATE INDEX IF NOT EXISTS idx_payments_order ON payment_schema.payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payment_schema.payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_created ON payment_schema.payments(created_at);
CREATE INDEX IF NOT EXISTS idx_payment_history_payment ON payment_schema.payment_history(payment_id);

CREATE INDEX IF NOT EXISTS idx_addresses_location ON delivery_schema.addresses(city, street);
CREATE INDEX IF NOT EXISTS idx_deliveries_order ON delivery_schema.deliveries(order_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_state ON delivery_schema.deliveries(delivery_state);
CREATE INDEX IF NOT EXISTS idx_delivery_history_delivery ON delivery_schema.delivery_history(delivery_id);

-- Создание функций обновления времени
CREATE OR REPLACE FUNCTION order_schema.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION payment_schema.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delivery_schema.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггеров обновления времени
CREATE OR REPLACE TRIGGER update_payments_updated_at
    BEFORE UPDATE ON payment_schema.payments
    FOR EACH ROW
    EXECUTE FUNCTION payment_schema.update_updated_at_column();

CREATE OR REPLACE TRIGGER update_deliveries_updated_at
    BEFORE UPDATE ON delivery_schema.deliveries
    FOR EACH ROW
    EXECUTE FUNCTION delivery_schema.update_updated_at_column();

CREATE OR REPLACE TRIGGER update_addresses_updated_at
    BEFORE UPDATE ON delivery_schema.addresses
    FOR EACH ROW
    EXECUTE FUNCTION delivery_schema.update_updated_at_column();