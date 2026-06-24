--liquibase formatted sql

--changeset march:1
CREATE TABLE orden (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    carrito_id BIGINT NOT NULL,
    total DOUBLE NOT NULL,
    estado VARCHAR(30) NOT NULL
);

--changeset march:2
INSERT INTO orden (usuario_id, carrito_id, total, estado) VALUES 
(1, 201, 1599.99, 'PAGADO'),
(2, 202, 349.50, 'PENDIENTE'),
(3, 203, 899.00, 'ENVIADO'),
(4, 204, 2100.75, 'ENTREGADO'),
(5, 205, 120.00, 'CANCELADO'),
(6, 206, 450.25, 'PENDIENTE'),
(7, 207, 3200.00, 'PAGADO'),
(8, 208, 150.00, 'ENVIADO'),
(1, 209, 850.50, 'ENTREGADO'),
(2, 210, 500.00, 'CANCELADO');