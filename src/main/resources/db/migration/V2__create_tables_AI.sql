CREATE TABLE conversaciones_chat (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    titulo VARCHAR(150) NOT NULL DEFAULT 'Nueva conversación',
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conversaciones_usuario_fecha
    ON conversaciones_chat (usuario_id, fecha_actualizacion DESC);


CREATE TABLE mensajes_chat (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversacion_id UUID NOT NULL
        REFERENCES conversaciones_chat(id) ON DELETE CASCADE,
    tipo VARCHAR(20) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_mensaje_tipo
        CHECK (tipo IN ('USER', 'ASSISTANT'))
);

CREATE INDEX idx_mensajes_conversacion_fecha
    ON mensajes_chat (conversacion_id, fecha_creacion);