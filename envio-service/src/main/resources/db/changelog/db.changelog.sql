--liquibase formatted sql

--changeset matias:1
CREATE TABLE envio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    orden_id BIGINT NOT NULL,
    direccion_envio VARCHAR(150) NOT NULL,
    comuna VARCHAR(80) NOT NULL,
    ciudad VARCHAR(80) NOT NULL,
    empresa_transportista VARCHAR(80) NOT NULL,
    numero_seguimiento VARCHAR(80),
    estado VARCHAR(30) NOT NULL,
    fecha_envio DATETIME NOT NULL,
    fecha_entrega_estimada DATETIME
);

