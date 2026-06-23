--liquibase formatted sql

--changeset march:1
CREATE TABLE resena (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    gpu_id BIGINT NOT NULL,
    comentario VARCHAR(255),
    calificacion INT,
    fecha DATE
);

--changeset march:3
INSERT INTO resena (usuario_id, gpu_id, comentario, calificacion, fecha) VALUES 
(3, 101, 'Una bestia para jugar en 4K. Corre todo en Ultra sin pestañear.', 5, '2026-06-01'),
(4, 102, 'Rendimiento aceptable, pero los ventiladores suenan como turbina de avión.', 3, '2026-06-05'),
(5, 103, 'Pésima experiencia. Me llegó defectuosa y da pantallazos azules.', 1, '2026-06-10'),
(6, 104, 'Excelente calidad-precio. Perfecta para armar una PC gama media.', 4, '2026-06-12'),
(7, 101, 'Ideal para renderizado 3D y edición de video pesada. Muy recomendada.', 5, '2026-06-15'),
(8, 105, 'Se calienta demasiado rápido. Tuve que mejorar la ventilación de mi gabinete.', 2, '2026-06-18'),
(9, 106, 'Diseño hermoso, luces RGB personalizables y rendimiento impecable.', 5, '2026-06-19'),
(10, 107, 'Cumple bien, pero los drivers de esta marca siempre dan dolores de cabeza.', 3, '2026-06-20'),
(2, 104, 'La mejor compra que he hecho este año, superó todas mis expectativas.', 5, '2026-06-21'),
(1, 108, 'Es buena, pero por este precio esperaba un poco más de FPS en juegos recientes.', 4, '2026-06-22');