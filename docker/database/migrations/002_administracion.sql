BEGIN;

CREATE TABLE IF NOT EXISTS auditoria_administrativa (
    id UUID PRIMARY KEY,
    actor_id UUID NOT NULL REFERENCES usuarios(id),
    accion VARCHAR(40) NOT NULL CHECK (accion IN (
        'CATEGORY_CREATED', 'CATEGORY_RENAMED', 'CATEGORY_STATUS_CHANGED', 'USER_STATUS_CHANGED'
    )),
    recurso_id UUID NOT NULL,
    detalle VARCHAR(1024) NOT NULL,
    fecha TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_auditoria_fecha
    ON auditoria_administrativa (fecha DESC, id ASC);
CREATE INDEX IF NOT EXISTS idx_auditoria_actor_fecha
    ON auditoria_administrativa (actor_id, fecha DESC);

COMMIT;
