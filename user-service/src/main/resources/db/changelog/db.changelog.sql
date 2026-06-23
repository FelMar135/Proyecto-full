--liquibase formatted sql

--changeset march:1
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    email VARCHAR(255),
    telefono VARCHAR(255),
    direccion VARCHAR(255)
);

--changeset march:2
INSERT INTO usuario (nombre, apellido, email, telefono, direccion) VALUES
('Felipe', 'Marchant', 'marchant@gmail.com', '957912469', 'Pasaje Tres 2345'),
('Matias', 'Acevedo', 'matiki@gmail.com', '991432574', 'Mapocho 3149'),
('Kevin', 'Angulo', 'kevinan@gmail.com', '91902458', 'Av. Cumming 1239'),
('Camila', 'Rojas', 'camilarojas@gmail.com', '978451236', 'Los Pinos 456'),
('Sebastian', 'Muñoz', 'sebamunoz@gmail.com', '965874123', 'Av. Libertador 987'),
('Valentina', 'Soto', 'valesoto@gmail.com', '952147896', 'Pasaje Central 741'),
('Diego', 'Fernandez', 'diegof@gmail.com', '987654321', 'Calle Norte 159'),
('Fernanda', 'Lopez', 'fernandalopez@gmail.com', '961258743', 'Villa Los Aromos 852'),
('Ignacio', 'Torres', 'ignaciotorres@gmail.com', '974123658', 'Av. Providencia 456'),
('Antonia', 'Silva', 'antoniasilva@gmail.com', '956321478', 'Camino El Alba 963');