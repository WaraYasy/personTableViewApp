# PeopleViewApp: Práctica con JavaFX, FXML y TableView

Este proyecto es un ejercicio para practicar habilidades avanzadas en JavaFX, centrándose en el uso de **TableView** para la gestión de datos, sistema de logging profesional, y empaquetación de aplicaciones en archivos .jar ejecutables.

## Descripción

La aplicación implementa una **agenda personal** con interfaz gráfica utilizando JavaFX y persistencia en **base de datos MariaDB**. La interfaz está construida con un **GridPane** como contenedor principal que organiza los controles de manera estructurada, y una **TableView** para mostrar y manipular una lista de personas. Los usuarios pueden agregar, eliminar y restaurar registros de personas con información como nombre, apellido y fecha de nacimiento, con todos los datos almacenados de forma permanente en la base de datos.

## Objetivos

- Practicar el uso de **TableView** para visualización y manipulación de datos tabulares
- Integrar sistema de logging profesional con **SLF4J** y **Logback**
- Generar archivos **.jar ejecutables** con todas las dependencias
- Crear una aplicación JavaFX bien estructurada siguiendo el patrón **Modelo-Vista-Controlador (MVC)**
- Crear una aplicación JavaFX bien estructurada y documentada que cumpla el 'decálogo🤯🫨' 

## Características

### Interfaz Gráfica
- **Diseño responsivo**: Interfaz construida con FXML y estilizada con CSS
- **GridPane**: Contenedor principal que organiza elementos en cuadrícula de 3 columnas × 5 filas
- **TableView**: Tabla principal para visualizar la lista de personas con selección múltiple
- **Controles incluidos**: 
  - Campos de texto para nombre y apellidos
  - DatePicker para fecha de nacimiento
  - Botones de acción (Add, Delete Selected Rows, Restore Rows)
  - Tooltips informativos en todos los controles

### Funcionalidades
- **Agregar personas**: Formulario con validación de campos obligatorios
- **Eliminar registros**: Selección múltiple y eliminación segura de filas
- **Restaurar datos**: Restablece la tabla a su estado inicial con datos predefinidos
- **Validación**: Control de datos vacíos y fechas futuras
- **Alertas informativas**: Mensajes de error e información mediante cuadros de diálogo

### Sistema Técnico
- **Logging avanzado**: Registra eventos de aplicación en múltiples niveles y archivos
- **Modelo de datos**: Clase `Person` con validaciones y generación automática de IDs
- **Arquitectura modular**: Separación clara entre modelo, vista y controlador
- **Ventana redimensionable**: Con límites mínimos (800×600) y máximos (900×900)

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
├── configuration.properties   # 🚨Configuración de base de datos
├── logback.xml                # Configuración de logging
└── es/wara/
    ├── fxml/
    │   └── tableView.fxml     # Definición de la interfaz
    ├── css/
    │   └── style.css          # Estilos CSS
    └── sql/
        └── init.sql           # Script de inicialización de BD
```

## Requisitos

- **Java 11** o superior
- **Maven 3.8** o superior
- **MariaDB** para la base de datos)
- **Dependencias gestionadas automáticamente** por Maven (ver `pom.xml`):
  - JavaFX Controls (21.0.5)
  - JavaFX FXML (21.0.5)
  - SLF4J API (2.0.13)
  - Logback Classic y Core (1.5.13)
  - MariaDB Java Client (3.5.6)

## Configuración de Base de Datos

### Archivo de Configuración
La aplicación requiere un archivo `configuration.properties` en la carpeta `src/main/resources/` con la siguiente estructura:

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

## Logging

La aplicación incluye un sistema de logging configurado con Logback que registra eventos en múltiples niveles:

- **Consola**: Mensajes de depuración durante el desarrollo
- **`logs/PeopleViewApp-all.log`** - Todos los eventos (DEBUG, INFO, WARN, ERROR)
- **`logs/PeopleViewApp-info.log`** - Solo eventos informativos (INFO)

### Configuración de logs:
- Rotación automática por tamaño (50MB-100MB por archivo)
- Histórico de 30 días
- Límite total de espacio (500MB-1GB)

## Funcionalidades Detalladas

### Gestión de Personas
1. **Agregar**: Completa los campos de nombre, apellido y fecha de nacimiento, luego presiona "Add"
2. **Eliminar**: Selecciona una o múltiples filas en la tabla y presiona "Delete Selected Rows"
3. **Restaurar**: Presiona "Restore Rows" para volver a los datos iniciales (✨The Beatles ✨)

### Validaciones
- Nombres y apellidos no pueden estar vacíos
- Las fechas de nacimiento no pueden ser futuras
- Mensajes de error informativos para guiar al usuario

### Datos Predefinidos
La aplicación incluye datos de ejemplo de **✨The Beatles🥧✨**:
- John Lennon (1940-10-09)
- Paul McCartney (1942-06-18)  
- George Harrison (1943-02-25)
- Ringo Starr (1940-07-07)

---

*Ejercicio de DEIN para reforzar conceptos de JavaFX, FXML y TableView. Feliz revisión otoñal de ejercicios Israel 🎃🍂🍁*
