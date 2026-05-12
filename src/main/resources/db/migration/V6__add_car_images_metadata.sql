CREATE TABLE car_images (
    id           BIGSERIAL    PRIMARY KEY,
    car_id       BIGINT       NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
    storage_key  TEXT         NOT NULL UNIQUE,
    source_url   TEXT,
    content_type VARCHAR(50)  NOT NULL DEFAULT 'image/jpeg'
);

CREATE INDEX idx_car_images_car_id ON car_images(car_id);
