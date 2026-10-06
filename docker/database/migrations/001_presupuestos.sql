BEGIN;

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

COMMIT;
