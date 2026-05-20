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
    rol VARCHAR(50) NOT NULL DEFAULT 'USUARIO' CHECK (rol IN ('USUARIO', 'ADMINISTRADOR', 'EMPRESA')),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_vendedor VARCHAR(50) CHECK (tipo_vendedor IN ('PARTICULAR', 'EMPRESA')),
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

INSERT INTO usuarios (nombre, email, password, telefono, direccion, rol, activo, tipo_vendedor) VALUES
-- Administradores (password: 12345)
('Admin Principal', 'admin1@motorhunt.com', '$2a$10$zN1VnX54Y8urdXkK6Snfa.3vWyDcYdewd5ny7puxlL37o.yuD4Uxi', '900123456', 'Calle Principal 1, Madrid', 'ADMINISTRADOR', TRUE, NULL),
('Admin Secundario', 'admin2@motorhunt.com', '$2a$10$zN1VnX54Y8urdXkK6Snfa.3vWyDcYdewd5ny7puxlL37o.yuD4Uxi', '900123457', 'Avenida Central 2, Madrid', 'ADMINISTRADOR', TRUE, NULL),
-- Usuarios normales (password: 12345)
('Juan García López', 'juan.garcia@example.com', '$2a$10$zN1VnX54Y8urdXkK6Snfa.3vWyDcYdewd5ny7puxlL37o.yuD4Uxi', '612345678', 'Calle Secundaria 15, Barcelona', 'USUARIO', TRUE, 'PARTICULAR'),
('María López Martínez', 'maria.lopez@example.com', '$2a$10$zN1VnX54Y8urdXkK6Snfa.3vWyDcYdewd5ny7puxlL37o.yuD4Uxi', '623456789', 'Avenida Diagonal 50, Barcelona', 'USUARIO', TRUE, 'PARTICULAR'),
-- Empresa (password: 12345)
('AutoMotor Premium S.L.', 'info@automotor.es', '$2a$10$zN1VnX54Y8urdXkK6Snfa.3vWyDcYdewd5ny7puxlL37o.yuD4Uxi', '934567890', 'Polígono Industrial 3, Valencia', 'EMPRESA', TRUE, 'EMPRESA');

-- Crear muchos coches de ejemplo típicos de segunda mano en España (sin fotos - se añaden manualmente desde el panel)
INSERT INTO coches (marca, modelo, motor, color, tipo_cambio, combustible, numero_puertas, ubicacion, caballos_potencia, kilometros, precio, numero_plazas, centimetros_cubicos, etiqueta_ambiental, estado, descripcion, ano, usuario_id) VALUES
-- Coches del usuario Juan García López (10 coches de marcas españolas y populares)
('Seat', 'León', '1.4L', 'Blanco', 'MANUAL', 'GASOLINA', 5, 'Madrid', 100, 145000, 5200.00, 5, 1398, 'B', 'EN_VENTA', 'Buen estado general, cambio de aceite reciente. Pequeños arañazos.', 2012, 3),
('Peugeot', '207', '1.4L', 'Gris', 'MANUAL', 'GASOLINA', 3, 'Barcelona', 75, 165000, 3900.00, 5, 1360, 'C', 'EN_VENTA', 'Funciona perfectamente, coche de ciudad ideal. Revisión reciente.', 2008, 3),
('Ford', 'Focus', '1.6L', 'Negro', 'MANUAL', 'DIESEL', 5, 'Valencia', 105, 175000, 6200.00, 5, 1598, 'B', 'EN_VENTA', 'Bien cuidado, revisión en regla. Económico en consumo.', 2010, 3),
('Renault', 'Clio', '1.2L', 'Azul', 'MANUAL', 'GASOLINA', 5, 'Madrid', 75, 195000, 3200.00, 5, 1149, 'B', 'EN_VENTA', 'Coche fiable, pequeños detalles cosméticos.', 2009, 3),
('Seat', 'Ibiza', '1.4L', 'Rojo', 'MANUAL', 'GASOLINA', 3, 'Barcelona', 85, 152000, 4100.00, 5, 1390, 'B', 'EN_VENTA', 'Revisión completa hecha. Neumáticos nuevos.', 2011, 3),
('Fiat', 'Punto', '1.3L', 'Blanco', 'MANUAL', 'DIESEL', 5, 'Valencia', 75, 162000, 3400.00, 5, 1248, 'B', 'EN_VENTA', 'Económico y práctico. Pequeños retoques.', 2007, 3),
('Toyota', 'Corolla', '1.4L', 'Plata', 'MANUAL', 'GASOLINA', 5, 'Madrid', 97, 142000, 5800.00, 5, 1329, 'C', 'EN_VENTA', 'Muy fiable, motor sin problemas. Ideal para todo.', 2014, 3),
('Citroën', 'C3', '1.4L', 'Verde', 'MANUAL', 'GASOLINA', 5, 'Barcelona', 92, 175000, 4200.00, 5, 1360, 'B', 'EN_VENTA', 'Espacioso, buen maletero. Cómodo y práctico.', 2009, 3),
('Volkswagen', 'Golf', '1.4L', 'Gris', 'MANUAL', 'GASOLINA', 5, 'Valencia', 80, 155000, 5900.00, 5, 1390, 'B', 'EN_VENTA', 'Coche alemán de calidad. Bien conservado.', 2010, 3),
('Opel', 'Corsa', '1.2L', 'Rojo Oscuro', 'MANUAL', 'GASOLINA', 5, 'Madrid', 86, 168000, 4100.00, 5, 1199, 'B', 'EN_VENTA', 'Muy práctico, excelente relación precio-valor.', 2011, 3),

