CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================================
-- TABLA: usuarios
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID NOT NULL,
    fecha_creacion DATE NOT NULL,
    email VARCHAR(255) NOT NULL,
    enable2fa BOOLEAN NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    "contraseña" VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL,
    secret2fa VARCHAR(255),
    estado VARCHAR(255) NOT NULL,
    fecha_actualizacion DATE,

    CONSTRAINT usuarios_pkey
        PRIMARY KEY (id),

    CONSTRAINT usuarios_rol_check
        CHECK (rol IN ('USER', 'ADMIN')),

    CONSTRAINT usuarios_estado_check
        CHECK (
            estado IN (
                'PENDIENTE',
                'ACTIVO',
                'SUSPENDIDO',
                'INACTIVO',
                'ELIMINADO'
            )
        )
);

-- Índice único para búsquedas por email y evitar duplicados.
CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_email
    ON usuarios (email);

-- ============================================================
-- TABLA: categorias
-- ============================================================

CREATE TABLE IF NOT EXISTS categorias (
    id UUID NOT NULL,
    activo BOOLEAN NOT NULL,
    fecha_creacion DATE NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    categoria_padre_id UUID,
    tipo VARCHAR(255) NOT NULL,
    fecha_actualizacion DATE,
    usuario_id UUID,

    CONSTRAINT categorias_pkey
        PRIMARY KEY (id),

    CONSTRAINT categorias_tipo_check
        CHECK (tipo IN ('INGRESOS', 'GASTOS'))
);

-- ============================================================
-- TABLA: cuentas
-- ============================================================

CREATE TABLE IF NOT EXISTS cuentas (
    id UUID NOT NULL,
    activo BOOLEAN NOT NULL,
    fecha_creacion DATE NOT NULL,
    saldo_actual NUMERIC(14,2) NOT NULL,
    saldo_inicial NUMERIC(14,2) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    fecha_actualizacion DATE,
    usuario_id UUID NOT NULL,

    CONSTRAINT cuentas_pkey
        PRIMARY KEY (id),

    CONSTRAINT cuentas_tipo_check
        CHECK (
            tipo IN (
                'EFECTIVO',
                'BANCO',
                'TARJETA DE CRÉDITO',
                'AHORROS',
                'OTRO'
            )
        )
);


-- ============================================================
-- TABLA: movimientos
-- ============================================================

CREATE TABLE IF NOT EXISTS movimientos (
    id UUID NOT NULL,
    monto NUMERIC(19,2) NOT NULL,
    fecha_registro TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    fecha_emision DATE NOT NULL,
    descripcion VARCHAR(255),
    tipo VARCHAR(255) NOT NULL,
    fecha_actualizacion TIMESTAMP(6) WITHOUT TIME ZONE,
    cuenta_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    suscripcion_id UUID NULL

    CONSTRAINT movimientos_pkey
        PRIMARY KEY (id),

    CONSTRAINT movimientos_tipo_check
        CHECK (tipo IN ('INGRESO', 'EGRESO'))
);

CREATE INDEX idx_movimientos_suscripcion_id
    ON movimientos(suscripcion_id);


-- ============================================================
-- TABLA: transferencias
-- ============================================================

CREATE TABLE IF NOT EXISTS transferencias (
    id UUID NOT NULL,
    monto NUMERIC(14,2) NOT NULL,
    fecha_creacion DATE NOT NULL,
    descripcion VARCHAR(255),
    cuenta_destino_id UUID NOT NULL,
    cuenta_origen_id UUID NOT NULL,
    usuario_id UUID NOT NULL,

    CONSTRAINT transferencias_pkey
        PRIMARY KEY (id)
);


-- ============================================================
-- TABLA: suscripciones
-- ============================================================

CREATE TABLE IF NOT EXISTS suscripciones (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    cuenta_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    monto NUMERIC(19, 2) NOT NULL,
    frecuencia_cobro VARCHAR(255) NOT NULL,
    dia_pago INTEGER NOT NULL,
    siguiente_pago DATE NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_suscripciones_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_suscripciones_cuenta
        FOREIGN KEY (cuenta_id)
        REFERENCES cuentas(id),

    CONSTRAINT fk_suscripciones_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id),

    CONSTRAINT chk_suscripciones_monto
        CHECK (monto > 0),

    CONSTRAINT chk_suscripciones_dia_pago
        CHECK (dia_pago BETWEEN 1 AND 31)
    
    CONSTRAINT suscripciones_frecuencia_cobro_check
        CHECK(
            frecuencia_cobro IN (
                'MENSUAL',
                'ANUAL',
                'SEMANAL'
            ) 
        )
);

CREATE INDEX idx_suscripciones_usuario_id
    ON suscripciones(usuario_id);

CREATE INDEX idx_suscripciones_cuenta_id
    ON suscripciones(cuenta_id);

CREATE INDEX idx_suscripciones_categoria_id
    ON suscripciones(categoria_id);


-- ============================================================
-- FOREIGN KEYS
-- ============================================================

ALTER TABLE categorias
    ADD CONSTRAINT fk_categorias_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id);

ALTER TABLE categorias
    ADD CONSTRAINT fk_categorias_padre
    FOREIGN KEY (categoria_padre_id)
    REFERENCES categorias(id);


ALTER TABLE cuentas
    ADD CONSTRAINT fk_cuentas_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id);

ALTER TABLE movimientos
    ADD CONSTRAINT fk_movimientos_categoria
    FOREIGN KEY (categoria_id)
    REFERENCES categorias(id);

ALTER TABLE movimientos
    ADD CONSTRAINT fk_movimientos_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id);

ALTER TABLE movimientos
    ADD CONSTRAINT fk_movimientos_cuenta
    FOREIGN KEY (cuenta_id)
    REFERENCES cuentas(id);

ALTER TABLE movimientos
    ADD CONSTRAINT fk_movimientos_suscripcion
    FOREIGN KEY (suscripcion_id)
    REFERENCES suscripciones(id);

ALTER TABLE transferencias
    ADD CONSTRAINT fk_transferencias_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES usuarios(id);

ALTER TABLE transferencias
    ADD CONSTRAINT fk_transferencias_cuenta_origen
    FOREIGN KEY (cuenta_origen_id)
    REFERENCES cuentas(id);

ALTER TABLE transferencias
    ADD CONSTRAINT fk_transferencias_cuenta_destino
    FOREIGN KEY (cuenta_destino_id)
    REFERENCES cuentas(id);