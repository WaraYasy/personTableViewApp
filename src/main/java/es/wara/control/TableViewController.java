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
 * Controlador JavaFX para gestión de personas con soporte multilingüe.
 *
 * @author Wara Pacheco
 * @version 3.0
 * @since 2025-10-05
 */
public class TableViewController {

    // ===== CONTROLES DE LA INTERFAZ =========
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDeleteRows;

    @FXML
    private Button btnRestoreRows;

    @FXML
    private DatePicker dateBirth;

    @FXML
    private TableView<Person> tablePerson;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private ProgressBar progressBar;

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
     * Inicializa el controlador después de cargar el FXML
     */
    @FXML
    public void initialize() {
        // Configurar internacionalización
        // Locale locale = Locale.forLanguageTag("en");
        Locale locale = Locale.forLanguageTag("es");
        bundle = ResourceBundle.getBundle("es.wara.texts", locale);
        logger.debug("Resource bundle configurado para locale: {}", locale);

        // Configurar tabla
        tablePerson.setItems(allThePeople);
        tablePerson.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Configurar columnas
        colPersonId.setCellValueFactory(new PropertyValueFactory<>("personId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        // Cargar datos iniciales
        cargarDatos();

        logger.debug("TableViewController inicializado correctamente");
    }



    /**
     * Agrega una nueva persona a la base de datos de forma asíncrona
     */
    public void addPerson() {
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        LocalDate birthDate = dateBirth.getValue();

        //Validar campos
        if (!isValid(firstName, lastName, birthDate)) {
            return;
        }

        Person newPerson = new Person(firstName.trim(), lastName.trim(), birthDate);

        btnAdd.setDisable(true);
        gestionarProgreso(true);

        // Crear Task para que la acción sea asíncrona
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                logger.info("Insertando persona: {}", newPerson.toString());

                if (SIMULAR_CARGA_LENTA) {
                    Thread.sleep(MILISEGUNDOS_SIMULACION);
                }

                boolean resultado = DaoPerson.addPersonSync(newPerson);
                logger.info("Inserción {}", resultado ? "exitosa" : "fallida");
                return resultado;
            }
        };

        tarea.setOnSucceeded(e -> {
            boolean insertado = tarea.getValue();
            btnAdd.setDisable(false);
            gestionarProgreso(false);

            if (insertado) {
                clearFields();
                allThePeople.add(newPerson);
                tablePerson.refresh();
                mostrarAlertInfo(btnAdd.getScene().getWindow(), bundle.getString("successAddPerson"));
            } else {
                mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorAddPerson"));
            }
        });

        tarea.setOnFailed(e -> {
            btnAdd.setDisable(false);
            gestionarProgreso(false);
            mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorAddPerson"));
        });
        //La tarea se lleva a cabo en un hilo diferente
        new Thread(tarea).start();
    }


    /**
     * Elimina las filas seleccionadas de la tabla de forma asíncrona
     */
    public void deleteSelectedRows() {
        ObservableList<Person> personasSeleccionadas = tablePerson.getSelectionModel().getSelectedItems();

        if (personasSeleccionadas.isEmpty()) {
            mostrarAlertInfo(btnDeleteRows.getScene().getWindow(), bundle.getString("errorDeleteRows"));
            return;
        }

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
     * Restaura la tabla a su estado inicial con los datos de The Beatles
     */
    @FXML
    public void restoreRows() {
        logger.info("Iniciando restauración de datos básicos");
        int currentSize = allThePeople.size();

        btnRestoreRows.setDisable(true);
        gestionarProgreso(true);

        // Ejecutar restauración en background
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (SIMULAR_CARGA_LENTA) {
                    Thread.sleep(MILISEGUNDOS_SIMULACION);
                }
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
     * Carga los datos desde la base de datos de forma asíncrona
     */
    private void cargarDatos() {
        gestionarProgreso(true);

        Task<ObservableList<Person>> task = new Task<ObservableList<Person>>() {
            @Override
            protected ObservableList<Person> call() throws Exception {
                logger.info("Cargando personas desde BD");

                if (SIMULAR_CARGA_LENTA) {
                    Thread.sleep(MILISEGUNDOS_SIMULACION);
                }

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
     * Valida los campos del formulario
     * @return true si todos los campos son válidos, false en caso contrario
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
     * Limpia todos los campos del formulario
     */
    private void clearFields() {
        logger.debug("Limpiando campos del formulario");
        txtFirstName.clear();
        txtLastName.clear();
        dateBirth.setValue(null);
    }

    /**
     * Muestra un diálogo de error
     */
    private void mostrarAlertError(Window win, String mensaje) {
        logger.error(mensaje);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(win);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo informativo
     */
    private void mostrarAlertInfo(Window win, String mensaje) {
        logger.info(mensaje);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(win);
        alert.setHeaderText(null);
        alert.setTitle("Info");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Gestiona la visibilidad de la barra de progreso
     * @param mostrar true para mostrar, false para ocultar
     */
    private void gestionarProgreso(boolean mostrar) {
        Platform.runLater(() -> {
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            progressBar.setVisible(mostrar);
            if (!mostrar) {
                progressBar.progressProperty().unbind();
            }
        });
    }
}