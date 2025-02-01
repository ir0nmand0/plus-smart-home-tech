-- Создание БД и пользователей
CREATE DATABASE shopping_cart;
CREATE DATABASE shopping_store;
CREATE DATABASE warehouse;
CREATE DATABASE analyzer;

CREATE USER cart_user WITH PASSWORD 'cart_pass';
CREATE USER store_user WITH PASSWORD 'store_pass';
CREATE USER warehouse_user WITH PASSWORD 'warehouse_pass';
CREATE USER analyzer_user WITH PASSWORD 'analyzer_pass';

ALTER DATABASE shopping_cart OWNER TO cart_user;
ALTER DATABASE shopping_store OWNER TO store_user;
ALTER DATABASE warehouse OWNER TO warehouse_user;
ALTER DATABASE analyzer OWNER TO analyzer_user;

-- Настройка схем и прав
CREATE SCHEMA IF NOT EXISTS cart_schema AUTHORIZATION cart_user;
CREATE SCHEMA IF NOT EXISTS store_schema AUTHORIZATION store_user;
CREATE SCHEMA IF NOT EXISTS warehouse_schema AUTHORIZATION warehouse_user;
CREATE SCHEMA IF NOT EXISTS analyzer_schema AUTHORIZATION analyzer_user;

-- Настройка прав для каждой схемы
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

-- Установка дефолтных прав
ALTER DEFAULT PRIVILEGES GRANT ALL ON TABLES TO cart_user;
ALTER DEFAULT PRIVILEGES GRANT ALL ON SEQUENCES TO cart_user;
ALTER DEFAULT PRIVILEGES GRANT ALL ON TABLES TO store_user;
ALTER DEFAULT PRIVILEGES GRANT ALL ON SEQUENCES TO store_user;
ALTER DEFAULT PRIVILEGES GRANT ALL ON TABLES TO warehouse_user;
ALTER DEFAULT PRIVILEGES GRANT ALL ON SEQUENCES TO warehouse_user;
ALTER DEFAULT PRIVILEGES GRANT ALL ON TABLES TO analyzer_user;
ALTER DEFAULT PRIVILEGES GRANT ALL ON SEQUENCES TO analyzer_user;