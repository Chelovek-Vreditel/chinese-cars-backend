CREATE TABLE cars (
    id                  BIGSERIAL       PRIMARY KEY,
    brand               VARCHAR(25)     NOT NULL,
    series              VARCHAR(50),
    model               VARCHAR(255)    NOT NULL,
    base_price_cny      DECIMAL(10,2)   NOT NULL,
    description         TEXT,
    source_url          TEXT,

    UNIQUE (brand, model)
);

CREATE TABLE cars_configurations (
    id                  BIGSERIAL       PRIMARY KEY,
    car_id              BIGINT          NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    base_price_cny      DECIMAL(10,2)   NOT NULL,

    FOREIGN KEY (car_id) REFERENCES cars(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE (car_id, name)
);

CREATE TABLE configuration_options (
    id                  BIGSERIAL       PRIMARY KEY,
    configuration_id    BIGINT          NOT NULL,
    category            VARCHAR(255),
    name                VARCHAR(255)    NOT NULL,
    value               VARCHAR(255)    NOT NULL, -- Значение/included/is_optional/none
    price_cny           DECIMAL(10,2),

    FOREIGN KEY (configuration_id) REFERENCES cars_configurations(id) 
    ON DELETE CASCADE ON UPDATE CASCADE, 
    UNIQUE (configuration_id, name)
);

CREATE TABLE cars_update_times (
    car_id              BIGINT          PRIMARY KEY,
    updated_at          TIMESTAMP       NOT NULL,

    FOREIGN KEY (car_id) REFERENCES cars(id) 
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE cars_configurations_update_times (
    car_configuration_id    BIGINT          PRIMARY KEY,
    updated_at              TIMESTAMP       NOT NULL,

    FOREIGN KEY (car_configuration_id) REFERENCES cars_configurations(id) 
    ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE configuration_options_update_times (
    configuration_option_id BIGINT          PRIMARY KEY,
    updated_at              TIMESTAMP       NOT NULL,

    FOREIGN KEY (configuration_option_id) REFERENCES configuration_options(id) 
    ON DELETE CASCADE ON UPDATE CASCADE
);