-- Coches del usuario María López Martínez (12 coches de marcas populares asequibles)
('Hyundai', 'i10', '1.1L', 'Naranja', 'MANUAL', 'GASOLINA', 5, 'Sevilla', 68, 138000, 3800.00, 5, 1086, 'C', 'EN_VENTA', 'Coche joven, buen estado. Pocos km por año.', 2013, 4),
('Kia', 'Picanto', '1.0L', 'Blanco', 'MANUAL', 'GASOLINA', 5, 'Bilbao', 65, 155000, 3100.00, 5, 995, 'B', 'EN_VENTA', 'Económico, mantenimiento básico al día.', 2012, 4),
('Dacia', 'Logan', '1.4L', 'Gris', 'MANUAL', 'GASOLINA', 4, 'Murcia', 75, 175000, 3200.00, 5, 1390, 'C', 'EN_VENTA', 'Muy práctico, valor de reventa. Perfecto como primer coche.', 2010, 4),
('Suzuki', 'Swift', '1.3L', 'Rojo', 'MANUAL', 'GASOLINA', 5, 'Pamplona', 92, 142000, 4300.00, 5, 1298, 'B', 'EN_VENTA', 'Ágil y económico, ideal para ciudad.', 2013, 4),
('Nissan', 'Micra', '1.2L', 'Azul', 'MANUAL', 'GASOLINA', 5, 'Sevilla', 80, 165000, 4100.00, 5, 1197, 'B', 'EN_VENTA', 'Pequeño, fácil de aparcar. Buen estado.', 2010, 4),
('Daewoo', 'Matiz', '0.8L', 'Blanco', 'MANUAL', 'GASOLINA', 5, 'Bilbao', 52, 185000, 2100.00, 4, 796, 'C', 'EN_VENTA', 'Muy económico, perfecto para debutantes.', 2005, 4),
('Chevrolet', 'Kalos', '1.4L', 'Gris Plata', 'MANUAL', 'GASOLINA', 5, 'Murcia', 94, 152000, 3600.00, 5, 1398, 'B', 'EN_VENTA', 'Fiable de marca americana. Buen comportamiento.', 2012, 4),
('Peugeot', '206', '1.1L', 'Plata', 'MANUAL', 'GASOLINA', 5, 'Pamplona', 60, 175000, 2800.00, 5, 1124, 'C', 'EN_VENTA', 'Básico pero fiable. Muy económico de mantener.', 2004, 4),
('Ford', 'Fiesta', '1.25L', 'Negro', 'MANUAL', 'GASOLINA', 5, 'Sevilla', 82, 148000, 4700.00, 5, 1242, 'B', 'EN_VENTA', 'Coche popular fiable. Piezas baratas.', 2011, 4),
('Renault', 'Megane', '1.5L', 'Azul Marino', 'MANUAL', 'DIESEL', 5, 'Bilbao', 100, 155000, 5300.00, 5, 1461, 'B', 'EN_VENTA', 'Buen motor diesel, consumo bajo.', 2009, 4),
('Fiat', 'Panda', '1.1L', 'Rojo', 'MANUAL', 'GASOLINA', 5, 'Murcia', 54, 168000, 2600.00, 5, 1108, 'C', 'EN_VENTA', 'Pequeño japonés muy cómodo.', 2008, 4),
('Citroën', 'Saxo', '1.1L', 'Blanco', 'MANUAL', 'GASOLINA', 3, 'Pamplona', 60, 195000, 2300.00, 4, 1124, 'C', 'EN_VENTA', 'Clásico pequeño, muy versátil.', 2002, 4),

