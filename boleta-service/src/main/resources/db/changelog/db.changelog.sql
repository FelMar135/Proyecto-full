--liquibase formatted sql

--changeset boleta:1
CREATE TABLE boleta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orden_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    subtotal DOUBLE NOT NULL,
    iva DOUBLE NOT NULL,
    total DOUBLE NOT NULL,
    fecha_emision DATE NOT NULL,
    numero_boleta VARCHAR(100) NOT NULL
);
