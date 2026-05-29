# Documentacion de Flujo del Proyecto y Auth

## 1. Flujo General del Backend

El backend es una API REST construida con Spring Boot. La ruta base comun esta definida en `ApiConfig`:

```text
/erp_rrhh/v1
```

Por ejemplo:

```text
POST /erp_rrhh/v1/auth/login
GET  /erp_rrhh/v1/roles
GET  /erp_rrhh/v1/permissions
```

La aplicacion usa:

- Spring Web para exponer endpoints REST.
- Spring Data JPA para persistencia.
- PostgreSQL como base de datos.
- Spring Security para autenticacion y autorizacion.
- JWT en cookie HTTP-only para mantener la sesion.
- BCrypt para almacenar contrasenas.
- Springdoc OpenAPI para Swagger.

## 2. Arquitectura de Auth

El modulo `auth` maneja:

- Usuarios.
- Roles.
- Permisos.
- Login/logout.
- JWT.
- Reglas de acceso por permisos.

El modelo principal es:

```text
Usuario N:M Rol
Rol     N:M Permiso
```

Tablas principales:

- `usuarios`
- `roles`
- `permissions`
- `usuario_roles`
- `role_permissions`

Los roles no autorizan directamente los endpoints. Los roles solo agrupan permisos.

La autorizacion real se hace con permisos:

```java
@PreAuthorize("hasAuthority('USUARIO_CREAR')")
```

No se debe usar como regla principal:

```java
@PreAuthorize("hasRole('ADMIN')")
```

## 3. Flujo de Login

Endpoint:

```http
POST /erp_rrhh/v1/auth/login
```

Body:

```json
{
  "email": "admin@erp.com",
  "password": "Admin123"
}
```

Flujo interno:

1. El usuario envia email y contrasena.
2. Spring Security busca el usuario por email.
3. Se valida que el usuario exista y este activo.
4. BCrypt valida la contrasena.
5. Se cargan roles activos y permisos activos del usuario.
6. Los permisos se convierten en `GrantedAuthority`.
7. Se genera un JWT con datos minimos.
8. El JWT se devuelve en una cookie llamada `auth-token`.

Respuesta esperada:

