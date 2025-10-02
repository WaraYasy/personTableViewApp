# 🍂🍁PeopleViewApp: Práctica con JavaFX, FXML y TableView🍂🍁

## Descripción

La aplicación implementa una **agenda personal multilingue** con interfaz gráfica utilizando JavaFX y persistencia en **base de datos MariaDB**. La interfaz está construida con una **TableView** para mostrar y manipular una lista de personas, con soporte completo de **internacionalización (i18n)** en español e inglés. Los usuarios pueden agregar, eliminar y restaurar registros de personas con información como nombre, apellido y fecha de nacimiento, con todos los datos almacenados de forma permanente en la base de datos.

## Objetivos

- Practicar el uso de **TableView** para visualización y manipulación de datos tabulares
- Integrar sistema de logging profesional con **SLF4J** y **Logback**
- Generar archivos **.jar ejecutables** con todas las dependencias
- Conectarse a una base de datos Maria DB en un contenedor Docker
- Crear una aplicación JavaFX bien estructurada siguiendo el patrón **Modelo-Vista-Controlador (MVC)**
- Crear una aplicación JavaFX bien estructurada y documentada que cumpla el 'decálogo🤯🫨' 

## Características

### Interfaz Gráfica
- **Diseño responsivo**: Interfaz construida con FXML y estilizada con CSS
- **Internacionalización**: Soporte completo para español e inglés con ResourceBundle
- **GridPane**: Contenedor principal que organiza elementos en cuadrícula de 3 columnas × 5 filas
- **TableView**: Tabla principal para visualizar la lista de personas con selección múltiple
- **Controles multilingues**: 
  - Campos de texto con prompts localizados
  - DatePicker para fecha de nacimiento
  - Botones de acción con textos traducidos
  - Tooltips informativos localizados en todos los controles
  - Mensajes de error y confirmación en el idioma seleccionado

### Funcionalidades
- **Agregar personas**: Formulario con validación de campos obligatorios y mensajes localizados
- **Eliminar registros**: Selección múltiple y eliminación segura con confirmaciones
- **Restaurar datos**: Restablece la tabla a su estado inicial con datos predefinidos
- **Validación avanzada**: Control de datos vacíos, fechas futuras y consistencia de datos
- **Alertas multilingues**: Mensajes de error e información localizados
- **Soporte de idiomas**: Cambio dinámico entre español e inglés
- **Gestión de configuración**: Carga segura de propiedades de base de datos

### Sistema Técnico
- **Logging profesional**: Sistema completo con SLF4J/Logback, logs contextuales y rotación
- **Internacionalización**: ResourceBundle con soporte para múltiples idiomas
- **Gestión de configuración**: Clase `Propiedades` para carga segura desde classpath
- **Modelo de datos robusto**: Clase `Person` con validaciones avanzadas
- **Patrón DAO**: Separación clara de acceso a datos con `DaoPerson`
- **Arquitectura modular**: MVC con documentación JavaDoc completa
- **Manejo de errores**: Try-catch comprehensivo con logging detallado
- **Ventana redimensionable**: Con límites mínimos (650×600) y máximos (900×900)

## Estructura del Proyecto

```
src/main/java/es/wara/
├── Lanzador.java              # Punto de entrada principal
├── PeopleViewApp.java         # Aplicación JavaFX principal
├── control/
│   └── TableViewController.java  # Controlador de la interfaz
├── dao/
│   ├── ConectionDB.java       # Gestión de conexiones de base de datos
│   └── DaoPerson.java         # Operaciones CRUD para Person
└── model/
    └── Person.java            # Modelo de datos de Persona

src/main/resources/
├── logback.xml                # Configuración avanzada de logging
└── es/wara/
    ├── configuration.properties  # 🚨 Configuración de BD (classpath)
    ├── texts.properties          # Textos base para i18n
    ├── texts_es.properties       # Textos en español (con únicos)
    ├── texts_en.properties       # Textos en inglés
    ├── fxml/
    │   └── tableView.fxml        # FXML
    ├── css/
    │   └── style.css             # Estilos CSS
    └── sql/
        └── init.sql              # Script de inicialización de BD
```

## Requisitos

- **Java 11** o superior
- **Maven 3.8** o superior
- **MariaDB** para la base de datos
- **Dependencias gestionadas automáticamente** por Maven (ver `pom.xml`):
  - JavaFX Controls (21.0.5)
  - JavaFX FXML (21.0.5)
  - SLF4J API (2.0.13)
  - Logback Classic y Core (1.5.13)
  - MariaDB Java Client (3.5.6)

## Configuración de Base de Datos

### Archivo de Configuración
La aplicación requiere un archivo `configuration.properties` en la carpeta `src/main/resources/es/wara/` con la siguiente estructura:

```properties
# Configuración de Base de Datos
db.host=localhost
db.port=3306
db.name=dbpersonas
db.user=tu_usuario
db.password=tu_contraseña
```

### Configuración de la Base de Datos
1. **Crear la base de datos**: Ejecuta el script `src/main/resources/es/wara/sql/init.sql` en tu servidor de base de datos
2. **Configurar credenciales**: Modifica el archivo `configuration.properties` con tus credenciales de base de datos
3. **Verificar conexión**: La aplicación intentará conectarse automáticamente al iniciar

