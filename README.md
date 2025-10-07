# ✨PeopleViewApp - Versión Asíncrona - Práctica con JavaFX, FXML, Maria DB y TableView✨

Este proyecto es un ejercicio para pra## 📝Documentación

Para generar la documentación JavaDoc:

```bash
mvn javadoc:javadoc
```

La documentación se generará en `target/apidocs/`.

### 🏠 Arquitectura Refactorizada

Esta versión incluye mejoras arquitectónicas significativas:

- **Métodos utilitarios**: `prepareAsyncOperation()` y `completeAsyncOperation()` para operaciones consistentes
- **Reducción de duplicación**: Eliminación de ~18 líneas de código repetitivo
- **JavaDoc profesional**: Documentación completa con ejemplos de código y enlaces cruzados
- **UI responsiva**: Operaciones que nunca bloquean la interfaz de usuariobilidades avanzadas en JavaFX, centrándose en el uso de **TableView** para la gestión de datos, sistema de logging profesional, y empaquetación de aplicaciones en archivos .jar ejecutables.

##📋Descripción

La aplicación implementa una **agenda personal multilingue** con interfaz gráfica utilizando JavaFX y persistencia en **base de datos MariaDB**. La interfaz está construida con una **TableView** para mostrar y manipular una lista de personas, con soporte completo en español e inglés. Los usuarios pueden agregar, eliminar y restaurar registros de personas con información como nombre, apellido y fecha de nacimiento, con todos los datos almacenados de forma permanente en la base de datos.

**🚀 Versión Refactorizada**: Incluye operaciones **completamente asíncronas** que no bloquean la interfaz de usuario, métodos utilitarios para mejor mantenibilidad y documentación JavaDoc profesional.

## 🏁Objetivos

- Practicar el uso de **TableView** para visualización y manipulación de datos tabulares
- Integrar sistema de logging profesional con **SLF4J** y **Logback**
- Generar archivos **.jar ejecutables** con todas las dependencias
- Conectarse a una base de datos Maria DB en un contenedor Docker
- Crear una aplicación JavaFX bien estructurada siguiendo el patrón **Modelo-Vista-Controlador (MVC)**
- Crear una aplicación JavaFX bien estructurada y documentada que cumpla el 'decálogo🤯🫨'

## Características

### 🖥️Interfaz Gráfica
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

### 📍Funcionalidades
- **Agregar personas**: Formulario con validación de campos obligatorios y mensajes localizados
- **Eliminar registros**: Selección múltiple y eliminación segura con confirmaciones
- **Restaurar datos**: Restablece la tabla a su estado inicial con datos predefinidos
- **Operaciones asíncronas**: Todas las operaciones de BD se ejecutan sin bloquear la UI
- **Indicadores de progreso**: Barras de progreso y feedback visual durante operaciones
- **Validación avanzada**: Control de datos vacíos, fechas futuras y consistencia de datos
- **Alertas multilingues**: Mensajes de error e información localizados
- **Soporte de idiomas**: Cambio dinámico entre español e inglés
- **Gestión de configuración**: Carga segura de propiedades de base de datos

## 🗄️Estructura del Proyecto

```
src/main/java/es/wara/
├── Lanzador.java              # Punto de entrada principal
├── PeopleViewApp.java         # Aplicación JavaFX principal
├── control/
│   └── TableViewController.java  # Controlador refactorizado con operaciones asíncronas
├── dao/
│   ├── ConectionDB.java       # Gestión de conexiones de base de datos
│   └── DaoPerson.java         # Operaciones CRUD para Person
└── model/
    └── Person.java            # Modelo de datos de Persona

src/main/resources/
├── logback.xml                # Configuración avanzada de logging
└── es/wara/
    ├── configuration.properties  # 🚨Configuración de BD 🚨
    └── Resource Bundle 'texts'
    │    ├── texts_es.properties       # Textos en español (con únicos)
    │    └── texts_en.properties       # Textos en inglés
    ├── fxml/
    │   └── tableView.fxml        # FXML
    ├── css/
    │   └── style.css             # Estilos CSS
    └── sql/
        └── init.sql              # Script de inicialización de BD
```

## 🗒️Requisitos

- **Java 11** o superior
- **Maven 3.8** o superior
- **MariaDB** para la base de datos
- **Dependencias gestionadas automáticamente** por Maven (ver `pom.xml`):
    - JavaFX Controls (21.0.5)
    - JavaFX FXML (21.0.5)
    - SLF4J API (2.0.13)
    - Logback Classic y Core (1.5.13)
    - MariaDB Java Client (3.5.6)

## ⚙️Configuración de Base de Datos

### Archivo de Configuración
La aplicación requiere un archivo `configuration.properties` en la carpeta `src/main/resources/es/wara/` con la siguiente estructura:

```properties
host=localhost
port=3306
user=tu_user
pass=tu_contraseña
database=nombre_base_de_datos
```
⚠️NOTA:Este fichero ya está creado en el repositorio, tu debes agregar las credenciales correspondientes..

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

## 🗂️Documentación

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

## 🌎Multilingue

La aplicación soporta completamente **múltiples idiomas** utilizando el patrón ResourceBundle de Java:

### Idiomas Soportados:
- **Español**: Idioma por defecto con soporte completo para caracteres especiales (ñ, á, é, í, ó, ú)
- **Inglés**: Traducción completa de toda la interfaz

### Archivos de Recursos:
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
4. **Operaciones asíncronas**: Todas las operaciones muestran indicadores de progreso y no bloquean la UI
5. **Validaciones automáticas**: El sistema valida los datos y muestra mensajes de error localizados
6. **Confirmaciones**: Todas las operaciones destructivas requieren confirmación del usuario

### Datos Predefinidos
La aplicación incluye datos de ejemplo de **✨The Beatles🥧✨**:
- John Lennon (1940-10-09)
- Paul McCartney (1942-06-18)
- George Harrison (1943-02-25)
- Ringo Starr (1940-07-07)

---

*Ejercicio de DEIN para reforzar conceptos de JavaFX, FXML, TableView y sistemas de logging. Ahora con soporte multilingue.✨*