# Implementacion Multi-Tenant por Schema

Este documento junta en un solo lugar la implementacion importante de multi-tenancy agregada al backend `backend-erp`.

Estrategia usada:

```text
Shared Database + One Schema per Tenant
```

Estructura esperada:

```text
erp_rrhh
├── public
│   ├── tenants
│   ├── permissions
│   └── flyway_schema_history
│
├── tenant_empresa_demo
│   ├── usuarios
│   ├── roles
│   ├── usuario_roles
│   ├── role_permissions
│   ├── departamentos
│   ├── turnos
│   ├── asignaciones_turno
│   ├── vacaciones
│   ├── solicitudes
│   └── flyway_schema_history
│
└── tenant_empresa_prueba
    └── mismas tablas tenant
```

## Idea Principal

El usuario no manda `schema_name`.

El usuario entra por un subdominio:

```text
empresa-demo.mi-dominio.com
```

El backend extrae:

```text
empresa-demo
```

Busca en:

```sql
SELECT schema_name
FROM public.tenants
WHERE subdomain = 'empresa-demo'
AND status = 'ACTIVE';
```

Y luego trabaja con:

```sql
SET search_path TO tenant_empresa_demo, public;
```

Asi los repositorios actuales consultan automaticamente el schema correcto.

## Rutas Importantes

Base API:

```text
/erp-rrhh/v1
```

Login:

```http
POST /erp-rrhh/v1/auth/login
```

Crear tenant:

```http
POST /erp-rrhh/v1/tenants
```

Header para crear tenant:

```http
X-Provisioning-Key: change-me
```

Header local para login multi-tenant:

```http
X-Tenant-Subdomain: empresa-demo
```

En produccion normalmente no se manda ese header, porque el tenant sale del subdominio.

## Produccion vs Local

Produccion:

```text
https://empresa-demo.erp-rrhh.com/erp-rrhh/v1/auth/login
```

El backend lee `empresa-demo` desde el host.

Local:

```text
http://localhost:8080/erp-rrhh/v1/auth/login
```

Como `localhost` no tiene subdominio real, se manda:

```http
X-Tenant-Subdomain: empresa-demo
```

## Configuracion

Archivo:

```text
src/main/resources/application.properties
```

Configuracion importante:

```properties
spring.jpa.hibernate.ddl-auto=none
spring.jpa.open-in-view=false

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration/public
spring.flyway.schemas=public
spring.flyway.default-schema=public
spring.flyway.create-schemas=true
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0

application.multitenancy.tenant-header=X-Tenant-Subdomain
application.multitenancy.allow-header-tenant=true
application.multitenancy.provisioning-key=${TENANT_PROVISIONING_KEY:change-me}
```

Notas:

- `ddl-auto=none`: Hibernate no crea tablas automaticamente.
- `open-in-view=false`: evita que Hibernate abra conexion antes de resolver tenant.
- Flyway crea `public.tenants` y `public.permissions`.

## Dependencias Agregadas

Archivo:

```text
pom.xml
```

Dependencias importantes:

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

Para pruebas:

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

## Archivos Creados

Paquete principal:

```text
src/main/java/com/devcoreerp/backend_erp/multitenancy
```

Archivos:

```text
Tenant.java
TenantStatus.java
TenantRepository.java
TenantContext.java
TenantConstants.java
TenantValidator.java
TenantResolver.java
TenantSchemaResolver.java
TenantIdentifierResolver.java
MultiTenantConnectionProviderImpl.java
HibernateMultiTenantConfig.java
TenantLoginFilter.java
TenantProvisioningService.java
TenantProvisioningController.java
```

DTOs:

```text
multitenancy/dtos/TenantProvisioningRequestDTO.java
multitenancy/dtos/TenantResponseDTO.java
```

Excepciones:

```text
multitenancy/exceptions/TenantException.java
multitenancy/exceptions/InvalidTenantException.java
multitenancy/exceptions/InvalidSchemaException.java
multitenancy/exceptions/TenantInactiveException.java
multitenancy/exceptions/TenantNotFoundException.java
multitenancy/exceptions/TenantProvisioningException.java
```

Migraciones:

```text
src/main/resources/db/migration/public/V1__create_public_tenants.sql
src/main/resources/db/migration/public/V2__create_public_permissions.sql
src/main/resources/db/migration/tenant/V1__create_tenant_tables.sql
```

## Clases Clave

### TenantContext

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/multitenancy/TenantContext.java
```

Responsabilidad:

- Guardar el tenant actual en un `ThreadLocal`.
- Permitir que Hibernate sepa que schema usar.
- Limpiarse al final de cada request.

Uso conceptual:

```java
TenantContext.setCurrentTenant(tenant);
TenantContext.getCurrentTenant();
TenantContext.clear();
```

### Tenant

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/multitenancy/Tenant.java
```

Entidad global:

