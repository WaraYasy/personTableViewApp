package es.wara.control;

import es.wara.dao.DaoPerson;
import es.wara.model.Person;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

/**
 * Controlador JavaFX para gestión de personas con operaciones asíncronas.
 * Maneja la interfaz de tabla y formularios con soporte multiidioma.
 * 
 * @author Wara Pacheco
 * @version 3.0
 * @since 2025-10-05
 */
public class TableViewController {

    // ===== CONTROLES DE LA INTERFAZ =========
    @FXML
    private Button btnAdd;                    // Botón para añadir nueva persona

    @FXML
    private Button btnDeleteRows;             // Botón para eliminar filas seleccionadas

    @FXML
    private Button btnRestoreRows;            // Botón para restaurar datos originales

    @FXML
    private DatePicker dateBirth;             // Selector de fecha de nacimiento

    @FXML
    private TableView<Person> tablePerson;    // Tabla principal de personas

    @FXML
    private TextField txtFirstName;           // Campo de texto para nombre

    @FXML
    private TextField txtLastName;            // Campo de texto para apellido

    @FXML
    private ProgressBar progressBar;          // Barra de progreso para operaciones asíncronas

    // ===== COLUMNAS DE LA TABLA =====
    @FXML
    private TableColumn<Person, Integer> colPersonId;

    @FXML
    private TableColumn<Person, String> colFirstName;

    @FXML
    private TableColumn<Person, String> colLastName;

    @FXML
    private TableColumn<Person, LocalDate> colBirthDate;

    /** Lista observable que alimenta la tabla */
    private ObservableList<Person> allThePeople = FXCollections.observableArrayList();

    /** Resource bundle para internacionalización */
    private ResourceBundle bundle;

    /** Logger para eventos y depuración */
    private static final Logger logger = LoggerFactory.getLogger(TableViewController.class);


