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

--changeset boleta:2
INSERT INTO boleta (orden_id, usuario_id, subtotal, iva, total, fecha_emision, numero_boleta) VALUES
(1, 1, 100000, 19000, 119000, '2026-06-01', 'BOL-1001'),
(2, 1, 250000, 47500, 297500, '2026-06-02', 'BOL-1002'),
(3, 2, 300000, 57000, 357000, '2026-06-03', 'BOL-1003'),
(4, 2, 180000, 34200, 214200, '2026-06-04', 'BOL-1004'),
(5, 3, 420000, 79800, 499800, '2026-06-05', 'BOL-1005');