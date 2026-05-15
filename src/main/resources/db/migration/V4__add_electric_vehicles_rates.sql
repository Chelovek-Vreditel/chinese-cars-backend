-- =============================================================================
-- Ставки таможенной пошлины для электромобилей
-- Источник: Решение Совета ЕЭК № 80 от 14.09.2021 (код ТН ВЭД 8703 80 00 02)
-- Ставка: 15% от таможенной стоимости (без привязки к объёму двигателя)
-- =============================================================================

-- Создаём отдельную таблицу для пошлин на электромобили
CREATE TABLE electric_vehicle_duty_rates (
    id                      BIGSERIAL       PRIMARY KEY,
    rate_percent            DECIMAL(5,2)    NOT NULL,
    tnved_code              VARCHAR(20)     NOT NULL,
    valid_from              DATE            NOT NULL,
    valid_to                DATE,
    
    CHECK (valid_to IS NULL OR valid_to >= valid_from)
);

CREATE INDEX idx_ev_duty_valid ON electric_vehicle_duty_rates(valid_from, valid_to);

COMMENT ON TABLE electric_vehicle_duty_rates IS 'Ставки таможенной пошлины для электромобилей (Решение Совета ЕЭК № 80 от 14.09.2021)';
COMMENT ON COLUMN electric_vehicle_duty_rates.rate_percent IS 'Ставка в % от таможенной стоимости';
COMMENT ON COLUMN electric_vehicle_duty_rates.tnved_code IS 'Код ТН ВЭД (для электромобилей: 8703 80 00 02)';

-- Вставляем ставку для электромобилей: 15%
INSERT INTO electric_vehicle_duty_rates (rate_percent, tnved_code, valid_from) VALUES
(15.00, '8703800002', '2022-01-01');
