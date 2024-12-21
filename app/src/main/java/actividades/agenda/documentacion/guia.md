# Sobre el proyecto

Este es el abc del proyecto. Puedes ver los md copiando y pegando en:
[Markdown Live Preview](https://markdownlivepreview.com/)

## Problemática
Se requiere una app para móvil capaz de registrar Tareas marcándolas como:
- Pendiente
- Completado
- Vencido

Con este fin de crean tareas que tienen:
- ID.
- Nombre.
- Fecha de Inicio y de Fin.
- El Estado (Pendiente, Completado o Vencido)
- Categoría o nivel de prioridad (Baja, Media o Alta)

Los estados deben de poder actualizarse según fecha de modo que puedan vencer las que están
pendientes.

El único atributo que no se debe poder modificar es el ID.

La fecha de una tarea no debe entrar en conflicto o repetirse con la fecha de otra.

Adicionalmente si alguna tarea pendiente llega a su fecha de inicio deberá de enviar una
notificación al usuario.

## ¿Qué se usa en este proyecto?
- Kotlin para la lógica de la app.
- Xml para los layouts que verán los usuarios.
- Guardado de datos con .txt, sqlite y MySQL.
- MySQL accesible desde Kotlin mediante un servicio web de Node JS.
- Testing para realizar pruebas en carpetas (androidTest) y (Test).

## Diagrama de Flujo

- [Actividad Agenda](https://lucid.app/lucidchart/3b2c5713-9435-4cc6-acd9-16457f711577/edit?viewport_loc=-1646%2C-1486%2C4992%2C2460%2C0_0&invitationId=inv_400621b6-47e5-4346-b0c9-43f7818f5000) - Listado: Puede Filtrar, Buscar Tareas y Navegar entre Actividades.
- [Actividad Tarea](https://lucid.app/lucidchart/4d9609f4-76d1-44a2-b736-c56e3e16735a/edit?viewport_loc=-1098%2C-716%2C4992%2C2460%2C0_0&invitationId=inv_928f0f19-9a06-4ca4-b8db-27f38a63f56e) - Creación y Edición de Tareas.
- [Actividad Sesion]() - Inicio de Sesión y Registro de Usuario.


## Sobre la estructura física:

- manifests
  - AndroidManifest.xml // punto de entrada al ejecutar la aplicación
- kotlin+java
  - actividades.agenda
    - codigo
    - documentacion
      - planteamiento_base.md // Una guía para realmente entender el proyecto
      - plantillas.md // Guía base para entender los templates que usa Kotlin del lado del cliente.
      - sintaxis.md // Básico para entender el lenguaje que se usa en el proyecto
- res
  - drawable // carpeta para imagenes, íconos o fondos
  - layout // carpeta para los templates
  - mipmap // almacena íconos de apps de forma optimizada, asegura un escalado correcto
  - values // usar variables o modificadores espcíficos sobre los templates
  - xml // Configura diseño y comportamiento de la aplicación
- Gradle Scripts
  - build.gradle.kts (Project: Agenda)
    // Versiones de plugins, dependencias globales y configuraciones para los módulos del proyecto.
  - build.gradle.kts (Module: app)
    // dependencias del módulo, los plugins aplicados, las configuraciones de compilación y las
    // opciones específicas para el módulo
  - proguard-rules.pro (ProGuard Rules for ":app")
    // Contiene las reglas de ProGuard para la ofuscación y optimización del código
  - gradle.properties (Project Properties)
    // Permite definir propiedades globales para Gradle
  - gradle-wrapper.properties (Gradle Version)
    // Define la versión de Gradle que el proyecto utiliza
  - libs.versions.toml (Version Catalog)
    // Se utiliza para definir un catálogo de versiones para las dependencias del proyecto
  - local.properties (SDK Location)
    // Específica la ubicación del SDK de Android en el sistema local.
  - settings.gradle.kts (Project Settings)
    // qué módulos forman parte del proyecto de Gradle y cómo se deben vincular.
