package es.wara.control;

import es.wara.dao.DaoPerson;
import es.wara.model.Person;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controlador JavaFX para gestión de personas con soporte multilingüe.
 * <p>
 * Maneja operaciones CRUD (Create, Read, Delete) y restauración de datos
 * en una interfaz basada en TableView con internacionalización.
 * </p>
 *
 * <h2>Funcionalidades principales:</h2>
 * <ul>
 *   <li>Añadir nuevas personas con validación</li>
 *   <li>Eliminar personas seleccionadas</li>
 *   <li>Restaurar datos básicos (The Beatles)</li>
 *   <li>Soporte multilingüe con ResourceBundle</li>
 * </ul>
 *
 * @author Wara Pacheco
 * @version 2.0
 * @since 2025-10-02
 * @see Person
 * @see DaoPerson
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

    /** Lista observable con todas las personas de la base de datos. */
    private ObservableList<Person> allThePeople;

    /** Resource bundle para internacionalización. */
    private ResourceBundle bundle;

    /** Logger para eventos y depuración. */
    private static final Logger loger = LoggerFactory.getLogger(TableViewController.class);

    /**
     * Inicializa el controlador después de cargar el FXML.
     * <p>
     * Configura las columnas de la tabla, carga los datos iniciales
     * y establece el resource bundle para internacionalización.
     * </p>
     *
     * @see DaoPerson#fillTable()
     * @see javafx.scene.control.TableView.TableViewSelectionModel
     * @see javafx.scene.control.cell.PropertyValueFactory
     */
    @FXML
    public void initialize() {
        // Configurar internacionalización
        Locale locale = Locale.forLanguageTag("es");
        bundle = ResourceBundle.getBundle("es.wara.texts", locale);
        loger.debug("Resource bundle configurado para locale: {}", locale);

        // Cargar datos iniciales
        allThePeople = DaoPerson.fillTable();
        tablePerson.setItems(allThePeople);
        loger.debug("Lista inicial cargada con {} personas", allThePeople.size());

        // Configurar selección múltiple
        TableView.TableViewSelectionModel<Person> tsm = tablePerson.getSelectionModel();
        tsm.setSelectionMode(SelectionMode.MULTIPLE);
        loger.debug("Modo de selección múltiple configurado");

        // Configurar columnas de la tabla
        colPersonId.setCellValueFactory(new PropertyValueFactory<>("personId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        loger.debug("Columnas de tabla configuradas exitosamente");
    }

    /**
     * Añade una nueva persona a la tabla con validación de datos.
     * <p>
     * Valida los campos obligatorios, verifica la fecha de nacimiento
     * y persiste la información en la base de datos.
     * </p>
     */
    @FXML
    public void addPerson() {
        try {
            loger.info("Iniciando proceso de agregar nueva persona");
            
            // Obtener valores de los campos
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();  
            LocalDate birthDate = dateBirth.getValue();

            // Validaciones básicas
            if (firstName == null || firstName.trim().isEmpty()) {
                mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorMissingFirstName"));
                return;
            }
            if (lastName == null || lastName.trim().isEmpty()) {
                mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorMissingLastName"));
                return;
            }
            
            // Crear y validar nueva persona
            Person newPerson = new Person(firstName.trim(), lastName.trim(), birthDate);
            if(newPerson.isValidBirthDate(birthDate)){
                boolean success = DaoPerson.addPerson(newPerson);
                if (success) {
                    allThePeople = DaoPerson.fillTable();
                    tablePerson.setItems(allThePeople);
                    String mensaje = bundle.getString("successAddPerson");
                    mostrarAlertInfo(btnAdd.getScene().getWindow(), "Persona agregada correctamente.");
                } else {
                    mostrarAlertError(btnAdd.getScene().getWindow(), bundle.getString("errorAddPerson"));
                }
            }else{
                String mensaje = bundle.getString("errorBirthDate")+birthDate;
                mostrarAlertError(btnAdd.getScene().getWindow(), mensaje);
            }

            clearFields();
        } catch (Exception e) {
            loger.error("Error al agregar nueva persona: {}", e.getMessage(), e);
        }
    }

    /**
     * Elimina las filas seleccionadas de la tabla de personas.
     * <p>
     * Este método maneja la eliminación múltiple de personas tanto de la interfaz como de la base de datos.
     * Implementa una estrategia de eliminación segura procesando los elementos en orden inverso
     * para evitar problemas con los índices cambiantes.
     * </p>
     *
     * 
     * @see DaoPerson#deletePerson(Person)
     * @see javafx.scene.control.TableView.TableViewSelectionModel
     * @see #mostrarAlertInfo(Window, String)
     */
    @FXML
    public void deleteSelectedRows() {
        TableView.TableViewSelectionModel<Person> tsm = tablePerson.getSelectionModel();

        loger.info("Iniciando eliminación de filas seleccionadas");
        
        // Validar selección
        if (tsm.isEmpty()) {
            mostrarAlertInfo(btnDeleteRows.getScene().getWindow(), bundle.getString("errorDeleteRows"));
            return;
        }

        try {
            // Obtener índices seleccionados
            ObservableList<Integer> selectedIndicesList = tsm.getSelectedIndices();
            Integer[] selectedIndices = selectedIndicesList.toArray(new Integer[0]);
            Arrays.sort(selectedIndices);
            loger.info("Eliminando {} personas seleccionadas", selectedIndices.length);

            // Eliminar en orden inverso para preservar índices
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                int index = selectedIndices[i];
                //obtener persona a eliminar
                Person personToRemove = tablePerson.getItems().get(index);
                // Eliminar de la base de datos
                boolean success = DaoPerson.deletePerson(personToRemove);
                // Si se eliminó correctamente, actualizar la tabla
                if (success) {
                    // Limpiar selección y eliminar de la vista
                    tsm.clearSelection(index);
                    tablePerson.getItems().remove(index);
                    loger.debug("Persona eliminada: {}", personToRemove);
                } else {
                    loger.error("Error al eliminar persona: {}", personToRemove);
                }
            }

            String mensaje = String.format(bundle.getString("deleteSuccessMessage"),
                    selectedIndices.length,
                    tablePerson.getItems().size());
            mostrarAlertInfo(btnDeleteRows.getScene().getWindow(), mensaje);

        } catch (Exception e) {
            loger.error("Error durante la eliminación de filas: {}", e.getMessage(), e);
        }
    }

    /**
     * Restaura la tabla de personas a su estado inicial con los datos básicos de The Beatles.
     * <p>
     * Esta operación destructiva elimina completamente todos los datos actuales de la base de datos
     * y los reemplaza con los 4 registros originales predefinidos. Es útil para demostración
     * y testing, pero debe usarse con precaución en entornos de producción.
     * </p>
     * 
     * 
     * @see DaoPerson#restoreBasicData()
     * @see #mostrarAlertInfo(Window, String)
     * @see #mostrarAlertError(Window, String)
     */
    @FXML
    public void restoreRows() {
        try {
            loger.info("Iniciando restauración de la tabla a los datos básicos originales");

            // Guardar estadísticas antes de la restauración
            int currentSize = tablePerson.getItems().size();

            // Restaurar datos básicos en la base de datos
            boolean success = DaoPerson.restoreBasicData();
            
            if (success) {
                // Recargar datos desde la base de datos
                allThePeople = DaoPerson.fillTable();
                tablePerson.setItems(allThePeople);

                String mensaje = bundle.getString("restoreSuccessTitle") + "\n" +
                        String.format(bundle.getString("restoreSuccessPrevious"), currentSize) + "\n" +
                        String.format(bundle.getString("restoreSuccessNow"), allThePeople.size()) + "\n" +
                        bundle.getString("restoreSuccessData");
                mostrarAlertInfo(btnRestoreRows.getScene().getWindow(), mensaje);
            } else {
                mostrarAlertError(btnRestoreRows.getScene().getWindow(), bundle.getString("errorRestore"));
            }

        } catch (Exception e) {
            String mensaje = bundle.getString("errorRestore2")+e.getMessage();
            mostrarAlertError(btnRestoreRows.getScene().getWindow(), mensaje);
        }
    }

    /**
     * Limpia todos los campos de entrada del formulario de captura de datos.
     * <p>
     * Restablece todos los controles del formulario a su estado inicial vacío,
     * preparando la interfaz para una nueva captura de datos de persona.
     * </p>
     * 
     * 
     * @see #addPerson()
     * @see javafx.scene.control.TextField#clear()
     * @see javafx.scene.control.DatePicker#
     */
    private void clearFields() {
        loger.debug("Limpiando campos del formulario");
        txtFirstName.clear();
        txtLastName.clear();
        dateBirth.setValue(null);
        loger.debug("Campos del formulario limpiados");
    }


    /**
     * Muestra un cuadro de diálogo de error con el mensaje especificado.
     * <p>
     * Crea y muestra una alerta modal de tipo ERROR para informar al usuario.
     * El diálogo bloquea la interacción hasta que el usuario lo cierre.
     * </p>
     * 
     * @param win     Ventana propietaria del diálogo (usualmente la ventana principal)
     * @param mensaje Texto descriptivo del error a mostrar al usuario
     * 
     * @see javafx.scene.control.Alert
     * @see javafx.scene.control.Alert.AlertType#ERROR
     */

    private void mostrarAlertError(Window win, String mensaje) {
        loger.error(mensaje);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(win);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un cuadro de diálogo informativo con el mensaje especificado.
     * <p>
     * Crea y muestra una alerta modal de tipo INFORMACIÓN para comunicar al usuario
     * el resultado exitoso de operaciones o información relevante.
     * </p>
     * 
     * @param win Ventana propietaria
     * @param mensaje Mensaje informativo
     */
    private void mostrarAlertInfo(Window win, String mensaje) {
        loger.info(mensaje);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(win);
        alert.setHeaderText(null);
        alert.setTitle("Info");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}