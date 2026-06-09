--liquibase formatted sql

--changeset admin:init-001
CREATE TABLE IF NOT EXISTS "surfaces"
(
    `id`                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    `minute_price`      DECIMAL(10, 2) NOT NULL,
    `name`              VARCHAR(150) NOT NULL,
    `creation_date`     TIMESTAMP NOT NULL,
    `modification_date` TIMESTAMP NOT NULL,
    `deleted`           BOOL NOT NULL,
    UNIQUE (`name`)

);

--changeset admin:init-002
CREATE TABLE IF NOT EXISTS "users"
(
    `id`                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    `name`              VARCHAR(150) NOT NULL,
    `phone_number`      VARCHAR(150) NOT NULL,
    `password`          VARCHAR(150) NOT NULL,
    `creation_date`     TIMESTAMP NOT NULL,
    `modification_date` TIMESTAMP NOT NULL,
    `deleted`           BOOL NOT NULL,
    UNIQUE (`phone_number`)
);

--changeset admin:init-003
CREATE TABLE IF NOT EXISTS "courts"
(
    `id`                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    `name`              VARCHAR(150) NOT NULL,
    `number`            INT NOT NULL,
    `creation_date`     TIMESTAMP NOT NULL,
    `modification_date` TIMESTAMP NOT NULL,
    `surface_id`        BIGINT REFERENCES "surfaces"(`id`),
    `deleted`           BOOL NOT NULL,
    UNIQUE (`number`)
);

--changeset admin:init-004
CREATE TABLE IF NOT EXISTS "reservations"
(
    `id`                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    `creation_date`     TIMESTAMP NOT NULL,
    `modification_date` TIMESTAMP NOT NULL,
    `from_date`         TIMESTAMP NOT NULL,
    `to_date`           TIMESTAMP NOT NULL,
    `user_id`           BIGINT REFERENCES "users"(`id`),
    `court_id`          BIGINT REFERENCES "courts"(`id`),
    `game_type`         ENUM('SINGLES', 'DOUBLES') NOT NULL,
    `deleted`           BOOL NOT NULL
);
