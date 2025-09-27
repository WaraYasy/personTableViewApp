package es.wara.control;

import es.wara.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Controlador para la gestión de personas en una interfaz JavaFX basada en TableView.
 * <p>
 * Esta clase gestiona la visualización y manipulación de una lista de personas, permitiendo
 * operaciones CRUD (alta, baja, restauración de datos y visualización) y una experiencia de usuario enriquecida
 * con validaciones, alertas y registro de eventos.
 * </p>
 * <h2>Funciones principales:</h2>
 * <ul>
 *   <li>Mostrar la lista de personas en una {@link TableView}</li>
 *   <li>Agregar nuevas personas mediante formulario</li>
 *   <li>Eliminar personas seleccionadas</li>
 *   <li>Restaurar el estado original de la tabla</li>
 *   <li>Mostrar mensajes de error e información mediante cuadros de diálogo</li>
 * </ul>
 *
 * @author Wara Pacheco
 * @version 1.2
 * @since 2025-09-25
 */
public class TableViewController {

    // ===== CONTROLES DE LA INTERFAZ =========

    /** Botón para agregar una nueva persona a la tabla. */
    @FXML
    private Button btnAdd;

    /** Botón para eliminar las filas seleccionadas de la tabla. */
    @FXML
    private Button btnDeleteRows;

    /** Botón para restaurar la tabla a su estado inicial. */
    @FXML
    private Button btnRestoreRows;

    /** Campo de selección de fecha de nacimiento. */
    @FXML
    private DatePicker dateBirth;

    /** Tabla principal que muestra la lista de personas. */
    @FXML
    private TableView<Person> tablePerson;

    /** Campo de texto para ingresar el nombre. */
    @FXML
    private TextField txtFirstName;

    /** Campo de texto para ingresar el apellido. */
    @FXML
    private TextField txtLastName;

    // ===== COLUMNAS DE LA TABLA =====

    /** Columna que muestra el ID único de la persona. */
    @FXML
    private TableColumn<Person, Integer> colPersonId;

    /** Columna que muestra el nombre de la persona. */
    @FXML
    private TableColumn<Person, String> colFirstName;

    /** Columna que muestra el apellido de la persona. */
    @FXML
    private TableColumn<Person, String> colLastName;

    /** Columna que muestra la fecha de nacimiento de la persona. */
    @FXML
    private TableColumn<Person, LocalDate> colBirthDate;

    /** Logger para registrar eventos y depuración del controlador. */
    private static final Logger loger = LoggerFactory.getLogger(TableViewController.class);

