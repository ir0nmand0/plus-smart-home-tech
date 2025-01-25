-- SQL скрипт для PostgreSQL 17.2 (официальный Docker-образ)
-- Этот скрипт создаёт схему и таблицы для микросервиса shopping-store

-- Включение расширения pgcrypto для генерации UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Создание схемы для интернет-магазина
CREATE SCHEMA IF NOT EXISTS shopping_store;

-- Таблица для хранения информации о товарах
CREATE TABLE IF NOT EXISTS shopping_store.products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),     -- Уникальный идентификатор товара
    product_name VARCHAR(255) NOT NULL,                -- Название товара
    description TEXT,                                  -- Описание товара
    price DOUBLE PRECISION NOT NULL CHECK (price > 0),  -- Цена товара
    product_category VARCHAR(50) NOT NULL              -- Категория товара
    CHECK (product_category IN ('CONTROL', 'SENSORS', 'LIGHTING')),
    quantity_state VARCHAR(20) NOT NULL                -- Степень доступности
    CHECK (quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    product_state VARCHAR(20) NOT NULL                 -- Статус товара
    DEFAULT 'ACTIVE'
    CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),
    rating DOUBLE PRECISION                            -- Рейтинг товара
    DEFAULT 5.0
    CHECK (rating BETWEEN 1.0 AND 5.0),           -- По спецификации минимум 1.0
    image_src TEXT                                     -- Ссылка на изображение
);

-- Индексы
CREATE INDEX IF NOT EXISTS idx_products_category
    ON shopping_store.products (product_category);

CREATE INDEX IF NOT EXISTS idx_products_state
    ON shopping_store.products (product_state);

CREATE INDEX IF NOT EXISTS idx_products_quantity_state
    ON shopping_store.products (quantity_state);