```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

La cookie queda configurada como:

```text
Nombre: auth-token
HttpOnly: true
Secure: false en desarrollo local
SameSite: Strict
```

Importante: el JWT no guarda permisos. En cada request se vuelve a cargar el usuario desde base de datos. Esto permite que si se cambia un permiso o rol, el cambio aplique sin esperar a que expire el token.

Para Swagger y pruebas del frontend tambien se puede enviar el token en el header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 4. Flujo de Autorizacion

Despues del login, cada request privado sigue este flujo:

1. `JwtAuthenticationFilter` busca la cookie `auth-token`.
2. Valida el JWT.
3. Extrae el email del token.
4. Carga el usuario desde base de datos con roles y permisos.
5. `Usuario.getAuthorities()` convierte permisos activos en authorities.
6. Spring Security evalua `@PreAuthorize`.

Ejemplo:

```java
@PreAuthorize("hasAuthority('ROL_CREAR')")
@PostMapping("/roles")
public ResponseEntity<RoleResponseDTO> createRole(...) {
    ...
}
```

Si el usuario tiene el permiso `ROL_CREAR`, accede. Si no lo tiene, recibe `403 Forbidden`.

## 5. Endpoints Publicos

Estos endpoints no requieren autenticacion:

```http
POST /erp_rrhh/v1/auth/login
GET  /swagger-ui/**
GET  /swagger-ui.html
GET  /v3/api-docs/**
```

Todo endpoint bajo:

```text
/erp_rrhh/v1/**
```

requiere autenticacion, excepto login.

## 6. Swagger

Con la aplicacion corriendo localmente, Swagger se abre en:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Si el puerto cambia, reemplazar `8080` por el puerto configurado.

### Uso de Swagger con Login

1. Abrir:

```text
http://localhost:8080/swagger-ui/index.html
```

2. Ejecutar:

```http
POST /erp_rrhh/v1/auth/login
```

3. Copiar el valor del campo `token` que responde el login.
4. Presionar el boton **Authorize** en Swagger.
5. Pegar el token. Swagger usa esquema Bearer, asi que puedes pegar solo el JWT o `Bearer <token>` segun lo pida la interfaz.
6. Ejecutar endpoints protegidos desde Swagger.

Nota: el backend tambien deja la cookie `auth-token`, pero para Swagger lo mas simple es usar el boton **Authorize** con Bearer JWT.

## 7. Endpoints de Auth

### Login

```http
POST /erp_rrhh/v1/auth/login
```

Publico.

Body:

```json
{
  "email": "admin@erp.com",
  "password": "Admin123"
}
```

### Logout

```http
POST /erp_rrhh/v1/auth/logout
```

Requiere autenticacion.

Elimina la cookie `auth-token`.

### Crear Usuario

```http
POST /erp_rrhh/v1/auth/usuarios
```

Permiso requerido:

```text
USUARIO_CREAR
```

Body:

```json
{
  "username": "jlopez",
  "password": "Usuario123",
  "firstName": "Juan",
  "surnames": "Lopez",
  "email": "jlopez@erp.com",
  "phoneNumber": "70000000",
  "roleName": "Administrador RRHH"
}
```

### Consultar Usuario por ID

```http
GET /erp_rrhh/v1/auth/usuarios/{id}
```

Permiso requerido:

```text
USUARIO_LISTAR
```

### Asignar Rol a Usuario

Opcion con body:

```http
POST /erp_rrhh/v1/auth/usuarios/{usuarioId}/roles
```

Body:

```json
{
  "roleId": 1
}
```

Opcion por path:

```http
POST /erp_rrhh/v1/auth/usuarios/{usuarioId}/roles/{roleId}
```

Permiso requerido:

```text
USUARIO_ASIGNAR_ROL
```

Validaciones:

- El usuario debe existir.
- El usuario debe estar activo.
- El rol debe existir.
- El rol debe estar activo.
- No se permite duplicar la asignacion usuario-rol.

## 8. Endpoints de Roles

### Listar Roles

```http
GET /erp_rrhh/v1/roles
```

Permiso requerido:

```text
ROL_LISTAR
```

### Consultar Rol por ID

```http
GET /erp_rrhh/v1/roles/{id}
```

Permiso requerido:

```text
ROL_LISTAR
```

### Crear Rol

```http
POST /erp_rrhh/v1/roles
```

Permiso requerido:

```text
ROL_CREAR
```

Body:

```json
{
  "name": "Reclutador",
  "description": "Gestiona procesos de seleccion",
  "permissionCodes": [
    "USUARIO_LISTAR",
    "PERMISO_LISTAR"
  ]
}
```

Validaciones:

- No puede existir otro rol con el mismo nombre.
- Los permisos indicados deben existir.
- Los permisos indicados deben estar activos.

### Actualizar Rol

```http
PUT /erp_rrhh/v1/roles/{id}
```

Permiso requerido:

```text
ROL_EDITAR
```

Body:

```json
{
  "description": "Gestiona procesos de seleccion y entrevistas",
  "permissionCodes": [
    "USUARIO_LISTAR",
    "PERMISO_LISTAR",
    "ROL_LISTAR"
  ]
}
```

### Desactivar Rol

```http
DELETE /erp_rrhh/v1/roles/{id}
```

Permiso requerido:

```text
ROL_ELIMINAR
```

### Asignar Permiso a Rol

Opcion con body:

```http
POST /erp_rrhh/v1/roles/{roleId}/permissions
```

Body:

```json
{
  "permissionId": 1
}
```

Opcion por path:

```http
POST /erp_rrhh/v1/roles/{roleId}/permissions/{permissionId}
```

Permiso requerido:

```text
ROL_ASIGNAR_PERMISO
```

Validaciones:

- El rol debe existir.
- El rol debe estar activo.
- El permiso debe existir.
- El permiso debe estar activo.
- No se permite duplicar la asignacion rol-permiso.

## 9. Endpoints de Permisos

### Listar Permisos

```http
GET /erp_rrhh/v1/permissions
```

Permiso requerido:

```text
PERMISO_LISTAR
```

### Consultar Permiso por ID

```http
GET /erp_rrhh/v1/permissions/{id}
```

Permiso requerido:

```text
PERMISO_LISTAR
```

### Crear Permiso

```http
POST /erp_rrhh/v1/permissions
```

Permiso requerido:

```text
PERMISO_CREAR
```

Body:

```json
{
  "code": "EMPLEADO_CREAR",
  "description": "Crear empleados"
}
```

El codigo se normaliza:

- Se convierte a mayusculas.
- Los espacios se convierten en `_`.

Ejemplo:

```text
empleado crear -> EMPLEADO_CREAR
```

## 10. Permisos Base Cargados al Iniciar

Al arrancar la aplicacion, `BasePermissionSeeder` crea permisos base si no existen:

```text
USUARIO_CREAR
USUARIO_LISTAR
USUARIO_EDITAR
USUARIO_ELIMINAR
USUARIO_ASIGNAR_ROL
ROL_CREAR
ROL_LISTAR
ROL_EDITAR
ROL_ELIMINAR
ROL_ASIGNAR_PERMISO
PERMISO_CREAR
PERMISO_LISTAR
```

El seed es idempotente: si el permiso ya existe, no lo duplica.

## 11. Primer Acceso al Sistema

Como todos los endpoints administrativos estan protegidos por permisos, para el primer acceso necesitas un usuario inicial con un rol que tenga permisos.

Opciones recomendadas:

1. Crear un seed temporal de desarrollo para un rol administrador y un usuario inicial.
2. Insertar el usuario/rol/permisos directamente en base de datos.
3. Crear un comando interno de bootstrap que luego se desactive.

Para produccion, evitar dejar un usuario admin hardcodeado.

## 12. Ejemplo de Uso con cURL

Login guardando cookies:

```bash
curl -i -c cookies.txt \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"admin@erp.com\",\"password\":\"Admin123\"}" \
  http://localhost:8080/erp_rrhh/v1/auth/login
```

Consumir endpoint protegido usando la cookie:

```bash
curl -i -b cookies.txt \
  http://localhost:8080/erp_rrhh/v1/roles
```

Consumir endpoint protegido usando Bearer token:

```bash
curl -i \
  -H "Authorization: Bearer TU_TOKEN" \
  http://localhost:8080/erp_rrhh/v1/roles
```

## 13. Endpoints Organizacionales

Los controladores organizacionales tambien estan bajo la ruta base:

```text
/erp_rrhh/v1/areas
/erp_rrhh/v1/cargos
/erp_rrhh/v1/departamentos
```

Actualmente, por configuracion global, requieren autenticacion. Si se desea aplicar autorizacion fina, agregar permisos especificos:

```java
@PreAuthorize("hasAuthority('AREA_CREAR')")
@PostMapping
public ResponseEntity<?> create(...) {
    ...
}
```

## 14. Como Agregar un Nuevo Permiso a un Modulo

Ejemplo: proteger creacion de empleados.

1. Crear permiso:

```http
POST /erp_rrhh/v1/permissions
```

```json
{
  "code": "EMPLEADO_CREAR",
  "description": "Crear empleados"
}
```

2. Asignar permiso a un rol:

```http
POST /erp_rrhh/v1/roles/{roleId}/permissions/{permissionId}
```

3. Proteger endpoint:

```java
@PreAuthorize("hasAuthority('EMPLEADO_CREAR')")
@PostMapping
public ResponseEntity<?> createEmpleado(...) {
    ...
}
```

4. El usuario con ese rol podra acceder en su siguiente request, porque los permisos se cargan desde base de datos en cada peticion.

## 15. Notas para Futuro SaaS

El diseno actual deja la base lista para SaaS:

- `Permission` representa capacidades globales del sistema.
- `Role` representa agrupaciones configurables por empresa.
- `Usuario` puede tener multiples roles.
- La autorizacion no depende de nombres de roles.

Cuando se implemente multi-tenant, lo recomendable es:

- Crear entidad `Empresa` o `Tenant`.
- Relacionar `Role` con `tenant_id`.
- Hacer unico el nombre del rol por empresa, no globalmente.
- Filtrar consultas de usuarios y roles por tenant.
- Incluir en JWT solo un identificador no sensible del tenant activo.
- Mantener permisos globales y estables.
