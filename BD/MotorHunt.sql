-- =============================================
-- Base de Datos: MotorHunt 
-- Descripción: Sistema de compraventa de coches
-- =============================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS motorhunt;
USE motorhunt;

-- =============================================
-- Tabla de Usuarios
-- =============================================
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(500),
    rol VARCHAR(50) NOT NULL DEFAULT 'USUARIO' CHECK (rol IN ('USUARIO', 'ADMINISTRADOR')),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_actividad TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_usuarios_email (email),
    INDEX idx_usuarios_rol (rol),
    INDEX idx_usuarios_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla de Coches
-- =============================================
CREATE TABLE coches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    motor VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    tipo_cambio VARCHAR(50) NOT NULL CHECK (tipo_cambio IN ('MANUAL', 'AUTOMATICO')),
    combustible VARCHAR(50) NOT NULL CHECK (combustible IN ('GASOLINA', 'DIESEL', 'ELECTRICO', 'HIBRIDO')),
    numero_puertas INT NOT NULL,
    ubicacion VARCHAR(255) NOT NULL,
    caballos_potencia INT NOT NULL,
    kilometros INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    numero_plazas INT NOT NULL,
    centimetros_cubicos INT NOT NULL,
    etiqueta_ambiental VARCHAR(50) NOT NULL CHECK (etiqueta_ambiental IN ('B', 'C', 'ECO', '0')),
    estado VARCHAR(50) NOT NULL DEFAULT 'EN_VENTA' CHECK (estado IN ('EN_VENTA', 'VENDIDO', 'RESERVADO', 'FUERA_SERVICIO')),
    descripcion VARCHAR(1000),
    ano INT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fecha_venta TIMESTAMP NULL,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_coches_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_coches_usuario_id (usuario_id),
    INDEX idx_coches_marca (marca),
    INDEX idx_coches_estado (estado),
    INDEX idx_coches_precio (precio),
    INDEX idx_coches_ubicacion (ubicacion),
    INDEX idx_coches_combustible (combustible),
    INDEX idx_coches_etiqueta (etiqueta_ambiental)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla de Favoritos (Relación Muchos a Muchos)
-- =============================================
CREATE TABLE usuario_coches_favoritos (
    usuario_id BIGINT NOT NULL,
    coche_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, coche_id),
    CONSTRAINT fk_favoritos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_favoritos_coche FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE CASCADE,
    INDEX idx_favoritos_coche (coche_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla de Transacciones
-- =============================================
CREATE TABLE transacciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('COMPRA', 'VENTA', 'INTERCAMBIO')),
    precio_final DECIMAL(10, 2) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notas VARCHAR(1000),
    completada BOOLEAN NOT NULL DEFAULT FALSE,
    coche_id BIGINT NOT NULL,
    vendedor_id BIGINT NOT NULL,
    comprador_id BIGINT NOT NULL,
    CONSTRAINT fk_transacciones_coche FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE CASCADE,
    CONSTRAINT fk_transacciones_vendedor FOREIGN KEY (vendedor_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_transacciones_comprador FOREIGN KEY (comprador_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_transacciones_vendedor (vendedor_id),
    INDEX idx_transacciones_comprador (comprador_id),
    INDEX idx_transacciones_coche (coche_id),
    INDEX idx_transacciones_tipo (tipo),
    INDEX idx_transacciones_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla de Mensajes
-- =============================================
CREATE TABLE mensajes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contenido LONGTEXT NOT NULL,
    fecha_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_lectura TIMESTAMP NULL,
    remitente_id BIGINT NOT NULL,
    destinatario_id BIGINT NOT NULL,
    coche_id BIGINT,
    CONSTRAINT fk_mensajes_remitente FOREIGN KEY (remitente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_mensajes_destinatario FOREIGN KEY (destinatario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_mensajes_coche FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE SET NULL,
    INDEX idx_mensajes_remitente (remitente_id),
    INDEX idx_mensajes_destinatario (destinatario_id),
    INDEX idx_mensajes_leido (leido),
    INDEX idx_mensajes_coche (coche_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- DATOS DE PRUEBA
-- =============================================

INSERT INTO usuarios (nombre, email, password, telefono, direccion, rol, activo) VALUES
('Admin User', 'admin@motorhunt.com', 'hashed_password_123', '123456789', 'Calle Principal 1', 'ADMINISTRADOR', TRUE),
('Juan García', 'juan@example.com', 'hashed_password_456', '987654321', 'Calle Secundaria 2', 'USUARIO', TRUE),
('María López', 'maria@example.com', 'hashed_password_789', '555111222', 'Avenida Central 3', 'USUARIO', TRUE),
('Carlos Martínez', 'carlos@example.com', 'hashed_password_012', '555333444', 'Plaza Mayor 4', 'USUARIO', TRUE);

INSERT INTO coches (marca, modelo, motor, color, tipo_cambio, combustible, numero_puertas, ubicacion, caballos_potencia, kilometros, precio, numero_plazas, centimetros_cubicos, etiqueta_ambiental, estado, descripcion, ano, usuario_id) VALUES
('Toyota', 'Corolla', '1.6L', 'Blanco', 'AUTOMATICO', 'GASOLINA', 4, 'Madrid', 130, 45000, 15000.00, 5, 1598, 'C', 'EN_VENTA', 'Coche en perfecto estado, poco uso', 2020, 2),
('BMW', '320i', '2.0L', 'Negro', 'MANUAL', 'DIESEL', 4, 'Barcelona', 150, 32000, 22000.00, 5, 1995, 'C', 'EN_VENTA', 'Sedán deportivo bien mantenido', 2019, 3),
('Renault', 'Megane', '1.5L', 'Plata', 'MANUAL', 'DIESEL', 5, 'Valencia', 110, 62000, 12000.00, 5, 1461, 'B', 'EN_VENTA', 'Familiar práctico', 2018, 4),
('Tesla', 'Model 3', 'Eléctrico', 'Rojo', 'AUTOMATICO', 'ELECTRICO', 4, 'Sevilla', 272, 15000, 38000.00, 5, 0, 'ECO', 'EN_VENTA', 'Coche eléctrico de lujo', 2021, 2),
('Hyundai', 'i30', '1.4L', 'Azul', 'AUTOMATICO', 'GASOLINA', 5, 'Bilbao', 120, 28000, 11000.00, 5, 1400, 'C', 'VENDIDO', 'Excelente economía de combustible', 2020, 3);

INSERT INTO usuario_coches_favoritos (usuario_id, coche_id) VALUES
(2, 1),
(3, 2),
(4, 3);

INSERT INTO transacciones (tipo, precio_final, notas, completada, coche_id, vendedor_id, comprador_id) VALUES
('VENTA', 15000.00, 'Venta completada sin incidentes', TRUE, 1, 2, 3),
('COMPRA', 22000.00, 'Compra de coche BMW', FALSE, 2, 3, 4);
