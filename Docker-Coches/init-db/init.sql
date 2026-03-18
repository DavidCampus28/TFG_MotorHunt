-- =============================================
-- Base de Datos: MotorHunt
-- Descripción: Sistema de compraventa de coches
-- =============================================

-- Tabla de Usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(500),
    rol VARCHAR(50) NOT NULL CHECK (rol IN ('USUARIO', 'ADMINISTRADOR')),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_actividad TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Coches
CREATE TABLE coches (
    id BIGSERIAL PRIMARY KEY,
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
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_venta TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de Favoritos (Relación Muchos a Muchos)
CREATE TABLE usuario_coches_favoritos (
    usuario_id BIGINT NOT NULL,
    coche_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, coche_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE CASCADE
);

-- Tabla de Transacciones
CREATE TABLE transacciones (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('COMPRA', 'VENTA', 'INTERCAMBIO')),
    precio_final DECIMAL(10, 2) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notas VARCHAR(1000),
    completada BOOLEAN NOT NULL DEFAULT FALSE,
    coche_id BIGINT NOT NULL,
    vendedor_id BIGINT NOT NULL,
    comprador_id BIGINT NOT NULL,
    FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE CASCADE,
    FOREIGN KEY (vendedor_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (comprador_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de Mensajes
CREATE TABLE mensajes (
    id BIGSERIAL PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_lectura TIMESTAMP,
    remitente_id BIGINT NOT NULL,
    destinatario_id BIGINT NOT NULL,
    coche_id BIGINT,
    FOREIGN KEY (remitente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (destinatario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (coche_id) REFERENCES coches(id) ON DELETE SET NULL
);

-- =============================================
-- ÍNDICES para optimizar búsquedas
-- =============================================

-- Índices en Usuarios
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_activo ON usuarios(activo);

-- Índices en Coches
CREATE INDEX idx_coches_usuario_id ON coches(usuario_id);
CREATE INDEX idx_coches_marca ON coches(marca);
CREATE INDEX idx_coches_estado ON coches(estado);
CREATE INDEX idx_coches_precio ON coches(precio);
CREATE INDEX idx_coches_ubicacion ON coches(ubicacion);
CREATE INDEX idx_coches_combustible ON coches(combustible);
CREATE INDEX idx_coches_etiqueta ON coches(etiqueta_ambiental);

-- Índices en Transacciones
CREATE INDEX idx_transacciones_vendedor ON transacciones(vendedor_id);
CREATE INDEX idx_transacciones_comprador ON transacciones(comprador_id);
CREATE INDEX idx_transacciones_coche ON transacciones(coche_id);
CREATE INDEX idx_transacciones_tipo ON transacciones(tipo);
CREATE INDEX idx_transacciones_fecha ON transacciones(fecha);

-- Índices en Mensajes
CREATE INDEX idx_mensajes_remitente ON mensajes(remitente_id);
CREATE INDEX idx_mensajes_destinatario ON mensajes(destinatario_id);
CREATE INDEX idx_mensajes_leido ON mensajes(leido);
CREATE INDEX idx_mensajes_coche ON mensajes(coche_id);

-- =============================================
-- DATOS DE PRUEBA (Opcional)
-- =============================================

-- Insertar usuarios de prueba
INSERT INTO usuarios (nombre, email, password, telefono, direccion, rol, activo) VALUES
('Admin User', 'admin@motorhunt.com', 'hashed_password_123', '123456789', 'Calle Principal 1', 'ADMINISTRADOR', TRUE),
('Juan García', 'juan@example.com', 'hashed_password_456', '987654321', 'Calle Secundaria 2', 'USUARIO', TRUE),
('María López', 'maria@example.com', 'hashed_password_789', '555111222', 'Avenida Central 3', 'USUARIO', TRUE),
('Carlos Martínez', 'carlos@example.com', 'hashed_password_012', '555333444', 'Plaza Mayor 4', 'USUARIO', TRUE);

-- Insertar coches de prueba
INSERT INTO coches (marca, modelo, motor, color, tipo_cambio, combustible, numero_puertas, ubicacion, caballos_potencia, kilometros, precio, numero_plazas, centimetros_cubicos, etiqueta_ambiental, estado, descripcion, ano, usuario_id) VALUES
('Toyota', 'Corolla', '1.6L', 'Blanco', 'AUTOMATICO', 'GASOLINA', 4, 'Madrid', 130, 45000, 15000.00, 5, 1598, 'C', 'EN_VENTA', 'Coche en perfecto estado, poco uso', 2020, 2),
('BMW', '320i', '2.0L', 'Negro', 'MANUAL', 'DIESEL', 4, 'Barcelona', 150, 32000, 22000.00, 5, 1995, 'C', 'EN_VENTA', 'Sedán deportivo bien mantenido', 2019, 3),
('Renault', 'Megane', '1.5L', 'Plata', 'MANUAL', 'DIESEL', 5, 'Valencia', 110, 62000, 12000.00, 5, 1461, 'B', 'EN_VENTA', 'Familiar práctico', 2018, 4),
('Tesla', 'Model 3', 'Eléctrico', 'Rojo', 'AUTOMATICO', 'ELECTRICO', 4, 'Sevilla', 272, 15000, 38000.00, 5, 0, 'ECO', 'EN_VENTA', 'Coche eléctrico de lujo', 2021, 2),
('Hyundai', 'i30', '1.4L', 'Azul', 'AUTOMATICO', 'GASOLINA', 5, 'Bilbao', 120, 28000, 11000.00, 5, 1400, 'C', 'VENDIDO', 'Excelente economía de combustible', 2020, 3);
