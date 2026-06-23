--liquibase formatted sql

--changeset producto:1

CREATE TABLE categoria (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255)

);
--changeset producto:2

CREATE TABLE gpu (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  marca VARCHAR(100) NOT NULL,
  modelo VARCHAR(100) NOT NULL,
  vram INT NOT NULL,
  precio DOUBLE NOT NULL,
  estado VARCHAR(50) NOT NULL,
  categoria_id BIGINT NOT NULL,
   stock BIGINT,
  CONSTRAINT fk_gpu_categoria
    FOREIGN KEY (categoria_id)
    REFERENCES categoria(id)
);

--changeset producto:3

INSERT INTO categoria (nombre, descripcion) VALUES
('Gaming', 'Tarjetas gráficas orientadas a videojuegos'),
('Diseño', 'Tarjetas gráficas para diseño, edición y modelado'),
('Oficina', 'Tarjetas gráficas para uso básico y tareas administrativas'),
('Profesional', 'Tarjetas gráficas para trabajos de alto rendimiento');

--changeset producto:4

INSERT INTO gpu (nombre, marca, modelo, vram, precio, estado, categoria_id, stock) VALUES
('RTX 3060', 'NVIDIA', 'MSI Ventus', 12, 299990, 'Nuevo', 1,10),
('RTX 4070', 'NVIDIA', 'ASUS TUF', 12, 699990, 'Nuevo', 1,5),
('RTX 4060', 'NVIDIA', 'Gigabyte Eagle', 8, 389990, 'Nuevo', 1,8),
('RX 7600', 'AMD', 'Sapphire Pulse', 8, 289990, 'Nuevo', 1,6),
('RX 7800 XT', 'AMD', 'PowerColor Fighter', 16, 649990, 'Nuevo', 1,4),
('GTX 1660 Super', 'NVIDIA', 'MSI Gaming X', 6, 199990, 'Usado', 1,3),
('RTX 3050', 'NVIDIA', 'Zotac Twin Edge', 8, 249990, 'Nuevo', 1,12),
('Quadro P2000', 'NVIDIA', 'PNY', 5, 249990, 'Usado', 2,2),
('RTX A2000', 'NVIDIA', 'PNY Professional', 6, 499990, 'Nuevo', 4,7),
('GT 1030', 'NVIDIA', 'Gigabyte Low Profile', 2, 79990, 'Nuevo', 3,15);