-- =============================================================================
-- Таблица технических характеристик конфигураций для расчётов
-- =============================================================================
CREATE TABLE configuration_technical_specs (
    configuration_id    BIGINT          PRIMARY KEY,
    engine_volume_cc    INT             NOT NULL,
    engine_power_hp     INT             NOT NULL,
    engine_power_kw     DECIMAL(6,2)    NOT NULL,
    engine_type         VARCHAR(20)     NOT NULL CHECK (engine_type IN ('PETROL', 'DIESEL', 'ELECTRIC', 'HYBRID')),
    manufacture_year    INT             NOT NULL,
    tnved_code          VARCHAR(20),
    
    FOREIGN KEY (configuration_id) REFERENCES cars_configurations(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_tech_specs_engine_type ON configuration_technical_specs(engine_type);
CREATE INDEX idx_tech_specs_manufacture_year ON configuration_technical_specs(manufacture_year);

-- =============================================================================
-- Справочник ставок единой таможенной пошлины
-- =============================================================================
CREATE TABLE customs_duty_rates (
    id                      BIGSERIAL       PRIMARY KEY,
    age_category            VARCHAR(20)     NOT NULL CHECK (age_category IN ('NEW', 'USED_3_5', 'USED_5_PLUS')),
    engine_volume_from_cc   INT             NOT NULL,
    engine_volume_to_cc     INT,
    rate_percent            DECIMAL(5,2),
    rate_eur_per_cc         DECIMAL(8,4)    NOT NULL,
    valid_from              DATE            NOT NULL,
    valid_to                DATE,
    
    CHECK (engine_volume_to_cc IS NULL OR engine_volume_to_cc >= engine_volume_from_cc),
    CHECK (valid_to IS NULL OR valid_to >= valid_from)
);

CREATE INDEX idx_customs_duty_age ON customs_duty_rates(age_category);
CREATE INDEX idx_customs_duty_valid ON customs_duty_rates(valid_from, valid_to);

-- =============================================================================
-- Справочник таможенных сборов
-- =============================================================================
CREATE TABLE customs_fee_rates (
    id                      BIGSERIAL       PRIMARY KEY,
    cost_rub_from           DECIMAL(10,2)   NOT NULL,
    cost_rub_to             DECIMAL(10,2)   NOT NULL,
    fee_rub                 DECIMAL(10,2)   NOT NULL
);

-- =============================================================================
-- Справочник утилизационного сбора
-- =============================================================================
CREATE TABLE recycling_fee_rates (
    id                      BIGSERIAL       PRIMARY KEY,
    is_electrical           BOOLEAN         NOT NULL,
    engine_volume_l_from    DECIMAL(4,2),
    engine_volume_l_to      DECIMAL(4,2),
    engine_power_hp_from    INT             NOT NULL,
    engine_power_hp_to      INT             NOT NULL,
    coef_less               DECIMAL(10,2)   NOT NULL,
    coef_more               DECIMAL(10,2)   NOT NULL
);

CREATE TABLE import_settings (
    key                     TEXT            PRIMARY_KEY,
    value                   DECIMAL(12,2)   NOT NULL
);

-- =============================================================================
-- Комментарии к таблицам
-- =============================================================================
COMMENT ON TABLE configuration_technical_specs IS 'Технические характеристики конфигураций для расчёта таможенных платежей';
COMMENT ON TABLE customs_duty_rates IS 'Ставки единой таможенной пошлины (Решение Совета ЕЭК № 107 от 20.12.2017)';
COMMENT ON TABLE customs_fee_rates IS 'Ставки таможенных сборов (Постановление Правительства РФ № 1638 от 23.10.2025)';
COMMENT ON TABLE recycling_fee_rates IS 'Ставки утилизационного сбора (Постановление Правительства РФ № 1713 от 01.11.2025)';
COMMENT ON TABLE import_settings IS 'Настройки для расчёта импорта (стоимость доставки, процент страховки и т.д.)';
