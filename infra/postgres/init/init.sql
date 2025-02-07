-- Главный скрипт для создания баз данных, пользователей, схем и прав.

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

-- Назначение владельцев баз данных
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

-- Назначение прав и настройка search_path
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

-- Отзыв прав на схему public
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

-- Установка владельцев схем
ALTER SCHEMA cart_schema OWNER TO cart_user;
ALTER SCHEMA store_schema OWNER TO store_user;
ALTER SCHEMA warehouse_schema OWNER TO warehouse_user;
ALTER SCHEMA analyzer_schema OWNER TO analyzer_user;
ALTER SCHEMA order_schema OWNER TO order_user;
ALTER SCHEMA payment_schema OWNER TO payment_user;
ALTER SCHEMA delivery_schema OWNER TO delivery_user;