```java
@Entity
@Table(name = "tenants", schema = "public")
public class Tenant {
    private Long id;
    private String name;
    private String subdomain;
    private String schemaName;
    private TenantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Permission

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/auth/domain/Permission.java
```

Cambio importante:

```java
@Table(name = "permissions", schema = "public")
```

Eso hace que los permisos sean globales.

### MultiTenantConnectionProviderImpl

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/multitenancy/MultiTenantConnectionProviderImpl.java
```

Responsabilidad:

- Tomar conexiones del datasource.
- Aplicar `SET search_path`.
- Resetear a `public` al liberar conexion.

SQL usado:

```sql
SET search_path TO tenant_empresa_demo, public;
SET search_path TO public;
```

### TenantResolver

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/multitenancy/TenantResolver.java
```

Responsabilidad:

- Resolver tenant desde `Host`.
- En local/dev, permitir `X-Tenant-Subdomain`.

Ejemplos:

```text
empresa-demo.erp-rrhh.com -> empresa-demo
localhost + X-Tenant-Subdomain: empresa-demo -> empresa-demo
```

### TenantSchemaResolver

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/multitenancy/TenantSchemaResolver.java
```

Responsabilidad:

- Buscar tenant en `public.tenants`.
- Validar que este `ACTIVE`.
- Devolver `schema_name`.

### TenantLoginFilter

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/multitenancy/TenantLoginFilter.java
```

Responsabilidad:

- Antes del login, resolver el tenant.
- Setear `TenantContext`.
- Permitir que `AuthServiceImpl` busque el usuario en el schema correcto.

### JwtAuthenticationFilter

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/auth/infrastructure/filters/JwtAuthenticationFilter.java
```

Responsabilidad nueva:

- Validar JWT.
- Leer `tenantId` y `tenantSubdomain`.
- Validar tenant contra `public.tenants`.
- Setear `TenantContext`.
- Cargar usuario, roles y permisos desde el schema correcto.
- Limpiar `TenantContext`.

### TokenServiceImpl

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/auth/application/services/TokenServiceImpl.java
```

JWT ahora incluye:

```json
{
  "sub": "admin@empresa.com",
  "tenantId": 1,
  "tenantSubdomain": "empresa-demo",
  "roles": ["ADMIN"],
  "permissions": ["USUARIO_LISTAR", "ROL_LISTAR"]
}
```

### TenantProvisioningService

Archivo:

```text
src/main/java/com/devcoreerp/backend_erp/multitenancy/TenantProvisioningService.java
```

Responsabilidad:

1. Validar `subdomain`.
2. Generar `schema_name`.
3. Insertar tenant como `INACTIVE`.
4. Crear schema.
5. Ejecutar migraciones tenant.
6. Crear permisos globales si faltan.
7. Crear roles base en el schema tenant.
8. Crear usuario admin inicial.
9. Activar tenant como `ACTIVE`.

## Migraciones

### public.tenants

Archivo:

```text
src/main/resources/db/migration/public/V1__create_public_tenants.sql
```

SQL:

```sql
CREATE TABLE IF NOT EXISTS public.tenants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    subdomain VARCHAR(80) NOT NULL UNIQUE,
    schema_name VARCHAR(120) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_tenants_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'))
);
```

### public.permissions

Archivo:

```text
src/main/resources/db/migration/public/V2__create_public_permissions.sql
```

Crea:

```sql
CREATE TABLE IF NOT EXISTS public.permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);
```

Tambien inserta permisos base como:

```text
USUARIO_CREAR
USUARIO_LISTAR
ROL_CREAR
ROL_LISTAR
PERMISO_LISTAR
TURNO_CREAR
TURNO_ACTUALIZAR
DEPARTAMENTO_LISTAR
SOLICITUD_DELETE
```

### Tablas Tenant

Archivo:

```text
src/main/resources/db/migration/tenant/V1__create_tenant_tables.sql
```

Crea dentro de cada schema tenant:

```text
roles
usuarios
usuario_roles
role_permissions
departamentos
turnos
asignaciones_turno
vacaciones
solicitudes
```

No hardcodea schema:

```sql
CREATE TABLE IF NOT EXISTS usuarios (...);
```

No hace:

```sql
CREATE TABLE tenant_empresa_demo.usuarios (...);
```

## Crear Tenant

Endpoint:

```http
POST http://localhost:8080/erp-rrhh/v1/tenants
X-Provisioning-Key: change-me
Content-Type: application/json
```

Body:

```json
{
  "name": "Empresa Demo",
  "subdomain": "empresa-demo",
  "adminUsername": "admin",
  "adminPassword": "Admin123",
  "adminFirstName": "Admin",
  "adminSurnames": "Demo",
  "adminEmail": "admin@empresa.com",
  "adminPhoneNumber": "70000000"
}
```

Resultado esperado:

```json
{
  "id": 1,
  "name": "Empresa Demo",
  "subdomain": "empresa-demo",
  "schemaName": "tenant_empresa_demo",
  "status": "ACTIVE"
}
```

