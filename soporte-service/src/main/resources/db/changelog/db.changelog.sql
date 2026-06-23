-- liquibase formatted sql

-- changeset 1
CREATE TABLE soporte (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    orden_id BIGINT,
    asunto VARCHAR(255),
    descripcion TEXT,
    estado VARCHAR(50),
    fecha_creacion DATE
);

-- changeset 2
INSERT INTO soporte (usuario_id, orden_id, asunto, descripcion, estado, fecha_creacion) VALUES
(1, 501, 'Retraso en el envío', 'Mi orden debió llegar ayer según el rastreo, pero aún no aparece. ¿Me pueden ayudar?', 'ABIERTO', '2026-06-20'),
(2, 502, 'Problema de temperatura', 'La GPU que compré ayer alcanza los 90 grados jugando en 1080p. ¿Es normal o aplico garantía?', 'EN_PROGRESO', '2026-06-21'),
(3, NULL, 'Consulta sobre compatibilidad', 'Tengo una placa madre B450, ¿es compatible con la nueva serie de tarjetas que publicaron?', 'CERRADO', '2026-06-15'),
(4, 503, 'Ventilador hace ruido', 'Uno de los ventiladores de la tarjeta gráfica hace un ruido de roce constante.', 'ABIERTO', '2026-06-22'),
(5, 504, 'Error de cobro duplicado', 'Me llegaron dos notificaciones de cargo a mi tarjeta por la misma orden.', 'EN_PROGRESO', '2026-06-23'),
(1, 505, 'Solicitud de factura', 'Necesito la factura electrónica de mi última compra a nombre de mi empresa.', 'CERRADO', '2026-06-18'),
(6, NULL, 'Duda sobre stock', '¿Saben en qué fecha aproximada volverán a tener stock del modelo RX 7800 XT?', 'CERRADO', '2026-06-10'),
(7, 506, 'Producto equivocado', 'Pedí una gráfica de la marca ASUS y me llegó una de Gigabyte.', 'ABIERTO', '2026-06-23'),
(8, 507, 'Drivers no instalan', 'Windows no reconoce la tarjeta de video, ya intenté descargar los drivers de la página oficial.', 'EN_PROGRESO', '2026-06-22'),
(9, 508, 'Cancelación de orden', 'Me arrepentí de la compra, quiero cancelar mi pedido antes de que lo envíen.', 'CERRADO', '2026-06-19');

--ojo con el 501 de id orden