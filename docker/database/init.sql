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
                'BILLETERA_DIGITAL',
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
    url_comprobante TEXT NULL,
    fecha_actualizacion TIMESTAMP(6) WITHOUT TIME ZONE,
    cuenta_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    suscripcion_id UUID NULL

    CONSTRAINT movimientos_pkey
        PRIMARY KEY (id),

    CONSTRAINT movimientos_tipo_check
        CHECK (tipo IN ('INGRESO', 'EGRESO', 'AHORRO'))
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
-- TABLA: metas financieras
-- ============================================================
CREATE TABLE IF NOT EXISTS metas_financieras (
    id UUID PRIMARY KEY,
    
    usuario_id UUID NOT NULL,
    
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    
    monto_actual NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    monto_objetivo NUMERIC(19, 2) NOT NULL,
    progreso DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    
    fecha_inicio DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_objetivo DATE NOT NULL,
    
    estado VARCHAR(255) NOT NULL,
    ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_metas_financieras_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT chk_monto_positivo CHECK (monto_objetivo > 0),
    CONSTRAINT chk_monto_actual_no_negativo CHECK (monto_actual >= 0),
    CONSTRAINT chk_fechas_coherentes CHECK (fecha_objetivo >= fecha_inicio),
    CONSTRAINT chk_porcentaje_rango CHECK (porgreso >= 0.0)

    CONSTRAINT chk_estado
        CHECK(
            estado IN (
                'INICIADA', 
                'EN_PROCESO', 
                'PAUSADA', 
                'REANUDADA', 
                'COMPLETADA', 
                'CANCELADA'
        )
    )
);

CREATE INDEX idx_metas_financieras_usuario_id 
ON metas_financieras (usuario_id);

CREATE INDEX idx_metas_financieras_estado_fecha 
ON metas_financieras (estado, fecha_objetivo);

-- ============================================================
-- TABLA: aportes meta
-- ============================================================
CREATE TABLE IF NOT EXISTS aportes_meta (
    id UUID PRIMARY KEY,
    meta_id UUID NOT NULL,
    
    movimiento_id UUID NOT NULL,
    
    monto NUMERIC(19, 2) NOT NULL,
    
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    
    descripcion TEXT,

    CONSTRAINT fk_aportes_meta_meta_id 
        FOREIGN KEY (meta_id) 
        REFERENCES metas_financieras(id) 
        ON DELETE CASCADE,

    CONSTRAINT fk_aportes_meta_movimiento_id 
        FOREIGN KEY (movimiento_id) 
        REFERENCES movimientos(id) 
        ON DELETE RESTRICT,

    CONSTRAINT chk_monto_aporte_positivo CHECK (monto > 0)
);


CREATE INDEX idx_aportes_meta_meta_id 
ON aportes_meta (meta_id);

CREATE INDEX idx_aportes_meta_movimiento_id 
ON aportes_meta (movimiento_id);

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