    /**
     * Inicializa el controlador y configura la tabla de personas tras cargar el archivo FXML.
     * <p>
     * Realiza las siguientes acciones:
     * <ul>
     *   <li>Carga la lista inicial de personas en la tabla</li>
     *   <li>Configura el modo de selección múltiple</li>
     *   <li>Establece las fábricas de valores para cada columna</li>
     * </ul>
     * <b>Nota:</b> Lanza una excepción si ocurre un error durante la inicialización.
     */
    @FXML
    public void initialize() {
        // Cargar lista inicial de personas en la tabla
        ObservableList<Person> initialList = getPersonList();
        tablePerson.setItems(initialList);
        loger.debug("Lista inicial cargada con {} personas", initialList.size());

        // Configurar selección múltiple
        TableView.TableViewSelectionModel<Person> tsm = tablePerson.getSelectionModel();
        tsm.setSelectionMode(SelectionMode.MULTIPLE);

        // Configurar las columnas para mostrar las propiedades de Person
        colPersonId.setCellValueFactory(new PropertyValueFactory<>("personId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

    }

    /**
     * Agrega una nueva persona a la tabla utilizando los datos ingresados en el formulario.
     * <p>
     * Realiza validación de campos obligatorios, crea una nueva instancia de {@link Person}
     * y la añade a la tabla si los datos son válidos. Muestra una alerta de error si faltan datos
     * y limpia los campos tras una inserción exitosa.
     * </p>
     * @see Person#Person(String, String, LocalDate)
     * @see #clearFields()
     */
    @FXML
    public void addPerson() {
        try {
            // Obtener valores de los campos de entrada
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();
            LocalDate birthDate = dateBirth.getValue();

            // Validación básica de campos obligatorios
            if (firstName == null || firstName.trim().isEmpty()) {
                loger.error("Intento de agregar persona con nombre vacío");
                mostrarAlertError(btnAdd.getScene().getWindow(), "Debes introducir el nombre de la persona.");
                return;
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                loger.error("Intento de agregar persona con apellido vacío");
                mostrarAlertError(btnAdd.getScene().getWindow(), "Debes introducir los apellidos de la persona.");
                return;
            }

            // Crear nueva persona y agregar a la tabla
            Person newPerson = new Person(firstName.trim(), lastName.trim(), birthDate);
            tablePerson.getItems().add(newPerson);

            loger.info("Persona agregada exitosamente: {}", newPerson);

            // Limpiar campos para la siguiente entrada
            clearFields();
        } catch (Exception e) {
            loger.error("Error al agregar nueva persona: {}", e.getMessage(), e);
        }
    }

    /**
     * Elimina las filas seleccionadas de la tabla de personas.
     * <p>
     * Verifica que exista al menos una selección, elimina las personas seleccionadas en orden inverso
     * para mantener la integridad de los índices, y muestra una alerta informativa tras la operación.
     * Registra todas las operaciones para auditoría.
     * </p>
     * <b>Nota:</b> La eliminación se realiza en orden inverso.
     */
    @FXML
    public void deleteSelectedRows() {
        TableView.TableViewSelectionModel<Person> tsm = tablePerson.getSelectionModel();

        // Validar que hay filas seleccionadas
        if (tsm.isEmpty()) {
            loger.info("Intento de eliminar filas sin tener ninguna seleccionada");
            mostrarAlertInfo(btnDeleteRows.getScene().getWindow(), "No hay celdas seleccionadas.");
            return;
        }

        try {
            // Obtener índices seleccionados y ordenarlos
            ObservableList<Integer> selectedIndicesList = tsm.getSelectedIndices();
            Integer[] selectedIndices = selectedIndicesList.toArray(new Integer[0]);
            Arrays.sort(selectedIndices);

            loger.info("Eliminando {} filas seleccionadas: {}", selectedIndices.length, Arrays.toString(selectedIndices));

            // Eliminar en orden inverso para preservar índices
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                int index = selectedIndices[i];
                Person personToRemove = tablePerson.getItems().get(index);
                tsm.clearSelection(index);
                tablePerson.getItems().remove(index);
                loger.debug("Persona eliminada: {}", personToRemove);
            }

            String mensaje = "Eliminación exitosa.\n" + selectedIndices.length + " persona(s) eliminada(s).\nTotal restante: " + tablePerson.getItems().size();
            loger.info(mensaje);
            mostrarAlertInfo(btnDeleteRows.getScene().getWindow(), mensaje);

        } catch (Exception e) {
            loger.error("Error durante la eliminación de filas: {}", e.getMessage(), e);
        }
    }

    /**
     * Restaura la tabla de personas a su estado inicial.
     * <p>
     * Limpia completamente la tabla actual y recarga la lista original de personas.
     * Actualiza la interfaz y muestra una alerta informativa con el resultado.
     * <b>Advertencia:</b> Esta operación no se puede deshacer.
     * </p>
     */
    @FXML
    public void restoreRows() {
        try {
            loger.info("Iniciando restauración de la tabla a su estado original");

            // Guardar estadísticas antes de la restauración
            int currentSize = tablePerson.getItems().size();

            // Limpiar tabla actual y recargar datos originales
            tablePerson.getItems().clear();
            ObservableList<Person> listaOriginal = getPersonList();
            tablePerson.getItems().addAll(listaOriginal);

            String mensaje = "Tabla restaurada exitosamente.\nAnteriormente " + currentSize + " personas. Ahora " +
                    listaOriginal.size() + " personas.";
            loger.info(mensaje);
            mostrarAlertInfo(btnRestoreRows.getScene().getWindow(), mensaje);

        } catch (Exception e) {
            loger.error("Error durante la restauración de la tabla: {}", e.getMessage(), e);
        }
    }

    /**
     * Limpia todos los campos de entrada del formulario.
     * <p>
     * Borra los valores introducidos en los campos de nombre, apellido y fecha de nacimiento,
     * preparando el formulario para una nueva entrada.
     * <br>
     * Es llamado automáticamente después de agregar una persona exitosamente.
     * </p>
     * @see #addPerson()
     */
    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        dateBirth.setValue(null);
        loger.debug("Campos del formulario limpiados");
    }

    /**
     * Devuelve una lista inicial de personas para poblar la tabla.
     * <p>
     * Esta lista es estática y de ejemplo, con datos predefinidos.
     * </p>
     * @return {@link ObservableList} con instancias de {@link Person}
     */
    private static ObservableList<Person> getPersonList() {
        Person john  = new Person("John",  "Lennon",   LocalDate.of(1940, 10, 9));
        Person paul  = new Person("Paul",  "McCartney",LocalDate.of(1942, 6, 18));
        Person george= new Person("George","Harrison", LocalDate.of(1943, 2, 25));
        Person ringo = new Person("Ringo", "Starr",    LocalDate.of(1940, 7, 7));
        return FXCollections.observableArrayList(john, paul, george, ringo);
    }

    /**
     * Muestra un cuadro de diálogo de error con el mensaje especificado.
     *
     * @param win     Ventana propietaria del diálogo (usualmente la principal)
     * @param mensaje Texto a mostrar en el cuadro de diálogo
     */

    private void mostrarAlertError(Window win, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(win);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un cuadro de diálogo informativo con el mensaje especificado.
     *
     * @param win    Ventana propietaria del diálogo (usualmente la principal)
     * @param mensaje Texto a mostrar en el cuadro de diálogo
     */
    private void mostrarAlertInfo(Window win, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(win);
        alert.setHeaderText(null);
        alert.setTitle("Info");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}