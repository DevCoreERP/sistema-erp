ALTER TABLE public.tenants
    ADD COLUMN IF NOT EXISTS fecha_inicio_prueba DATE,
    ADD COLUMN IF NOT EXISTS fecha_fin_prueba DATE;

UPDATE public.tenants
SET fecha_inicio_prueba = COALESCE(fecha_inicio_prueba, created_at::date, CURRENT_DATE),
    fecha_fin_prueba = COALESCE(fecha_fin_prueba, COALESCE(created_at::date, CURRENT_DATE) + 7)
WHERE fecha_inicio_prueba IS NULL
   OR fecha_fin_prueba IS NULL;

CREATE TABLE IF NOT EXISTS public.modulo (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS public.plan (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    precio_usd NUMERIC(10, 2) NOT NULL,
    limite_usuarios INTEGER NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_plan_precio_usd CHECK (precio_usd >= 0),
    CONSTRAINT chk_plan_limite_usuarios CHECK (limite_usuarios > 0)
);

CREATE TABLE IF NOT EXISTS public.beneficio (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS public.plan_modulo (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    modulo_id BIGINT NOT NULL,
    CONSTRAINT fk_plan_modulo_plan FOREIGN KEY (plan_id) REFERENCES public.plan (id),
    CONSTRAINT fk_plan_modulo_modulo FOREIGN KEY (modulo_id) REFERENCES public.modulo (id),
    CONSTRAINT uq_plan_modulo UNIQUE (plan_id, modulo_id)
);

CREATE TABLE IF NOT EXISTS public.plan_beneficio (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    beneficio_id BIGINT NOT NULL,
    CONSTRAINT fk_plan_beneficio_plan FOREIGN KEY (plan_id) REFERENCES public.plan (id),
    CONSTRAINT fk_plan_beneficio_beneficio FOREIGN KEY (beneficio_id) REFERENCES public.beneficio (id),
    CONSTRAINT uq_plan_beneficio UNIQUE (plan_id, beneficio_id)
);

ALTER TABLE public.permissions
    ADD COLUMN IF NOT EXISTS modulo_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_permissions_modulo'
    ) THEN
        ALTER TABLE public.permissions
            ADD CONSTRAINT fk_permissions_modulo
            FOREIGN KEY (modulo_id) REFERENCES public.modulo (id);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS public.metodo_pago (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS public.tenant_metodo_pago (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    metodo_pago_id BIGINT NOT NULL,
    titular VARCHAR(150) NOT NULL,
    ultimos_digitos VARCHAR(4),
    marca VARCHAR(80),
    referencia VARCHAR(150),
    es_principal BOOLEAN NOT NULL DEFAULT FALSE,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tenant_metodo_pago_tenant FOREIGN KEY (tenant_id) REFERENCES public.tenants (id),
    CONSTRAINT fk_tenant_metodo_pago_metodo FOREIGN KEY (metodo_pago_id) REFERENCES public.metodo_pago (id),
    CONSTRAINT chk_tenant_metodo_pago_ultimos_digitos CHECK (
        ultimos_digitos IS NULL OR ultimos_digitos ~ '^[0-9]{1,4}$'
    )
);

CREATE TABLE IF NOT EXISTS public.suscripcion (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    tenant_metodo_pago_id BIGINT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    fecha_proximo_vencimiento DATE,
    plan_nombre_snapshot VARCHAR(120) NOT NULL,
    precio_usd_snapshot NUMERIC(10, 2) NOT NULL,
    limite_usuarios_snapshot INTEGER NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_suscripcion_tenant FOREIGN KEY (tenant_id) REFERENCES public.tenants (id),
    CONSTRAINT fk_suscripcion_plan FOREIGN KEY (plan_id) REFERENCES public.plan (id),
    CONSTRAINT fk_suscripcion_tenant_metodo_pago FOREIGN KEY (tenant_metodo_pago_id) REFERENCES public.tenant_metodo_pago (id),
    CONSTRAINT chk_suscripcion_estado CHECK (estado IN ('PRUEBA', 'ACTIVA', 'VENCIDA', 'SUSPENDIDA')),
    CONSTRAINT chk_suscripcion_tipo CHECK (tipo IN ('PRUEBA', 'PAGADA')),
    CONSTRAINT chk_suscripcion_fechas CHECK (fecha_fin >= fecha_inicio),
    CONSTRAINT chk_suscripcion_precio_snapshot CHECK (precio_usd_snapshot >= 0),
    CONSTRAINT chk_suscripcion_limite_snapshot CHECK (limite_usuarios_snapshot > 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_suscripcion_tenant_abierta
    ON public.suscripcion (tenant_id)
    WHERE estado IN ('ACTIVA', 'PRUEBA');

CREATE TABLE IF NOT EXISTS public.pago (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    suscripcion_id BIGINT NOT NULL,
    tenant_metodo_pago_id BIGINT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    monto_pagado_usd NUMERIC(10, 2) NOT NULL,
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    codigo_pago VARCHAR(80) NOT NULL UNIQUE,
    observacion VARCHAR(255),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pago_tenant FOREIGN KEY (tenant_id) REFERENCES public.tenants (id),
    CONSTRAINT fk_pago_suscripcion FOREIGN KEY (suscripcion_id) REFERENCES public.suscripcion (id),
    CONSTRAINT fk_pago_tenant_metodo_pago FOREIGN KEY (tenant_metodo_pago_id) REFERENCES public.tenant_metodo_pago (id),
    CONSTRAINT chk_pago_estado CHECK (estado IN ('PENDIENTE', 'PAGADO', 'FALLIDO')),
    CONSTRAINT chk_pago_monto CHECK (monto_pagado_usd >= 0)
);

CREATE INDEX IF NOT EXISTS idx_tenant_metodo_pago_tenant ON public.tenant_metodo_pago (tenant_id);
CREATE INDEX IF NOT EXISTS idx_suscripcion_tenant ON public.suscripcion (tenant_id);
CREATE INDEX IF NOT EXISTS idx_suscripcion_tenant_estado ON public.suscripcion (tenant_id, estado);
CREATE INDEX IF NOT EXISTS idx_pago_tenant ON public.pago (tenant_id);
