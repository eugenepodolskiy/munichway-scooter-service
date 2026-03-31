CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    balance DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE -- Добавлена недостающая колонка
);

CREATE TABLE scooters (
    id BIGSERIAL PRIMARY KEY,
    model_name VARCHAR(255),
    location VARCHAR(255),
    battery_level INTEGER NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE trips (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    scooter_id BIGINT REFERENCES scooters(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    total_cost DOUBLE PRECISION
);