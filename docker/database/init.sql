CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================================
-- CREACIÓN DE ENUMS
-- ============================================================
CREATE TYPE rol_usuario AS ENUM ('USER', 'ADMIN');
CREATE TYPE estado_usuario AS ENUM ('PENDIENTE', 'ACTIVO', 'SUSPENDIDO', 'INACTIVO', 'ELIMINADO');
CREATE TYPE tipo_categoria AS ENUM ('INGRESOS', 'GASTOS', 'AHORRO');
CREATE TYPE tipo_cuenta AS ENUM ('EFECTIVO', 'BANCO', 'BILLETERA_DIGITAL', 'OTRO');
CREATE TYPE tipo_movimiento AS ENUM ('INGRESO', 'EGRESO', 'AHORRO');
CREATE TYPE frecuencia_suscripcion AS ENUM ('MENSUAL', 'ANUAL', 'SEMANAL');
CREATE TYPE estado_meta AS ENUM ('INICIADA', 'EN_PROCESO', 'PAUSADA', 'REANUDADA', 'COMPLETADA', 'CANCELADA');


-- ============================================================
-- 1. TABLA: usuarios (Sin dependencias)
-- ============================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID NOT NULL,
    fecha_creacion DATE NOT NULL,
    email VARCHAR(255) NOT NULL,
    enable2fa BOOLEAN NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    "contraseña" VARCHAR(255) NOT NULL,
    rol rol_usuario NOT NULL,
    secret2fa VARCHAR(255),
    estado estado_usuario NOT NULL,
    fecha_actualizacion DATE,

    CONSTRAINT usuarios_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_usuarios_email
    ON usuarios (email);


-- ============================================================
-- 2. TABLA: categorias (Depende de usuarios y de sí misma)
-- ============================================================
CREATE TABLE IF NOT EXISTS categorias (
    id UUID NOT NULL,
    activo BOOLEAN NOT NULL,
    fecha_creacion DATE NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    categoria_padre_id UUID,
    tipo tipo_categoria NOT NULL,
    fecha_actualizacion DATE,
    usuario_id UUID,

    CONSTRAINT categorias_pkey PRIMARY KEY (id),

    CONSTRAINT fk_categorias_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_categorias_padre
        FOREIGN KEY (categoria_padre_id)
        REFERENCES categorias(id)
);


-- ============================================================
-- 3. TABLA: cuentas (Depende de usuarios)
-- ============================================================
CREATE TABLE IF NOT EXISTS cuentas (
    id UUID NOT NULL,
    activo BOOLEAN NOT NULL,
    fecha_creacion DATE NOT NULL,
    saldo_actual NUMERIC(14,2) NOT NULL,
    saldo_inicial NUMERIC(14,2) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    tipo tipo_cuenta NOT NULL,
    fecha_actualizacion DATE,
    usuario_id UUID NOT NULL,

    CONSTRAINT cuentas_pkey PRIMARY KEY (id),

    CONSTRAINT fk_cuentas_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
);


-- ============================================================
-- 4. TABLA: movimientos_frecuentes (Depende de usuarios, cuentas y categorias)
-- ============================================================
CREATE TABLE IF NOT EXISTS movimientos_frecuentes (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    cuenta_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    monto NUMERIC(19, 2) NOT NULL,
    frecuencia_cobro frecuencia_suscripcion NOT NULL,
    tipo tipo_movimiento NOT NULL,
    dia_pago INTEGER NOT NULL,
    siguiente_pago DATE NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_movimientos_frecuentes_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_movimientos_frecuentes_cuenta
        FOREIGN KEY (cuenta_id)
        REFERENCES cuentas(id),

    CONSTRAINT fk_movimientos_frecuentes_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id),

    CONSTRAINT chk_movimientos_frecuentes_monto
        CHECK (monto > 0),

    CONSTRAINT chk_movimientos_frecuentes_dia_pago
        CHECK (dia_pago BETWEEN 1 AND 31)
);

CREATE INDEX idx_movimientos_frecuentes_usuario_id ON movimientos_frecuentes(usuario_id);
CREATE INDEX idx_movimientos_frecuentes_cuenta_id ON movimientos_frecuentes(cuenta_id);
CREATE INDEX idx_movimientos_frecuentes_categoria_id ON movimientos_frecuentes(categoria_id);


