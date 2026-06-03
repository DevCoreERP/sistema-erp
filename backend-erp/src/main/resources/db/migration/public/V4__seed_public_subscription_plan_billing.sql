INSERT INTO public.modulo (nombre, descripcion, estado) VALUES
    ('Core HR', 'Gestion base de usuarios, roles, departamentos y estructura organizacional', TRUE),
    ('Nomina', 'Procesos base de nomina', TRUE),
    ('Ausencias', 'Saldos y solicitudes de vacaciones o ausencias', TRUE),
    ('Beneficios', 'Gestion de beneficios para empleados', TRUE),
    ('Reportes basicos', 'Reportes operativos basicos', TRUE),
    ('Autoservicio del empleado', 'Consultas y acciones de autoservicio del empleado', TRUE),
    ('Reclutamiento', 'Gestion de candidatos y reclutamiento', TRUE),
    ('Onboarding', 'Ingreso y acompanamiento de nuevos empleados', TRUE),
    ('Gestion del desempeno', 'Evaluaciones y objetivos de desempeno', TRUE),
    ('Capacitacion', 'Gestion de capacitaciones', TRUE),
    ('Desarrollo del talento', 'Planes de desarrollo y crecimiento', TRUE),
    ('Suite completa', 'Acceso integral a la suite HCM', TRUE),
    ('Analitica con IA', 'Analitica avanzada asistida por IA', TRUE),
    ('Sucesion', 'Planes de sucesion organizacional', TRUE),
    ('Compensacion variable', 'Gestion de compensacion variable', TRUE),
    ('Integraciones avanzadas', 'Integraciones avanzadas con sistemas externos', TRUE),
    ('Soporte prioritario', 'Atencion prioritaria para soporte', TRUE),
    ('API ilimitada', 'Acceso ilimitado a API', TRUE),
    ('Facturacion SaaS', 'Gestion de planes, suscripciones y pagos', TRUE)
ON CONFLICT (nombre) DO UPDATE
SET descripcion = EXCLUDED.descripcion,
    estado = TRUE;

INSERT INTO public.plan (nombre, descripcion, precio_usd, limite_usuarios, estado) VALUES
    ('Gratis / Prueba', 'Prueba gratuita de 7 dias para validar funcionalidades basicas', 0.00, 50, TRUE),
    ('Esencial', 'Pymes con necesidades basicas de RRHH', 9.00, 50, TRUE),
    ('Profesional', 'Empresas en crecimiento con foco en talento', 18.00, 50, TRUE),
    ('Premium', 'Grandes empresas con procesos estrategicos de HCM', 25.00, 50, TRUE)
ON CONFLICT (nombre) DO UPDATE
SET descripcion = EXCLUDED.descripcion,
    precio_usd = EXCLUDED.precio_usd,
    limite_usuarios = EXCLUDED.limite_usuarios,
    estado = TRUE,
    fecha_actualizacion = CURRENT_TIMESTAMP;

INSERT INTO public.beneficio (nombre, descripcion, estado) VALUES
    ('Hosting incluido', 'Infraestructura incluida en el servicio', TRUE),
    ('Actualizaciones automaticas', 'Actualizaciones del sistema incluidas', TRUE),
    ('Soporte basico 24/7', 'Soporte basico disponible 24/7', TRUE),
    ('Seguridad avanzada', 'Controles de seguridad avanzados', TRUE),
    ('Reportes basicos', 'Reportes operativos basicos incluidos', TRUE),
    ('Sin costos ocultos de mantenimiento', 'Mantenimiento incluido sin costos ocultos', TRUE),
    ('Reportes ilimitados', 'Acceso a reportes ilimitados', TRUE),
    ('Soporte prioritario', 'Atencion prioritaria de soporte', TRUE),
    ('Integraciones avanzadas', 'Integraciones avanzadas incluidas', TRUE),
    ('API ilimitada', 'Uso ilimitado de API', TRUE),
    ('Analitica estrategica con IA', 'Analitica estrategica asistida por IA', TRUE)