🚨🚨🚨**Importante**: Asegúrate de que el archivo `configuration.properties` esté incluido en tu `.gitignore` para no exponer credenciales de base de datos.

## Ejecución

### Con Maven (Recomendado)
Para compilar y ejecutar el proyecto con Maven:

```bash
mvn clean javafx:run
```

### Compilación y empaquetado
Para crear un JAR ejecutable con todas las dependencias:

```bash
mvn clean package
```

Esto generará un archivo JAR en `target/` junto con las librerías necesarias en `target/libs/`.

### Ejecución del JAR
Una vez compilado, puedes ejecutar el JAR directamente:

```bash
java -jar target/tableViewApp-1.0-SNAPSHOT.jar
```

## Documentación

Para generar la documentación JavaDoc:

```bash
mvn javadoc:javadoc
```

La documentación se generará en `target/apidocs/`.

## Logging y Monitoreo

La aplicación incluye un sistema de logging profesional configurado con **SLF4J** y **Logback** que registra eventos detallados en múltiples niveles:

### Archivos de Log:
- **Consola**: Mensajes de depuración durante el desarrollo (DEBUG y superior)
- **`logs/PeopleViewApp-all.log`** - Todos los eventos (DEBUG, INFO, WARN, ERROR)
- **`logs/PeopleViewApp-info.log`** - Solo eventos informativos y superiores (INFO, WARN, ERROR)

### Características del Sistema de Logging:
- **Logging contextual**: Cada operación CRUD se registra con detalles
- **Múltiples niveles**: DEBUG para desarrollo, INFO para operaciones, WARN/ERROR para problemas
- **Rotación automática**: Por tamaño (50MB-100MB por archivo)
- **Histórico**: Conserva logs por 30 días
- **Control de espacio**: Límite total de 500MB-1GB
- **Formato estructurado**: Timestamp, nivel, clase, y mensaje detallado

### Ejemplo de Logs:
```
2024-10-02 10:15:30 INFO  [TableViewController] - Iniciando controlador de tabla
2024-10-02 10:15:35 DEBUG [DaoPerson] - Ejecutando consulta: SELECT * FROM persona
2024-10-02 10:16:12 INFO  [TableViewController] - Persona agregada: Juan Pérez
```

## Internacionalización (i18n)

La aplicación soporta completamente **múltiples idiomas** utilizando el patrón ResourceBundle de Java:

### Idiomas Soportados:
- **Español**: Idioma por defecto con soporte completo para caracteres especiales (ñ, á, é, í, ó, ú)
- **Inglés**: Traducción completa de toda la interfaz

### Archivos de Recursos:
- `texts.properties` - Textos base (fallback)
- `texts_es.properties` - Textos en español con codificación Unicode
- `texts_en.properties` - Textos en inglés

### Elementos Localizados:
- **Etiquetas de interfaz**: Botones, campos, títulos
- **Mensajes de validación**: Errores y advertencias
- **Tooltips**: Ayuda contextual
- **Mensajes de confirmación**: Diálogos y alertas
- **Prompts de campos**: Textos de ayuda en formularios

### Características Técnicas:
- **Carga dinámica**: El idioma se determina automáticamente por la configuración del sistema
- **Codificación Unicode**: Soporte completo para caracteres especiales (\u00f1 para ñ)
- **Referencias FXML**: Uso de `%key` para carga automática de textos
- **Fallback inteligente**: Si falta una traducción, usa el texto base

## Funcionalidades Detalladas

### Gestión de Personas
1. **Agregar**: Completa los campos localizados de nombre, apellido y fecha de nacimiento, luego presiona el botón "Añadir"/"Add"
2. **Eliminar**: Selecciona una o múltiples filas en la tabla y presiona "Eliminar Seleccionadas"/"Delete Selected Rows"
3. **Restaurar**: Presiona "Restaurar Filas"/"Restore Rows" para volver a los datos iniciales (✨The Beatles ✨)
4. **Validaciones automáticas**: El sistema valida los datos y muestra mensajes de error localizados
5. **Confirmaciones**: Todas las operaciones destructivas requieren confirmación del usuario

### Validaciones Avanzadas
- **Campos obligatorios**: Nombres y apellidos no pueden estar vacíos
- **Validación temporal**: Las fechas de nacimiento no pueden ser futuras
- **Consistencia de datos**: Verificación de integridad antes de operaciones
- **Mensajes contextuales**: Errores y advertencias localizados según el idioma
- **Confirmaciones de eliminación**: Prevención de pérdida accidental de datos
- **Logging de validaciones**: Registro de todas las validaciones para auditoría

### Datos Predefinidos
La aplicación incluye datos de ejemplo de **✨The Beatles🥧✨**:
- John Lennon (1940-10-09)
- Paul McCartney (1942-06-18)  
- George Harrison (1943-02-25)
- Ringo Starr (1940-07-07)

---


*Ejercicio de DEIN para reforzar conceptos de JavaFX, FXML, TableView y sistemas de logging. Ahora con soporte multilingue. Feliz revisión otoñal de ejercicios Israel 🎃🍂🍁*