-- ============================================================
-- 5. TABLA: movimientos (Depende de cuentas, categorias, usuarios y opcionalmente movimientos_frecuentes)
-- ============================================================
CREATE TABLE IF NOT EXISTS movimientos (
    id UUID NOT NULL,
    monto NUMERIC(19,2) NOT NULL,
    fecha_registro TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    --fecha_emision DATE NOT NULL,
    descripcion VARCHAR(255),
    tipo tipo_movimiento NOT NULL,
    url_comprobante TEXT NULL,
    fecha_actualizacion TIMESTAMP(6) WITHOUT TIME ZONE,
    cuenta_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    frecuentes_id UUID NULL,

    CONSTRAINT movimientos_pkey PRIMARY KEY (id),

    CONSTRAINT fk_movimientos_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id),

    CONSTRAINT fk_movimientos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_movimientos_cuenta
        FOREIGN KEY (cuenta_id)
        REFERENCES cuentas(id),

    CONSTRAINT fk_movimientos_suscripcion
        FOREIGN KEY (frecuentes_id)
        REFERENCES movimientos_frecuentes(id)
);

CREATE INDEX idx_movimientos_frecuentes_id ON movimientos(frecuentes_id);


-- ============================================================
-- 6. TABLA: transferencias (Depende de usuarios y cuentas)
-- ============================================================
CREATE TABLE IF NOT EXISTS transferencias (
    id UUID NOT NULL,
    monto NUMERIC(14,2) NOT NULL,
    fecha_creacion DATE NOT NULL,
    descripcion VARCHAR(255),
    cuenta_destino_id UUID NOT NULL,
    cuenta_origen_id UUID NOT NULL,
    usuario_id UUID NOT NULL,

    CONSTRAINT transferencias_pkey PRIMARY KEY (id),

    CONSTRAINT fk_transferencias_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_transferencias_cuenta_origen
        FOREIGN KEY (cuenta_origen_id)
        REFERENCES cuentas(id),

    CONSTRAINT fk_transferencias_cuenta_destino
        FOREIGN KEY (cuenta_destino_id)
        REFERENCES cuentas(id)
);


-- ============================================================
-- 7. TABLA: metas_financieras (Depende de usuarios)
-- ============================================================
CREATE TABLE IF NOT EXISTS metas_financieras (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    monto_actual NUMERIC(19,2) NOT NULL DEFAULT 0.00,
    monto_objetivo NUMERIC(19,2) NOT NULL,
    progreso DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    fecha_inicio DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_objetivo DATE NOT NULL,
    estado estado_meta NOT NULL,
    ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_metas_financieras_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT chk_monto_positivo
        CHECK (monto_objetivo > 0),

    CONSTRAINT chk_monto_actual_no_negativo
        CHECK (monto_actual >= 0),

    CONSTRAINT chk_fechas_coherentes
        CHECK (fecha_objetivo >= fecha_inicio),

    CONSTRAINT chk_porcentaje_rango
        CHECK (progreso >= 0.0 AND progreso <= 100.0)
);

CREATE INDEX IF NOT EXISTS idx_metas_financieras_usuario_id
    ON metas_financieras (usuario_id);

CREATE INDEX IF NOT EXISTS idx_metas_financieras_estado_fecha
    ON metas_financieras (estado, fecha_objetivo);


-- ============================================================
-- 8. TABLA: aportes_meta (Depende de metas_financieras y movimientos)
-- ============================================================
CREATE TABLE IF NOT EXISTS aportes_meta (
    id UUID PRIMARY KEY,
    meta_id UUID NOT NULL,
    movimiento_id UUID NOT NULL,
    monto NUMERIC(19,2) NOT NULL,
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

    CONSTRAINT chk_monto_aporte_positivo
        CHECK (monto > 0)
);

CREATE INDEX IF NOT EXISTS idx_aportes_meta_meta_id
    ON aportes_meta (meta_id);

CREATE INDEX IF NOT EXISTS idx_aportes_meta_movimiento_id
    ON aportes_meta (movimiento_id);



-- Presupuestos y sus limites por categoria. Aplicar una vez a una base existente.
CREATE TABLE IF NOT EXISTS presupuestos (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    nombre VARCHAR(255) NOT NULL,
    periodo_inicial DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    monto_limite_total NUMERIC(19,2) NOT NULL CHECK (monto_limite_total > 0),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    fecha_actualizado TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT chk_presupuestos_periodo CHECK (periodo_inicial <= periodo_fin)
);

CREATE TABLE IF NOT EXISTS presupuesto_categorias (
    id UUID PRIMARY KEY,
    presupuesto_id UUID NOT NULL REFERENCES presupuestos(id) ON DELETE CASCADE,
    categoria_id UUID NOT NULL REFERENCES categorias(id),
    monto_limite NUMERIC(19,2) NOT NULL CHECK (monto_limite > 0),
    CONSTRAINT uq_presupuesto_categoria UNIQUE (presupuesto_id, categoria_id)
);

CREATE INDEX IF NOT EXISTS idx_presupuestos_usuario_periodo
    ON presupuestos(usuario_id, periodo_inicial, periodo_fin);
CREATE INDEX IF NOT EXISTS idx_presupuesto_categorias_categoria
    ON presupuesto_categorias(categoria_id);