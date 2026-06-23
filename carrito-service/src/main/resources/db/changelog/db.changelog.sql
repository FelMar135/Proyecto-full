--liquibase formatted sql

--changeset carrito:1
CREATE TABLE carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    gpu_id BIGINT NOT NULL,
    cantidad INT NOT NULL
);

--changeset carrito:2
INSERT INTO carrito (usuario_id, gpu_id, cantidad) VALUES
(1, 1, 2),
(1, 2, 1),
(2, 3, 1),
(2, 4, 2),
(3, 5, 1),
(3, 6, 3),
(4, 7, 1),
(4, 8, 2),
(5, 9, 1),
(5, 10, 2);