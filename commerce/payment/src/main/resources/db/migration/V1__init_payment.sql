-- Создание схемы для сервиса оплаты
CREATE SCHEMA IF NOT EXISTS payment_schema AUTHORIZATION payment_user;

-- Создание ENUM типа для статуса оплаты через DO блок
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payment_status') THEN
        CREATE TYPE payment_schema.payment_status AS ENUM (
            'PENDING',
            'SUCCESS',
            'FAILED'
        );
    END IF;
END
$$;

-- Создание таблицы платежей
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

-- Создание таблицы истории платежей
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

-- Создание индексов для таблиц сервиса оплаты
CREATE INDEX IF NOT EXISTS idx_payments_order ON payment_schema.payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payment_schema.payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_created ON payment_schema.payments(created_at);
CREATE INDEX IF NOT EXISTS idx_payment_history_payment ON payment_schema.payment_history(payment_id);

-- Создание функции обновления поля updated_at
CREATE OR REPLACE FUNCTION payment_schema.update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для обновления поля updated_at в таблице платежей
CREATE OR REPLACE TRIGGER update_payments_updated_at
    BEFORE UPDATE ON payment_schema.payments
    FOR EACH ROW
    EXECUTE FUNCTION payment_schema.update_updated_at_column();
