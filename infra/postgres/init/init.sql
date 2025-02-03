-- Создание БД
CREATE DATABASE IF NOT EXISTS shopping_cart;
CREATE DATABASE IF NOT EXISTS shopping_store;
CREATE DATABASE IF NOT EXISTS warehouse;
CREATE DATABASE IF NOT EXISTS analyzer;

-- Создание пользователей
CREATE USER IF NOT EXISTS cart_user WITH PASSWORD 'cart_pass';
CREATE USER IF NOT EXISTS store_user WITH PASSWORD 'store_pass';
CREATE USER IF NOT EXISTS warehouse_user WITH PASSWORD 'warehouse_pass';
CREATE USER IF NOT EXISTS analyzer_user WITH PASSWORD 'analyzer_pass';

-- Назначение владельцев БД
ALTER DATABASE shopping_cart OWNER TO cart_user;
ALTER DATABASE shopping_store OWNER TO store_user;
ALTER DATABASE warehouse OWNER TO warehouse_user;
ALTER DATABASE analyzer OWNER TO analyzer_user;

-- Создание схем
CREATE SCHEMA IF NOT EXISTS cart_schema AUTHORIZATION cart_user;
CREATE SCHEMA IF NOT EXISTS store_schema AUTHORIZATION store_user;
CREATE SCHEMA IF NOT EXISTS warehouse_schema AUTHORIZATION warehouse_user;
CREATE SCHEMA IF NOT EXISTS analyzer_schema AUTHORIZATION analyzer_user;

-- Выдача прав на схемы
GRANT ALL ON SCHEMA cart_schema TO cart_user;
GRANT ALL ON SCHEMA store_schema TO store_user;
GRANT ALL ON SCHEMA warehouse_schema TO warehouse_user;
GRANT ALL ON SCHEMA analyzer_schema TO analyzer_user;

-- Настройка search_path
ALTER USER cart_user SET search_path TO cart_schema;
ALTER USER store_user SET search_path TO store_schema;
ALTER USER warehouse_user SET search_path TO warehouse_schema;
ALTER USER analyzer_user SET search_path TO analyzer_schema;

-- Отзыв прав на public схему
REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE CREATE ON SCHEMA public FROM PUBLIC;

-- Установка дефолтных прав в схемах
ALTER DEFAULT PRIVILEGES IN SCHEMA cart_schema GRANT ALL ON TABLES TO cart_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA cart_schema GRANT ALL ON SEQUENCES TO cart_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA store_schema GRANT ALL ON TABLES TO store_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA store_schema GRANT ALL ON SEQUENCES TO store_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA warehouse_schema GRANT ALL ON TABLES TO warehouse_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA warehouse_schema GRANT ALL ON SEQUENCES TO warehouse_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA analyzer_schema GRANT ALL ON TABLES TO analyzer_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA analyzer_schema GRANT ALL ON SEQUENCES TO analyzer_user;