# ERP Móvil Flutter

Aplicación móvil desarrollada en Flutter para la gestión de empleados (ERP), implementando **Clean Architecture** y manejo de estados con **BLoC**.

## Características Principales

- **Diseño Empresarial**: Interfaz de usuario profesional basada en colores blancos y azules.
- **Autenticación (Simulada)**: Inicio de sesión y registro de empleados. Los datos se persisten localmente utilizando `shared_preferences`.
- **Portal del Empleado (Home)**: Panel de control con acceso a diferentes módulos y un directorio estático de empleados.
- **Módulo de Turnos**: Visualización de la agenda de turnos asignados (datos estáticos mockeados).
- **Módulo de Permisos**: Solicitud de permisos laborales. Incluye simulación de carga y un mensaje indicando que el administrador debe aprobarlo.
- **Módulo de Vacaciones**: Solicitud de días de vacaciones, visualización de días disponibles e integración con la misma simulación de aprobación del administrador.
- **Notificaciones**: Bandeja de notificaciones simulada para visualizar respuestas de "administración" a solicitudes previas.

## Estructura del Proyecto (Clean Architecture)

El proyecto sigue una estructura limpia para asegurar la escalabilidad:

```text
lib/
├── core/                  # Código transversal para toda la app
│   ├── constants/         # Colores, tamaños, etc.
│   ├── errors/            # Excepciones y fallos
│   ├── theme/             # Tema global (Colores empresariales)
│   └── utils/             # Utilidades varias (ej. formateadores de fecha)
│
├── features/              # Funcionalidades de la aplicación (Módulos)
│   ├── auth/              # Autenticación (Login, Registro)
│   ├── home/              # Dashboard principal y notificaciones
│   ├── permissions/       # Solicitar permisos laborales
│   ├── shifts/            # Consultar turnos de trabajo
│   └── vacations/         # Solicitar vacaciones
│
├── injection_container.dart # Configuración de GetIt (Inyección de dependencias)
└── main.dart              # Punto de entrada de la aplicación
```

Cada feature dentro de la carpeta `features/` (como `auth`) se divide típicamente en 3 capas:
1. **Domain**: Entidades abstractas, repositorios base y Casos de Uso.
2. **Data**: Modelos (serialización), DataSources y la implementación de los repositorios.
3. **Presentation**: Gestores de estado (BLoC), páginas (UI) y widgets.

## Instalación y Ejecución

1. Clona el repositorio.
2. Asegúrate de tener Flutter instalado y configurado en tu entorno.
3. Descarga las dependencias:
   ```bash
   flutter pub get
   ```
4. Ejecuta la aplicación en un dispositivo o emulador:
   ```bash
   flutter run
   ```

## Documentación
Todo el código está diseñado para ser autoexplicativo y contiene comentarios descriptivos en **español** para facilitar el mantenimiento y escalabilidad del equipo.