ON CONFLICT (nombre) DO UPDATE
SET descripcion = EXCLUDED.descripcion,
    estado = TRUE;

INSERT INTO public.metodo_pago (nombre, estado) VALUES
    ('Tarjeta', TRUE),
    ('Transferencia', TRUE),
    ('PayPal', TRUE)
ON CONFLICT (nombre) DO UPDATE
SET estado = TRUE;

INSERT INTO public.permissions (code, description, estado, modulo_id) VALUES
    ('SUSCRIPCION_VER', 'Consultar suscripcion del tenant', TRUE, (SELECT id FROM public.modulo WHERE nombre = 'Facturacion SaaS')),
    ('SUSCRIPCION_GESTIONAR', 'Gestionar suscripcion del tenant', TRUE, (SELECT id FROM public.modulo WHERE nombre = 'Facturacion SaaS')),
    ('METODO_PAGO_GESTIONAR', 'Gestionar metodos de pago del tenant', TRUE, (SELECT id FROM public.modulo WHERE nombre = 'Facturacion SaaS')),
    ('PAGO_LISTAR', 'Listar historial de pagos del tenant', TRUE, (SELECT id FROM public.modulo WHERE nombre = 'Facturacion SaaS'))
ON CONFLICT (code) DO UPDATE
SET description = EXCLUDED.description,
    estado = TRUE,
    modulo_id = EXCLUDED.modulo_id;

UPDATE public.permissions
SET modulo_id = (SELECT id FROM public.modulo WHERE nombre = 'Core HR')
WHERE code LIKE 'USUARIO_%'
   OR code LIKE 'ROL_%'
   OR code = 'PERMISO_LISTAR'
   OR code LIKE 'DEPARTAMENTO_%'
   OR code LIKE 'TURNO_%'
   OR code LIKE 'ASIGNACION_TURNO_%';

UPDATE public.permissions
SET modulo_id = (SELECT id FROM public.modulo WHERE nombre = 'Autoservicio del empleado')
WHERE code LIKE 'AGENDA_TURNO_%';

UPDATE public.permissions
SET modulo_id = (SELECT id FROM public.modulo WHERE nombre = 'Ausencias')
WHERE code LIKE 'SALDO_%'
   OR code LIKE 'SOLICITUD_%';

UPDATE public.permissions
SET modulo_id = (SELECT id FROM public.modulo WHERE nombre = 'Facturacion SaaS')
WHERE code IN ('SUSCRIPCION_VER', 'SUSCRIPCION_GESTIONAR', 'METODO_PAGO_GESTIONAR', 'PAGO_LISTAR');

UPDATE public.permissions
SET modulo_id = (SELECT id FROM public.modulo WHERE nombre = 'Core HR')
WHERE modulo_id IS NULL;

ALTER TABLE public.permissions
    ALTER COLUMN modulo_id SET NOT NULL;

INSERT INTO public.plan_modulo (plan_id, modulo_id)
SELECT plan.id, modulo.id
FROM public.plan plan
JOIN public.modulo modulo ON modulo.nombre IN (
    'Core HR',
    'Ausencias',
    'Autoservicio del empleado',
    'Facturacion SaaS'
)
WHERE plan.nombre = 'Gratis / Prueba'
ON CONFLICT (plan_id, modulo_id) DO NOTHING;

INSERT INTO public.plan_modulo (plan_id, modulo_id)
SELECT plan.id, modulo.id
FROM public.plan plan
JOIN public.modulo modulo ON modulo.nombre IN (
    'Core HR',
    'Nomina',
    'Ausencias',
    'Beneficios',
    'Reportes basicos',
    'Autoservicio del empleado',
    'Facturacion SaaS'
)
WHERE plan.nombre = 'Esencial'
ON CONFLICT (plan_id, modulo_id) DO NOTHING;

