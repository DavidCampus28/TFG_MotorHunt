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
    numero_denuncias INT NOT NULL DEFAULT 0,
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    motivo_bloqueado VARCHAR(500),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_actividad TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_usuarios_email (email),
    INDEX idx_usuarios_rol (rol),
    INDEX idx_usuarios_activo (activo),
    INDEX idx_usuarios_bloqueado (bloqueado),
    INDEX idx_usuarios_denuncias (numero_denuncias)
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
    etiqueta_ambiental VARCHAR(50) NOT NULL CHECK (etiqueta_ambiental IN ('B', 'C', 'ECO', 'CERO')),
    estado VARCHAR(50) NOT NULL DEFAULT 'EN_VENTA' CHECK (estado IN ('EN_VENTA', 'VENDIDO', 'RESERVADO', 'FUERA_SERVICIO')),
    numero_denuncias INT NOT NULL DEFAULT 0,
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    motivo_bloqueado VARCHAR(500),
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
    INDEX idx_coches_etiqueta (etiqueta_ambiental),
    INDEX idx_coches_bloqueado (bloqueado),
    INDEX idx_coches_denuncias (numero_denuncias)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla de Fotos de Coches
-- =============================================
CREATE TABLE coche_fotos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contenido LONGBLOB NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    portada BOOLEAN NOT NULL DEFAULT FALSE,
    orden INT NOT NULL DEFAULT 0,
    fecha_subida TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    coche_id BIGINT NOT NULL,
    CONSTRAINT fk_coche_fotos_coche FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE CASCADE,
    INDEX idx_coche_fotos_coche (coche_id),
    INDEX idx_coche_fotos_portada (coche_id, portada),
    INDEX idx_coche_fotos_orden (coche_id, orden)
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
('Admin User', 'admin@motorhunt.com', 'admin', '123456789', 'Calle Principal 1', 'ADMINISTRADOR', TRUE),
('Juan García', 'juan@example.com', 'hashed_password_456', '987654321', 'Calle Secundaria 2', 'USUARIO', TRUE),
('María López', 'maria@example.com', 'hashed_password_789', '555111222', 'Avenida Central 3', 'USUARIO', TRUE),
('Carlos Martínez', 'carlos@example.com', 'hashed_password_012', '555333444', 'Plaza Mayor 4', 'USUARIO', TRUE);
UPDATE usuarios SET password = '$2a$10$slYQmyNdGzin7olVN3BK2OPST9/PgBkqquzi.Gy6/a5LkB.o5xI4a' WHERE nombre = 'Admin User';
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

-- =============================================
-- Tabla de Me Gustas
-- =============================================
CREATE TABLE IF NOT EXISTS me_gustas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    coche_id BIGINT NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_me_gustas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_me_gustas_coche FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE CASCADE,
    UNIQUE KEY uk_me_gustas (usuario_id, coche_id),
    INDEX idx_me_gustas_usuario (usuario_id),
    INDEX idx_me_gustas_coche (coche_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla de Denuncias
-- =============================================
CREATE TABLE IF NOT EXISTS denuncias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('FRAUDE', 'SPAM', 'COCHE_INEXISTENTE', 'PRECIO_FALSO', 'COMPORTAMIENTO_SOSPECHOSO', 'CONTENIDO_INAPROPIADO', 'ABUSO', 'OTRO')),
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'REVISANDO', 'RESUELTA', 'RECHAZADA')),
    descripcion LONGTEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP NULL,
    resolucion TEXT,
    denunciante_id BIGINT NOT NULL,
    usuario_denunciado_id BIGINT,
    coche_denunciado_id BIGINT,
    admin_revisor_id BIGINT,
    CONSTRAINT fk_denuncias_denunciante FOREIGN KEY (denunciante_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_denuncias_usuario_denunciado FOREIGN KEY (usuario_denunciado_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT fk_denuncias_coche FOREIGN KEY (coche_denunciado_id) REFERENCES coches(id) ON DELETE SET NULL,
    CONSTRAINT fk_denuncias_admin FOREIGN KEY (admin_revisor_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_denuncias_estado (estado),
    INDEX idx_denuncias_tipo (tipo),
    INDEX idx_denuncias_fecha (fecha_creacion),
    INDEX idx_denuncias_usuario_denunciado (usuario_denunciado_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla de Alertas (Antifraude)
-- =============================================
CREATE TABLE IF NOT EXISTS alertas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL CHECK (tipo IN ('MULTIPLES_CUENTAS', 'IP_SOSPECHOSA', 'ANUNCIOS_REPETIDOS', 'FRAUDE_POTENCIAL', 'COCHE_ROBADO', 'PHISHING', 'PRECIO_ANOMALO', 'KILOMETRAJE_SOSPECHOSO', 'MATRICULA_INVALIDA')),
    titulo VARCHAR(255) NOT NULL,
    descripcion LONGTEXT,
    nivel_riesgo INT NOT NULL CHECK (nivel_riesgo BETWEEN 1 AND 4),
    ip_sospechosa VARCHAR(45),
    resuelta BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion TIMESTAMP NULL,
    detalles_resolucion TEXT,
    usuario_id BIGINT,
    coche_id BIGINT,
    CONSTRAINT fk_alertas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    CONSTRAINT fk_alertas_coche FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE SET NULL,
    INDEX idx_alertas_tipo (tipo),
    INDEX idx_alertas_nivel_riesgo (nivel_riesgo),
    INDEX idx_alertas_resuelta (resuelta),
    INDEX idx_alertas_fecha (fecha_creacion),
    INDEX idx_alertas_usuario (usuario_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- Tabla de Configuración Admin
-- =============================================
CREATE TABLE IF NOT EXISTS configuracion_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url_logo VARCHAR(255) DEFAULT '/img/LogoMotorHunt.png',
    url_banner VARCHAR(255),
    texto_bienvenida TEXT,
    texto_descripcion LONGTEXT,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    admin_id BIGINT,
    CONSTRAINT fk_config_admin FOREIGN KEY (admin_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_config_fecha (fecha_actualizacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar configuración por defecto
INSERT INTO configuracion_admin (url_logo, url_banner, texto_bienvenida, texto_descripcion) VALUES
('/img/LogoMotorHunt.png', '', 'Bienvenido a MotorHunt', 'Tu plataforma de confianza para comprar y vender coches');