-- Coches de la empresa AutoMotor Premium S.L. (40+ coches de segunda mano típicos españoles)
('Opel', 'Vectra', '1.6L', 'Plata', 'MANUAL', 'GASOLINA', 5, 'Madrid', 100, 182000, 3800.00, 5, 1598, 'B', 'EN_VENTA', 'Sedán inglés práctico, motor robusto.', 2006, 5),
('Mercedes-Benz', 'Clase A', '1.6L', 'Gris', 'AUTOMATICO', 'GASOLINA', 5, 'Barcelona', 102, 145000, 8200.00, 5, 1595, 'B', 'EN_VENTA', 'Marca premium con gran valor residual.', 2008, 5),
('BMW', '320i', '2.0L', 'Negro', 'MANUAL', 'GASOLINA', 4, 'Valencia', 150, 125000, 12500.00, 5, 1995, 'C', 'EN_VENTA', 'Sedán deportivo alemán, bien mantenido.', 2010, 5),
('Audi', 'A3', '1.6L', 'Rojo', 'MANUAL', 'GASOLINA', 5, 'Madrid', 102, 155000, 9800.00, 5, 1595, 'B', 'EN_VENTA', 'Compacto de lujo alemán. Excelente.', 2009, 5),
('Seat', 'Córdoba', '1.6L', 'Azul', 'MANUAL', 'GASOLINA', 4, 'Barcelona', 101, 138000, 4200.00, 5, 1595, 'B', 'EN_VENTA', 'Sedán español popular y fiable.', 2012, 5),
('Citroën', 'C4', '1.6L', 'Blanco', 'MANUAL', 'GASOLINA', 5, 'Valencia', 110, 165000, 5100.00, 5, 1598, 'B', 'EN_VENTA', 'Monovolumen cómodo francés.', 2008, 5),
('Peugeot', '407', '2.0L', 'Plata', 'AUTOMATICO', 'DIESEL', 4, 'Madrid', 136, 172000, 7500.00, 5, 1997, 'B', 'EN_VENTA', 'Berlina grande francesa con diesel económico.', 2007, 5),
('Renault', 'Laguna', '1.8L', 'Gris Oscuro', 'AUTOMATICO', 'GASOLINA', 4, 'Barcelona', 120, 155000, 6800.00, 5, 1798, 'B', 'EN_VENTA', 'Berlina confortable francesa.', 2006, 5),
('Ford', 'Mondeo', '1.8L', 'Negro', 'MANUAL', 'GASOLINA', 5, 'Valencia', 125, 162000, 5900.00, 5, 1796, 'B', 'EN_VENTA', 'Berlina americana robusta.', 2005, 5),
('Volkswagen', 'Passat', '1.8L', 'Azul', 'AUTOMATICO', 'GASOLINA', 5, 'Madrid', 125, 168000, 8100.00, 5, 1781, 'B', 'EN_VENTA', 'Berlina alemana de calidad probada.', 2007, 5),
('Skoda', 'Octavia', '1.6L', 'Gris', 'MANUAL', 'GASOLINA', 5, 'Barcelona', 102, 152000, 5500.00, 5, 1596, 'B', 'EN_VENTA', 'Sedán checo marcado por Volkswagen.', 2011, 5),
('Honda', 'Civic', '1.4L', 'Plata', 'MANUAL', 'GASOLINA', 4, 'Valencia', 100, 142000, 6900.00, 5, 1398, 'B', 'EN_VENTA', 'Sedán fiable japones, amplio interior.', 2011, 5),
('Toyota', 'Avensis', '1.8L', 'Blanco', 'AUTOMATICO', 'GASOLINA', 5, 'Madrid', 129, 145000, 8500.00, 5, 1798, 'C', 'EN_VENTA', 'Berlina fiable japonés muy duradero.', 2010, 5),
('Nissan', 'Almera', '1.5L', 'Azul', 'MANUAL', 'GASOLINA', 4, 'Barcelona', 98, 155000, 4800.00, 5, 1497, 'B', 'EN_VENTA', 'Sedán práctico nissan, buen consumo.', 2009, 5),
('Mitsubishi', 'Lancer', '1.6L', 'Negro', 'MANUAL', 'GASOLINA', 4, 'Valencia', 120, 148000, 6100.00, 5, 1597, 'B', 'EN_VENTA', 'Sedán japonés versátil y fiable.', 2008, 5),
('Hyundai', 'Elantra', '1.6L', 'Rojo', 'AUTOMATICO', 'GASOLINA', 4, 'Madrid', 122, 135000, 6500.00, 5, 1595, 'B', 'EN_VENTA', 'Sedán asiático con buena garantía residual.', 2011, 5),
('Kia', 'Sportage', '2.0L', 'Gris', 'AUTOMATICO', 'DIESEL', 5, 'Barcelona', 150, 138000, 9200.00, 5, 1995, 'B', 'EN_VENTA', 'SUV compacto coreano muy práctico.', 2012, 5),
('Dacia', 'Sandero', '1.5L', 'Verde', 'MANUAL', 'DIESEL', 5, 'Valencia', 90, 158000, 4900.00, 5, 1461, 'B', 'EN_VENTA', 'Práctico rumano muy popular en España.', 2013, 5),
('Suzuki', 'Alto', '1.0L', 'Rojo', 'MANUAL', 'GASOLINA', 5, 'Madrid', 68, 165000, 3300.00, 5, 993, 'C', 'EN_VENTA', 'Pequeño económico japonés.', 2012, 5),
('Mazda', 'MX-3', '1.6L', 'Negro Metalizado', 'MANUAL', 'GASOLINA', 2, 'Barcelona', 115, 95000, 8900.00, 2, 1597, 'C', 'EN_VENTA', 'Descapotable deportivo clásico bien cuidado.', 2001, 5),
('Fiat', 'Bravo', '1.4L', 'Azul', 'MANUAL', 'GASOLINA', 5, 'Valencia', 95, 145000, 3400.00, 5, 1368, 'B', 'EN_VENTA', 'Compacto italiano alegre.', 2007, 5),
('Renault', 'Scenic', '1.6L', 'Naranja', 'MANUAL', 'GASOLINA', 5, 'Madrid', 110, 175000, 5200.00, 7, 1598, 'B', 'EN_VENTA', 'Monovolumen compacto versátil.', 2008, 5),
('Peugeot', '206 CC', '1.6L', 'Rojo', 'MANUAL', 'GASOLINA', 2, 'Barcelona', 110, 105000, 7800.00, 4, 1587, 'C', 'EN_VENTA', 'Descapotable compacto francés deportivo.', 2004, 5),
('Opel', 'Meriva', '1.4L', 'Plata', 'MANUAL', 'GASOLINA', 5, 'Valencia', 100, 155000, 4600.00, 5, 1398, 'B', 'EN_VENTA', 'Monovolumen pequeño muy versátil.', 2010, 5),
('Citroën', 'Berlingo', '1.4L', 'Blanco', 'MANUAL', 'GASOLINA', 5, 'Madrid', 75, 168000, 4100.00, 5, 1361, 'B', 'EN_VENTA', 'Furgoneta familiar muy práctica.', 2006, 5),
('Seat', 'Arosa', '1.4L', 'Azul Claro', 'MANUAL', 'GASOLINA', 3, 'Barcelona', 75, 152000, 2400.00, 5, 1390, 'B', 'EN_VENTA', 'Pequeño español clásico muy versátil.', 2001, 5),
('Daewoo', 'Nubira', '1.6L', 'Negro', 'AUTOMATICO', 'GASOLINA', 4, 'Valencia', 105, 162000, 2800.00, 5, 1598, 'B', 'EN_VENTA', 'Sedán coreano básico pero robusto.', 2003, 5),
('Honda', 'CR-V', '2.0L', 'Gris Metalizado', 'MANUAL', 'GASOLINA', 5, 'Madrid', 150, 138000, 11200.00, 5, 1998, 'C', 'EN_VENTA', 'SUV japonés pequeño muy valuado.', 2008, 5),
('Toyota', 'RAV4', '2.0L', 'Negro', 'MANUAL', 'GASOLINA', 5, 'Barcelona', 152, 145000, 12500.00, 5, 1998, 'C', 'EN_VENTA', 'SUV japonés duradero y fiable.', 2009, 5),
('Renault', 'Espace', '2.2L', 'Plata', 'AUTOMATICO', 'DIESEL', 7, 'Valencia', 150, 175000, 8900.00, 7, 2188, 'B', 'EN_VENTA', 'Monovolumen grande francés de 7 plazas.', 2005, 5),
('Ford', 'Galaxy', '1.9L', 'Gris', 'AUTOMATICO', 'DIESEL', 7, 'Madrid', 130, 182000, 7200.00, 7, 1900, 'B', 'EN_VENTA', 'Monovolumen familia americana de 7 plazas.', 2004, 5),
('Opel', 'Zafira', '1.6L', 'Azul Oscuro', 'MANUAL', 'GASOLINA', 5, 'Barcelona', 101, 155000, 5800.00, 7, 1598, 'B', 'EN_VENTA', 'Monovolumen alemán flexible de 7 plazas.', 2011, 5),
('Hyundai', 'Tucson', '2.0L', 'Blanco', 'AUTOMATICO', 'GASOLINA', 5, 'Valencia', 150, 125000, 10500.00, 5, 1998, 'C', 'EN_VENTA', 'SUV compacto coreano popular.', 2013, 5),
('Kia', 'Ceed', '1.6L', 'Rojo', 'MANUAL', 'DIESEL', 5, 'Madrid', 115, 142000, 7100.00, 5, 1598, 'B', 'EN_VENTA', 'Compacto coreano con motor diesel.', 2012, 5),
('Ssangyong', 'Kyron', '2.0L', 'Negro', 'AUTOMATICO', 'DIESEL', 5, 'Barcelona', 137, 135000, 8200.00, 5, 1997, 'B', 'EN_VENTA', 'SUV coreano robusto y potente.', 2010, 5),
('Suzuki', 'Vitara', '1.6L', 'Verde', 'MANUAL', 'GASOLINA', 5, 'Valencia', 94, 155000, 6700.00, 5, 1590, 'B', 'EN_VENTA', 'SUV compacto japonés versátil.', 2009, 5),
('Subaru', 'Impreza', '2.0L', 'Plata', 'MANUAL', 'GASOLINA', 4, 'Madrid', 155, 128000, 9500.00, 5, 1994, 'C', 'EN_VENTA', 'Tracción integral excelente en lluvia.', 2010, 5),
('Mazda', '3', '1.6L', 'Azul', 'MANUAL', 'GASOLINA', 5, 'Barcelona', 105, 148000, 6800.00, 5, 1598, 'B', 'EN_VENTA', 'Compacto jacobés deportivo y dinámico.', 2009, 5),
('Alfa Romeo', '147', '1.6L', 'Rojo Competizione', 'MANUAL', 'GASOLINA', 3, 'Valencia', 120, 125000, 7200.00, 5, 1597, 'C', 'EN_VENTA', 'Italiano con estilo deportivo italiano.', 2005, 5);

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

