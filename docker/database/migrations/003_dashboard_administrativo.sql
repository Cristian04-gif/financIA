BEGIN;

CREATE TABLE IF NOT EXISTS metricas_ocr_diarias (
    fecha DATE PRIMARY KEY,
    exitos BIGINT NOT NULL DEFAULT 0 CHECK (exitos >= 0),
    fallos BIGINT NOT NULL DEFAULT 0 CHECK (fallos >= 0)
);

COMMIT;
