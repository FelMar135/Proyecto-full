--liquibase formatted sql

--changeset auth:1
CREATE TABLE auth_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL
);

--changeset auth:2
INSERT INTO auth_user (username, email, password, rol) VALUES
('admin', 'admin@gpustore.cl', '7c4a8d09ca3762af61e59520943dc26494f8941b', 'ADMIN'),
('matias', 'matias@gpustore.cl', '7c4a8d09ca3762af61e59520943dc26494f8941b', 'USER'),
('felipe', 'felipe@gpustore.cl', '7c4a8d09ca3762af61e59520943dc26494f8941b', 'USER');