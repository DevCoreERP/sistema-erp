CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL,
    tipo VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    surnames VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    fecha_ingreso DATE NOT NULL,
    estado BOOLEAN NOT NULL,
    account_non_expired BOOLEAN NOT NULL,
    account_non_locked BOOLEAN NOT NULL,
    credentials_non_expired BOOLEAN NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, role_id),
    CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_usuario_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES public.permissions (id)
);

CREATE TABLE IF NOT EXISTS departamentos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    parent_id BIGINT
);

CREATE TABLE IF NOT EXISTS turnos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    descripcion VARCHAR(255),
    estado BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS asignaciones_turno (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    turno_id BIGINT NOT NULL,
    fecha_i DATE NOT NULL,
    fecha_f DATE,
    estado BOOLEAN NOT NULL,
    CONSTRAINT fk_asignaciones_turno_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_asignaciones_turno_turno FOREIGN KEY (turno_id) REFERENCES turnos (id)
);

CREATE INDEX IF NOT EXISTS idx_asignaciones_turno_usuario ON asignaciones_turno (usuario_id);
CREATE INDEX IF NOT EXISTS idx_asignaciones_turno_turno ON asignaciones_turno (turno_id);
CREATE INDEX IF NOT EXISTS idx_asignaciones_turno_estado ON asignaciones_turno (estado);
CREATE INDEX IF NOT EXISTS idx_asignaciones_turno_fecha_i ON asignaciones_turno (fecha_i);
CREATE INDEX IF NOT EXISTS idx_asignaciones_turno_fecha_f ON asignaciones_turno (fecha_f);
CREATE INDEX IF NOT EXISTS idx_asignaciones_turno_usuario_periodo ON asignaciones_turno (usuario_id, estado, fecha_i, fecha_f);

CREATE TABLE IF NOT EXISTS vacaciones (
    id BIGSERIAL PRIMARY KEY,
    dias BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    usuario_id BIGINT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS solicitudes (
    id BIGSERIAL PRIMARY KEY,
    estado VARCHAR(255) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    saldo_id BIGINT NOT NULL
);
