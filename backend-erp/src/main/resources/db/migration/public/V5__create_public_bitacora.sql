CREATE TABLE IF NOT EXISTS bitacora (
    id BIGSERIAL PRIMARY KEY,
    ip VARCHAR(255) NOT NULL,
    usuario VARCHAR(255),
    tenant VARCHAR(255),
    endpoint VARCHAR(255) NOT NULL,
    http_status INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_bitacora_created_at ON bitacora(created_at);
CREATE INDEX IF NOT EXISTS idx_bitacora_usuario ON bitacora(usuario);
CREATE INDEX IF NOT EXISTS idx_bitacora_tenant ON bitacora(tenant);
CREATE INDEX IF NOT EXISTS idx_bitacora_http_status ON bitacora(http_status);