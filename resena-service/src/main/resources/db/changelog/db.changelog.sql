-- liquibase formatted sql

-- changeset tu-nombre:01-crear-tabla-resena
CREATE TABLE resena (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    gpu_id BIGINT NOT NULL,
    comentario VARCHAR(255),
    calificacion INT,
    fecha DATE
);

-- changeset 2
INSERT INTO resena (usuario_id, gpu_id, comentario, calificacion, fecha) VALUES
(1, 101, 'Increíble rendimiento en 4K. La mejor GPU que he tenido.', 5, '2026-06-15'),
(2, 105, 'Muy buena relación calidad-precio, ideal para 1080p.', 4, '2026-06-16'),
(3, 102, 'Hace un poco de ruido bajo carga, pero rinde bien.', 4, '2026-06-17'),
(4, 101, 'La tarjeta funciona 10/10, los FPS en juegos competitivos vuelan.', 5, '2026-06-18'),
(5, 108, 'Tuve problemas con los drivers al principio, tuve que instalar versiones anteriores.', 3, '2026-06-19'),
(1, 103, 'Excelente para renderizado 3D y edición de video.', 5, '2026-06-20'),
(6, 105, 'Se calienta demasiado en mi gabinete, tuve que mejorar el flujo de aire.', 3, '2026-06-21'),
(7, 110, 'No vale lo que cuesta, hay mejores opciones por este precio.', 2, '2026-06-21'),
(8, 102, 'Funciona perfecto con mi fuente de 650W. Cero quejas.', 5, '2026-06-22'),
(9, 104, 'Llegó con un defecto de fábrica en los ventiladores. Ya hablé con soporte.', 1, '2026-06-22');
-- ojo con el 101 de gpus