INSERT INTO public.plan_modulo (plan_id, modulo_id)
SELECT plan.id, modulo.id
FROM public.plan plan
JOIN public.modulo modulo ON modulo.nombre IN (
    'Core HR',
    'Nomina',
    'Ausencias',
    'Beneficios',
    'Reportes basicos',
    'Autoservicio del empleado',
    'Reclutamiento',
    'Onboarding',
    'Gestion del desempeno',
    'Capacitacion',
    'Desarrollo del talento',
    'Facturacion SaaS'
)
WHERE plan.nombre = 'Profesional'
ON CONFLICT (plan_id, modulo_id) DO NOTHING;

INSERT INTO public.plan_modulo (plan_id, modulo_id)
SELECT plan.id, modulo.id
FROM public.plan plan
JOIN public.modulo modulo ON TRUE
WHERE plan.nombre = 'Premium'
ON CONFLICT (plan_id, modulo_id) DO NOTHING;

INSERT INTO public.plan_beneficio (plan_id, beneficio_id)
SELECT plan.id, beneficio.id
FROM public.plan plan
JOIN public.beneficio beneficio ON beneficio.nombre IN (
    'Hosting incluido',
    'Actualizaciones automaticas',
    'Soporte basico 24/7',
    'Seguridad avanzada',
    'Reportes basicos'
)
WHERE plan.nombre = 'Gratis / Prueba'
ON CONFLICT (plan_id, beneficio_id) DO NOTHING;

INSERT INTO public.plan_beneficio (plan_id, beneficio_id)
SELECT plan.id, beneficio.id
FROM public.plan plan
JOIN public.beneficio beneficio ON beneficio.nombre IN (
    'Hosting incluido',
    'Actualizaciones automaticas',
    'Soporte basico 24/7',
    'Seguridad avanzada',
    'Reportes basicos',
    'Sin costos ocultos de mantenimiento'
)
WHERE plan.nombre = 'Esencial'
ON CONFLICT (plan_id, beneficio_id) DO NOTHING;

INSERT INTO public.plan_beneficio (plan_id, beneficio_id)
SELECT plan.id, beneficio.id
FROM public.plan plan
JOIN public.beneficio beneficio ON beneficio.nombre IN (
    'Hosting incluido',
    'Actualizaciones automaticas',
    'Soporte basico 24/7',
    'Seguridad avanzada',
    'Reportes ilimitados'
)
WHERE plan.nombre = 'Profesional'
ON CONFLICT (plan_id, beneficio_id) DO NOTHING;

INSERT INTO public.plan_beneficio (plan_id, beneficio_id)
SELECT plan.id, beneficio.id
FROM public.plan plan
JOIN public.beneficio beneficio ON beneficio.nombre IN (
    'Hosting incluido',
    'Actualizaciones automaticas',
    'Seguridad avanzada',
    'Reportes ilimitados',
    'Soporte prioritario',
    'Integraciones avanzadas',
    'API ilimitada',
    'Analitica estrategica con IA'
)
WHERE plan.nombre = 'Premium'
ON CONFLICT (plan_id, beneficio_id) DO NOTHING;

INSERT INTO public.suscripcion (
    tenant_id,
    plan_id,
    fecha_inicio,
    fecha_fin,
    estado,
    tipo,
    fecha_proximo_vencimiento,
    plan_nombre_snapshot,
    precio_usd_snapshot,
    limite_usuarios_snapshot
)
SELECT tenants.id,
       plan.id,
       tenants.fecha_inicio_prueba,
       tenants.fecha_fin_prueba,
       CASE WHEN tenants.fecha_fin_prueba >= CURRENT_DATE THEN 'PRUEBA' ELSE 'VENCIDA' END,
       'PRUEBA',
       tenants.fecha_fin_prueba,
       plan.nombre,
       plan.precio_usd,
       plan.limite_usuarios
FROM public.tenants tenants
JOIN public.plan plan ON plan.nombre = 'Gratis / Prueba'
WHERE NOT EXISTS (
    SELECT 1
    FROM public.suscripcion existing
    WHERE existing.tenant_id = tenants.id
)
ON CONFLICT DO NOTHING;
