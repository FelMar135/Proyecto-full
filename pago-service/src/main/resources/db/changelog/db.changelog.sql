--liquibase formatted sql

--changeset matias:1
CREATE TABLE pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orden_id BIGINT NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    fecha_pago DATETIME NOT NULL
);
