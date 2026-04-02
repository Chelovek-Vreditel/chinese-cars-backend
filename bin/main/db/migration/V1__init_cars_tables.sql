CREATE TABLE cars (
    id                  BIGSERIAL       PRIMARY KEY,
    brand               VARCHAR(25)     NOT NULL,
    series              VARCHAR(50),
    model               VARCHAR(255)    UNIQUE NOT NULL,
    base_price_cny      DECIMAL(10,2)   NOT NULL,
    description         TEXT,
    source_url          TEXT,
    created_dt          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP       NOT NULL
);

CREATE TABLE cars_configurations (
    id                  BIGSERIAL       PRIMARY KEY,
    car_id              BIGINT          REFERENCES cars(id) ON DELETE CASCADE,
    name                VARCHAR(255)    NOT NULL,
    base_price_cny      DECIMAL(10,2)        NOT NULL
);

CREATE TABLE configuration_options (
    id                  BIGSERIAL       PRIMARY KEY,
    configuration_id    BIGINT          REFERENCES cars_configurations(id) ON DELETE CASCADE,
    category            VARCHAR(255),
    name                VARCHAR(255)    NOT NULL,
    value               VARCHAR(255)    NOT NULL, -- Значение/included/is_optional/none
    price_cny           DECIMAL(10,2)  
);