## Login Local

Endpoint:

```http
POST http://localhost:8080/erp-rrhh/v1/auth/login
X-Tenant-Subdomain: empresa-demo
Content-Type: application/json
```

Body:

```json
{
  "email": "admin@empresa.com",
  "password": "Admin123"
}
```

Si el tenant es `empresa-prueba`, usar:

```http
X-Tenant-Subdomain: empresa-prueba
```

No usar:

```http
X-Tenant-Subdomain: tenant_empresa_prueba
```

## Login Produccion

Si frontend y backend viven bajo el mismo subdominio:

```text
https://empresa-demo.erp-rrhh.com/erp-rrhh/v1/auth/login
```

Angular no necesita mandar header, porque el backend lee:

```text
empresa-demo
```

desde el host.

Si frontend y backend estan separados:

```text
Frontend: https://empresa-demo.erp-rrhh.com
Backend:  https://api.erp-rrhh.com
```

Entonces Angular debe mandar:

```http
X-Tenant-Subdomain: empresa-demo
```

en el login.

## Instrucciones para Angular

### Detectar subdominio

```ts
const host = window.location.hostname;
const subdomain = host.split('.')[0];
```

Ejemplo:

```text
empresa-demo.erp-rrhh.com -> empresa-demo
```

### Login si backend esta separado

```ts
login(email: string, password: string) {
  const subdomain = window.location.hostname.split('.')[0];

  return this.http.post(
    'https://api.erp-rrhh.com/erp-rrhh/v1/auth/login',
    { email, password },
    {
      headers: {
        'X-Tenant-Subdomain': subdomain
      }
    }
  );
}
```

### Requests despues del login

Despues del login se usa JWT:

```http
Authorization: Bearer <token>
```

No es necesario mandar `X-Tenant-Subdomain` en cada request autenticada, porque el tenant ya esta dentro del JWT.

## SQL Util para Revisar

Ver tenants:

```sql
SELECT id, name, subdomain, schema_name, status
FROM public.tenants;
```

Ver permisos globales:

```sql
SELECT * FROM public.permissions;
```

Ver usuarios de un tenant:

```sql
SELECT id, username, email, enabled, estado
FROM tenant_empresa_demo.usuarios;
```

Ver roles:

```sql
SELECT * FROM tenant_empresa_demo.roles;
```

Ver permisos del rol ADMIN:

```sql
SELECT r.name, p.code
FROM tenant_empresa_demo.roles r
JOIN tenant_empresa_demo.role_permissions rp ON rp.role_id = r.id
JOIN public.permissions p ON p.id = rp.permission_id
WHERE r.name = 'ADMIN';
```

## Resetear Tenant Local

Para borrar solo un tenant de prueba:

```sql
DROP SCHEMA tenant_empresa_demo CASCADE;

DELETE FROM public.tenants
WHERE subdomain = 'empresa-demo';
```

Para limpiar toda la base local:

```sql
DROP DATABASE erp_rrhh;
CREATE DATABASE erp_rrhh;
```

## Errores Comunes

### No se pudo resolver el tenant de la request

Causa:

- Falta `X-Tenant-Subdomain` en local.
- El host no tiene subdominio.

Solucion local:

```http
X-Tenant-Subdomain: empresa-demo
```

### Credenciales invalidas

Puede significar:

- Email no existe en ese tenant.
- Password no coincide con BCrypt.
- Estas usando header de otro tenant.

Revisar:

```sql
SELECT id, username, email, enabled, estado
FROM tenant_empresa_prueba.usuarios
WHERE email = 'admin@gmail.com';
```

### Tenant INACTIVE

Revisar:

```sql
SELECT id, subdomain, schema_name, status
FROM public.tenants;
```

Si esta en `INACTIVE`, el backend no deja loguear.

Para desarrollo se puede activar manualmente:

```sql
UPDATE public.tenants
SET status = 'ACTIVE',
    updated_at = CURRENT_TIMESTAMP
WHERE subdomain = 'empresa-demo';
```

## Pruebas

Comando usado:

```bash
mvn -q test
```

Tambien se agregaron tests para:

```text
TenantValidator
TenantResolver
TenantContext
TokenServiceImpl
TenantSchemaFlywayIntegrationTest
```

La prueba con Testcontainers se omite si Docker no esta disponible.

## Checklist Rapido

1. Arrancar backend.
2. Verificar Flyway public:

```sql
SELECT * FROM public.flyway_schema_history;
```

3. Crear tenant.
4. Verificar:

```sql
SELECT * FROM public.tenants;
SELECT * FROM tenant_empresa_demo.usuarios;
SELECT * FROM tenant_empresa_demo.roles;
SELECT * FROM tenant_empresa_demo.role_permissions;
```

5. Login local con:

```http
X-Tenant-Subdomain: empresa-demo
```

6. Usar token en endpoints:

```http
Authorization: Bearer <token>
```

