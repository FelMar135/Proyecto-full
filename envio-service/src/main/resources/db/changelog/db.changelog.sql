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

--changeset matias:2
INSERT INTO envio (
    orden_id,
    direccion_envio,
    comuna,
    ciudad,
    empresa_transportista,
    numero_seguimiento,
    estado,
    fecha_envio,
    fecha_entrega_estimada
) VALUES
(1, 'Av. Providencia 1234', 'Providencia', 'Santiago', 'Chilexpress', 'CHX-1001', 'EN_PREPARACION', '2026-06-23 10:00:00', '2026-06-26 18:00:00'),
(2, 'Los Leones 550', 'Ñuñoa', 'Santiago', 'Starken', 'STK-2002', 'EN_TRANSITO', '2026-06-22 09:30:00', '2026-06-25 18:00:00'),
(3, 'Camino El Alba 890', 'Las Condes', 'Santiago', 'Blue Express', 'BLU-3003', 'ENTREGADO', '2026-06-20 11:15:00', '2026-06-23 18:00:00'),
(4, 'Av. Vicuña Mackenna 1200', 'Santiago', 'Santiago', 'Chilexpress', 'CHX-4004', 'EN_PREPARACION', '2026-06-24 09:00:00', '2026-06-27 18:00:00'),
(5, 'Av. Grecia 750', 'Ñuñoa', 'Santiago', 'Starken', 'STK-5005', 'EN_TRANSITO', '2026-06-24 10:30:00', '2026-06-27 18:00:00'),
(6, 'San Diego 450', 'Santiago', 'Santiago', 'Blue Express', 'BLU-6006', 'ENTREGADO', '2026-06-21 14:20:00', '2026-06-24 18:00:00'),
(7, 'Av. Apoquindo 3000', 'Las Condes', 'Santiago', 'Chilexpress', 'CHX-7007', 'CANCELADO', '2026-06-25 08:45:00', '2026-06-28 18:00:00'),
(8, 'Gran Avenida 8500', 'La Cisterna', 'Santiago', 'Starken', 'STK-8008', 'EN_PREPARACION', '2026-06-25 11:10:00', '2026-06-28 18:00:00'),
(9, 'Av. Independencia 980', 'Independencia', 'Santiago', 'Blue Express', 'BLU-9009', 'EN_TRANSITO', '2026-06-25 15:00:00', '2026-06-29 18:00:00'),
(10, 'Pedro de Valdivia 1500', 'Providencia', 'Santiago', 'Chilexpress', 'CHX-1010', 'ENTREGADO', '2026-06-22 13:30:00', '2026-06-25 18:00:00');