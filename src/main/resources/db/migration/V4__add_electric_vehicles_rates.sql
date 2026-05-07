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

-- =============================================================================
-- Утилизационный сбор для электромобилей
-- Источник: Постановление Правительства РФ № 1713 от 01.11.2025
-- Для электромобилей объём двигателя не учитывается, только мощность
-- =============================================================================

-- Льготный тариф для электромобилей (мощность ≤ 160 л.с. / 117.7 кВт)
INSERT INTO recycling_fee_rates (base_rate_rub, age_category, engine_volume_from_cc, engine_volume_to_cc, engine_power_from_kw, engine_power_to_kw, coefficient, is_preferential, valid_from) VALUES
(20000, 'NEW', NULL, NULL, 0, 117.7, 0.17, TRUE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 0, 117.7, 0.26, TRUE, '2025-12-01');

-- Общий тариф для электромобилей (мощность > 160 л.с.)
-- Новые электромобили (до 3 лет) - прогрессивная шкала по мощности
INSERT INTO recycling_fee_rates (base_rate_rub, age_category, engine_volume_from_cc, engine_volume_to_cc, engine_power_from_kw, engine_power_to_kw, coefficient, is_preferential, valid_from) VALUES
(20000, 'NEW', NULL, NULL, 0, 22.07, 33.37, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 22.08, 44.14, 66.74, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 44.15, 66.21, 100.11, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 66.22, 88.28, 133.48, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 88.29, 110.35, 166.85, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 110.36, 132.42, 200.22, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 132.43, 154.49, 233.59, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 154.50, 176.56, 266.96, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 176.57, 198.63, 300.33, FALSE, '2025-12-01'),
(20000, 'NEW', NULL, NULL, 198.64, NULL, 333.70, FALSE, '2025-12-01');

-- Б/у электромобили (старше 3 лет) - прогрессивная шкала по мощности
INSERT INTO recycling_fee_rates (base_rate_rub, age_category, engine_volume_from_cc, engine_volume_to_cc, engine_power_from_kw, engine_power_to_kw, coefficient, is_preferential, valid_from) VALUES
(20000, 'USED', NULL, NULL, 0, 22.07, 51.05, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 22.08, 44.14, 102.10, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 44.15, 66.21, 153.15, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 66.22, 88.28, 204.20, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 88.29, 110.35, 255.25, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 110.36, 132.42, 306.30, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 132.43, 154.49, 357.35, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 154.50, 176.56, 408.40, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 176.57, 198.63, 459.45, FALSE, '2025-12-01'),
(20000, 'USED', NULL, NULL, 198.64, NULL, 510.50, FALSE, '2025-12-01');

-- =============================================================================
-- Комментарии
-- =============================================================================
COMMENT ON COLUMN recycling_fee_rates.engine_volume_from_cc IS 'Нижняя граница объёма двигателя в куб.см (NULL для электромобилей)';
COMMENT ON COLUMN recycling_fee_rates.engine_volume_to_cc IS 'Верхняя граница объёма двигателя в куб.см (NULL для электромобилей или без ограничения)';