    // Configuración para pruebas
    private static final boolean SIMULAR_CARGA_LENTA = true;
    private static final int MILISEGUNDOS_SIMULACION = 3000;
    /**
     * Inicializa el controlador configurando la tabla, columnas e idioma.
     * Se ejecuta automáticamente después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        // Configurar idioma de la aplicación
        // Locale locale = Locale.forLanguageTag("en");  // Para inglés
        Locale locale = Locale.forLanguageTag("es");     // Para español
        bundle = ResourceBundle.getBundle("es.wara.texts", locale);
        logger.debug("Resource bundle configurado para locale: {}", locale);

        // Vincular lista observable con la tabla
        tablePerson.setItems(allThePeople);
        // Permitir selección múltiple de filas
        tablePerson.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Mapear columnas con propiedades del modelo Person
        colPersonId.setCellValueFactory(new PropertyValueFactory<>("personId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        // Cargar datos iniciales
        cargarDatos();

        logger.debug("TableViewController inicializado correctamente");
    }



    /**
     * Añade una nueva persona validando los datos y ejecutando la inserción de forma asíncrona.
     */
    public void addPerson() {
        // Obtener datos del formulario
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        LocalDate birthDate = dateBirth.getValue();

        // Validar campos obligatorios
        if (!isValid(firstName, lastName, birthDate)) {
            return;  // Salir si hay errores de validación
        }

        // Crear nueva persona con los datos validados
        Person newPerson = new Person(firstName.trim(), lastName.trim(), birthDate);

        // Deshabilitar botón para evitar doble clic
        btnAdd.setDisable(true);
        gestionarProgreso(true);

        // Crear tarea asíncrona para no bloquear la UI
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                logger.info("Insertando persona: {}", newPerson.toString());

                // Simular carga lenta para demostración
                if (SIMULAR_CARGA_LENTA) {
                    Thread.sleep(MILISEGUNDOS_SIMULACION);
                }

                // Ejecutar inserción en base de datos
                boolean resultado = DaoPerson.addPersonSync(newPerson);
                logger.info("Inserción {}", resultado ? "exitosa" : "fallida");
                return resultado;
            }
        };

        // Manejar éxito de la operación
        tarea.setOnSucceeded(e -> {
            boolean insertado = tarea.getValue();
            btnAdd.setDisable(false);        // Reactivar botón
            gestionarProgreso(false);         // Ocultar barra de progreso

            if (insertado) {
                clearFields();
                allThePeople.add(newPerson);
                tablePerson.refresh();
                mostrarAlertInfo(btnAdd.getScene().getWindow(), bundle.getString("successAddPerson"));
            } else {
                mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorAddPerson"));
            }
        });

        // Manejar fallo de la operación
        tarea.setOnFailed(e -> {
            btnAdd.setDisable(false);     // Reactivar botón
            gestionarProgreso(false);      // Ocultar barra de progreso
            mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorAddPerson"));
        });
        
        // Ejecutar la tarea en un hilo separado
        new Thread(tarea).start();
    }


    /**
     * Elimina las personas seleccionadas en la tabla de forma asíncrona.
     * Valida la selección antes de proceder con la eliminación.
     */
    public void deleteSelectedRows() {
        // Obtener elementos seleccionados en la tabla
        ObservableList<Person> personasSeleccionadas = tablePerson.getSelectionModel().getSelectedItems();

        // Verificar que hay elementos seleccionados
        if (personasSeleccionadas.isEmpty()) {
            mostrarAlertInfo(btnDeleteRows.getScene().getWindow(), bundle.getString("errorDeleteRows"));
            return;
        }

        // Crear copia para evitar problemas de concurrencia
        List<Person> personasAEliminar = new ArrayList<>(personasSeleccionadas);
        logger.info("Iniciando eliminación de {} personas", personasAEliminar.size());

        btnDeleteRows.setDisable(true);
        gestionarProgreso(true);

        // Crear Task en el controller
        Task<Integer> tarea = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int eliminadas = 0;
                int total = personasAEliminar.size();

                for (Person persona : personasAEliminar) {
                    if (SIMULAR_CARGA_LENTA) {
                        Thread.sleep(MILISEGUNDOS_SIMULACION);
                    }

                    if (DaoPerson.deletePersonSync(persona)) {
                        eliminadas++;
                    }
                }

                logger.info("Eliminación completada: {}/{} personas", eliminadas, total);
                return eliminadas;
            }
        };

        tarea.setOnSucceeded(e -> {
            int eliminadas = tarea.getValue();
            btnDeleteRows.setDisable(false);
            gestionarProgreso(false);

            if (eliminadas > 0) {
                allThePeople.removeAll(personasAEliminar);
                tablePerson.refresh();

                String mensaje = String.format(bundle.getString("deleteSuccessMessage"),
                        eliminadas, allThePeople.size());
                mostrarAlertInfo(btnDeleteRows.getScene().getWindow(), mensaje);
            } else {
                mostrarAlertError(btnDeleteRows.getScene().getWindow(), bundle.getString("errorDeleteRows"));
            }
        });

        tarea.setOnFailed(e -> {
            logger.error("Error en la tarea:{} ", tarea.getException().getMessage(), tarea.getException());
            btnDeleteRows.setDisable(false);
            gestionarProgreso(false);
            mostrarAlertError(btnDeleteRows.getScene().getWindow(), bundle.getString("errorDeleteRows"));
        });

        new Thread(tarea).start();
    }
    /**
     * Restaura la tabla eliminando todos los datos actuales e insertando los datos originales.
     * Operación asíncrona que restaura los 4 registros básicos de The Beatles.
     */
    @FXML
    public void restoreRows() {
        logger.info("Iniciando restauración de datos básicos");
        // Guardar tamaño actual para mostrar en mensaje de confirmación
        int currentSize = allThePeople.size();

        // Deshabilitar controles durante la operación
        btnRestoreRows.setDisable(true);
        gestionarProgreso(true);

        // Crear tarea asíncrona para restauración
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // Simular carga lenta para demostración
                if (SIMULAR_CARGA_LENTA) {
                    Thread.sleep(MILISEGUNDOS_SIMULACION);
                }
                // Ejecutar restauración completa de datos
                return DaoPerson.restoreBasicData();
            }
        };

        tarea.setOnSucceeded(e -> {
            btnRestoreRows.setDisable(false);

            if (tarea.getValue()) {
                // Recargar desde BD después de restaurar
                cargarDatos();

                String mensaje = bundle.getString("restoreSuccessTitle") + "\n" +
                        String.format(bundle.getString("restoreSuccessPrevious"), currentSize) + "\n" +
                        String.format(bundle.getString("restoreSuccessNow"), allThePeople.size()) + "\n" +
                        bundle.getString("restoreSuccessData");
                mostrarAlertInfo(btnRestoreRows.getScene().getWindow(), mensaje);
            } else {
                gestionarProgreso(false);
                mostrarAlertError(btnRestoreRows.getScene().getWindow(), bundle.getString("errorRestore"));
            }
        });

        tarea.setOnFailed(e -> {
            btnRestoreRows.setDisable(false);
            gestionarProgreso(false);
            String mensaje = bundle.getString("errorRestore2") + tarea.getException().getMessage();
            mostrarAlertError(btnRestoreRows.getScene().getWindow(), mensaje);
        });

        new Thread(tarea).start();
    }

    /*-------------------------------------*/
    /*        Métodos Auxiliares           */
    /*-------------------------------------*/

    /**
     * Carga todas las personas desde la base de datos de forma asíncrona.
     * Actualiza la tabla automáticamente al completarse la carga.
     */
    private void cargarDatos() {
        gestionarProgreso(true);  // Mostrar indicador de carga

        // Crear tarea para cargar datos en segundo plano
        Task<ObservableList<Person>> task = new Task<ObservableList<Person>>() {
            @Override
            protected ObservableList<Person> call() throws Exception {
                logger.info("Cargando personas desde BD");

                // Simular carga lenta para demostración
                if (SIMULAR_CARGA_LENTA) {
                    Thread.sleep(MILISEGUNDOS_SIMULACION);
                }

                // Obtener datos desde la capa DAO
                ObservableList<Person> personas = DaoPerson.fillTableSync();
                logger.info("Carga completada: {} personas", personas.size());
                return personas;
            }
        };

        task.setOnSucceeded(e -> {
            allThePeople.setAll(task.getValue());
            gestionarProgreso(false);
            logger.debug("Datos cargados: {} personas", allThePeople.size());
        });

        task.setOnFailed(e -> {
            logger.error("Error al cargar datos: {}", task.getException().getMessage());
            gestionarProgreso(false);
            mostrarAlertError(btnAdd.getScene().getWindow(), "Error al cargar datos");
        });

        new Thread(task).start();
    }

    /**
     * Valida que los campos obligatorios estén completos y la fecha sea válida.
     * @param firstName nombre ingresado
     * @param lastName apellido ingresado  
     * @param birthDate fecha de nacimiento
     * @return true si todos los datos son válidos
     */
    private boolean isValid(String firstName, String lastName, LocalDate birthDate) {
        if (firstName == null || firstName.trim().isEmpty()) {
            mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorMissingFirstName"));
            return false;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorMissingLastName"));
            return false;
        }

        if (!Person.isValidBirthDate(birthDate)) {
            String mensaje = bundle.getString("errorBirthDate") + birthDate;
            mostrarAlertError(btnAdd.getScene().getWindow(), mensaje);
            return false;
        }

        return true;
    }

    /**
     * Limpia todos los campos del formulario de entrada.
     */
    private void clearFields() {
        logger.debug("Limpiando campos del formulario");
        txtFirstName.clear();      // Limpiar campo nombre
        txtLastName.clear();       // Limpiar campo apellido
        dateBirth.setValue(null);  // Limpiar selector de fecha
    }

    /**
     * Muestra un diálogo de error al usuario.
     * @param win ventana padre del diálogo
     * @param mensaje texto del error a mostrar
     */
    private void mostrarAlertError(Window win, String mensaje) {
        logger.error(mensaje);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(win);         // Vincular a ventana padre
        alert.setHeaderText(null);    // Sin encabezado
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();          // Mostrar y esperar respuesta
    }

    /**
     * Muestra un diálogo informativo al usuario.
     * @param win ventana padre del diálogo
     * @param mensaje texto informativo a mostrar
     */
    private void mostrarAlertInfo(Window win, String mensaje) {
        logger.info(mensaje);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(win);         // Vincular a ventana padre
        alert.setHeaderText(null);    // Sin encabezado
        alert.setTitle("Info");
        alert.setContentText(mensaje);
        alert.showAndWait();          // Mostrar y esperar respuesta
    }

    /**
     * Controla la visibilidad de la barra de progreso.
     * @param mostrar true para mostrar, false para ocultar
     */
    private void gestionarProgreso(boolean mostrar) {
        // Ejecutar en hilo de JavaFX para actualizar UI
        Platform.runLater(() -> {
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);  // Progreso indeterminado
            progressBar.setVisible(mostrar);  // Mostrar/ocultar barra
            if (!mostrar) {
                progressBar.progressProperty().unbind();  // Desvincular propiedades al ocultar
            }
        });
    }
}