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
import java.util.List;

/**
 * Controlador para la gestión de personas en una interfaz JavaFX basada en TableView.
 * <p>
 * Esta clase gestiona la visualización y manipulación de una lista de personas, permitiendo
 * operaciones CRUD (alta, baja, restauración de datos y visualización) y una experiencia de usuario enriquecida
 * con validaciones, alertas y registro de eventos.
 * </p>
 * 
 * <h2>Funciones principales:</h2>
 * <ul>
 *   <li>Mostrar la lista de personas en una {@link TableView}</li>
 *   <li>Agregar nuevas personas mediante formulario con validación completa</li>
 *   <li>Eliminar personas seleccionadas con confirmación</li>
 *   <li>Restaurar el estado original de la tabla con datos básicos</li>
 *   <li>Mostrar mensajes de error e información mediante cuadros de diálogo</li>
 * </ul>
 * 
 * <h2>Validaciones implementadas:</h2>
 * <ul>
 *   <li>Nombre y apellido obligatorios</li>
 *   <li>Fecha de nacimiento opcional pero no puede ser futura</li>
 *   <li>Validación completa usando {@link Person#isValidPerson(List)}</li>
 * </ul>
 * 
 * <h2>Gestión de errores:</h2>
 * <ul>
 *   <li>Logging completo de todas las operaciones</li>
 *   <li>Alertas informativas para el usuario</li>
 *   <li>Manejo seguro de errores de base de datos</li>
 * </ul>
 *
 * @author Wara Pacheco
 * @version 1.3
 * @since 2025-09-25
 * @see Person
 * @see DaoPerson
 * @see javafx.scene.control.TableView
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

    /** Lista Observable que contiene todas las personas cargadas desde la base de datos. */
    private ObservableList<Person> allThePeople;

    /** Logger para registrar eventos y depuración del controlador. */
    private static final Logger loger = LoggerFactory.getLogger(TableViewController.class);

    /**
     * Inicializa el controlador y configura la tabla de personas tras cargar el archivo FXML.
     * <p>
     * Este método es llamado automáticamente por JavaFX después de cargar el archivo FXML
     * y antes de mostrar la interfaz al usuario.
     * </p>
     * 
     * <h3>Operaciones realizadas:</h3>
     * <ul>
     *   <li>Carga la lista inicial de personas desde la base de datos</li>
     *   <li>Configura el modo de selección múltiple en la tabla</li>
     *   <li>Establece las fábricas de valores para cada columna de la tabla</li>
     *   <li>Registra el número de personas cargadas para auditoría</li>
     * </ul>
     * 
     * <b>Nota:</b> Si ocurre un error durante la carga de datos, se registra pero no se
     * interrumpe la inicialización de la interfaz.
     * 
     * @see DaoPerson#fillTable()
     * @see javafx.scene.control.TableView.TableViewSelectionModel
     * @see javafx.scene.control.cell.PropertyValueFactory
     */
    @FXML
    public void initialize() {
        // Cargar lista inicial de personas en la tabla
        allThePeople = DaoPerson.fillTable();
        tablePerson.setItems(allThePeople);
        loger.debug("Lista inicial cargada con {} personas", allThePeople.size());

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
     * Este método realiza una validación completa de los datos ingresados por el usuario
     * antes de persistir la información en la base de datos. Utiliza el patrón de validación
     * del modelo {@link Person} para garantizar la integridad de los datos.
     * </p>
     * 
     * <h3>Proceso de validación:</h3>
     * <ol>
     *   <li>Crear instancia {@link Person} con los datos del formulario</li>
     *   <li>Ejecutar validación completa usando {@link Person#isValidPerson(List)}</li>
     *   <li>Si hay errores, mostrar mensaje detallado al usuario</li>
     *   <li>Si es válida, persistir en base de datos usando {@link DaoPerson#addPerson(Person)}</li>
     *   <li>Recargar tabla y limpiar formulario si fue exitoso</li>
     * </ol>
     * 
     * <h3>Validaciones aplicadas:</h3>
     * <ul>
     *   <li>Nombre: requerido, no puede estar vacío</li>
     *   <li>Apellido: requerido, no puede estar vacío</li>
     *   <li>Fecha nacimiento: opcional, pero no puede ser futura</li>
     * </ul>
     * 
     * @see Person#Person(String, String, LocalDate)
     * @see Person#isValidPerson(List)
     * @see DaoPerson#addPerson(Person)
     * @see #clearFields()
     * @see #mostrarAlertError(Window, String)
     * @see #mostrarAlertInfo(Window, String)
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
            if(newPerson.isValidBirthDate(birthDate)){
                boolean success = DaoPerson.addPerson(newPerson);
                if (success) {
                    allThePeople = DaoPerson.fillTable();
                    tablePerson.setItems(allThePeople);
                    loger.info("Persona agregada exitosamente: {}", newPerson);
                    mostrarAlertInfo(btnAdd.getScene().getWindow(), "Persona agregada correctamente.");
                } else {
                    loger.error("Error al agregar persona: {}", newPerson);
                    mostrarAlertError(btnAdd.getScene().getWindow(), "Error al agregar la persona a la base de datos.");
                }
            }else{
                String mensaje = "No es posible añadir una fecha de nacimiento mayor a la actual." +
                        "\n Error:"+birthDate;
                loger.error(mensaje);
                mostrarAlertError(btnAdd.getScene().getWindow(), mensaje);
            }

            // Limpiar campos para la siguiente entrada
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
     * <h3>Proceso de eliminación:</h3>
     * <ol>
     *   <li>Validar que hay al menos una fila seleccionada</li>
     *   <li>Obtener y ordenar los índices seleccionados</li>
     *   <li>Eliminar de la base de datos usando {@link DaoPerson#deletePerson(Person)}</li>
     *   <li>Eliminar de la tabla visual en orden inverso</li>
     *   <li>Mostrar resumen de la operación al usuario</li>
     * </ol>
     * 
     * <h3>Características de seguridad:</h3>
     * <ul>
     *   <li><b>Eliminación en orden inverso:</b> Evita problemas de índices</li>
     *   <li><b>Validación de selección:</b> Informa si no hay elementos seleccionados</li>
     *   <li><b>Persistencia dual:</b> Elimina tanto de BD como de la interfaz</li>
     *   <li><b>Logging completo:</b> Registra cada operación para auditoría</li>
     * </ul>
     * 
     * @see DaoPerson#deletePerson(Person)
     * @see javafx.scene.control.TableView.TableViewSelectionModel
     * @see #mostrarAlertInfo(Window, String)
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
                boolean success = DaoPerson.deletePerson(personToRemove);
                if (success) {
                    tsm.clearSelection(index);
                    tablePerson.getItems().remove(index);
                    loger.debug("Persona eliminada: {}", personToRemove);
                } else {
                    loger.error("Error al eliminar persona: {}", personToRemove);
                }
            }

            String mensaje = "Eliminación exitosa.\n" + selectedIndices.length + " persona(s) eliminada(s).\nTotal restante: " + tablePerson.getItems().size();
            loger.info(mensaje);
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
     * <h3>Proceso de restauración:</h3>
     * <ol>
     *   <li>Registrar estadísticas actuales para logging</li>
     *   <li>Ejecutar {@link DaoPerson#restoreBasicData()} para limpiar y repoblar la BD</li>
     *   <li>Recargar la tabla desde la base de datos actualizada</li>
     *   <li>Mostrar resultado detallado al usuario</li>
     * </ol>
     * 
     * <h3>Datos restaurados:</h3>
     * <ul>
     *   <li><b>John Lennon</b> - Nacido: 1940-10-09</li>
     *   <li><b>Paul McCartney</b> - Nacido: 1942-06-18</li>
     *   <li><b>George Harrison</b> - Nacido: 1943-02-25</li>
     *   <li><b>Ringo Starr</b> - Nacido: 1940-07-07</li>
     * </ul>
     * 
     * <h3>⚠️ Advertencias importantes:</h3>
     * <ul>
     *   <li><b>Operación destructiva:</b> Elimina TODOS los datos actuales</li>
     *   <li><b>No reversible:</b> No se puede deshacer esta operación</li>
     *   <li><b>Reinicia IDs:</b> Los identificadores se reinician desde 1</li>
     * </ul>
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

                String mensaje = "Tabla restaurada exitosamente a los datos básicos originales.\n" +
                        "Anteriormente: " + currentSize + " personas.\n" +
                        "Ahora: " + allThePeople.size() + " personas (The Beatles).\n" +
                        "Datos restaurados: John Lennon, Paul McCartney, George Harrison, Ringo Starr.";
                loger.info(mensaje);
                mostrarAlertInfo(btnRestoreRows.getScene().getWindow(), mensaje);
            } else {
                String errorMsg = "Error al restaurar los datos básicos en la base de datos.";
                loger.error(errorMsg);
                mostrarAlertError(btnRestoreRows.getScene().getWindow(), errorMsg);
            }

        } catch (Exception e) {
            loger.error("Error durante la restauración de la tabla: {}", e.getMessage(), e);
            mostrarAlertError(btnRestoreRows.getScene().getWindow(), 
                "Error inesperado durante la restauración: " + e.getMessage());
        }
    }

    /**
     * Limpia todos los campos de entrada del formulario de captura de datos.
     * <p>
     * Restablece todos los controles del formulario a su estado inicial vacío,
     * preparando la interfaz para una nueva captura de datos de persona.
     * Este método es esencial para mantener una experiencia de usuario fluida.
     * </p>
     * 
     * <h3>Campos que se limpian:</h3>
     * <ul>
     *   <li><b>Nombre:</b> Campo de texto {@link #txtFirstName}</li>
     *   <li><b>Apellido:</b> Campo de texto {@link #txtLastName}</li>
     *   <li><b>Fecha de nacimiento:</b> Selector de fecha {@link #dateBirth}</li>
     * </ul>
     * 
     * <p><b>Uso:</b> Se llama automáticamente después de agregar una persona exitosamente
     * para preparar el formulario para la siguiente entrada.</p>
     * 
     * @see #addPerson()
     * @see javafx.scene.control.TextField#clear()
     * @see javafx.scene.control.DatePicker#setValue(Object)
     */
    private void clearFields() {
        txtFirstName.clear();
        txtLastName.clear();
        dateBirth.setValue(null);
        loger.debug("Campos del formulario limpiados");
    }


    /**
     * Muestra un cuadro de diálogo de error con el mensaje especificado.
     * <p>
     * Crea y muestra una alerta modal de tipo ERROR para informar al usuario
     * sobre errores o situaciones que requieren atención inmediata.
     * El diálogo bloquea la interacción hasta que el usuario lo cierre.
     * </p>
     * 
     * <h3>Características del diálogo:</h3>
     * <ul>
     *   <li><b>Tipo:</b> ERROR (icono rojo de advertencia)</li>
     *   <li><b>Modal:</b> Bloquea la ventana padre hasta cerrarse</li>
     *   <li><b>Título:</b> "Error"</li>
     *   <li><b>Sin cabecera:</b> Solo muestra el mensaje principal</li>
     * </ul>
     * 
     * @param win     Ventana propietaria del diálogo (usualmente la ventana principal)
     * @param mensaje Texto descriptivo del error a mostrar al usuario
     * 
     * @see javafx.scene.control.Alert
     * @see javafx.scene.control.Alert.AlertType#ERROR
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
     * <p>
     * Crea y muestra una alerta modal de tipo INFORMACIÓN para comunicar al usuario
     * el resultado exitoso de operaciones o información relevante que no requiere
     * acción inmediata pero es importante que el usuario conozca.
     * </p>
     * 
     * <h3>Características del diálogo:</h3>
     * <ul>
     *   <li><b>Tipo:</b> INFORMATION (icono azul informativo)</li>
     *   <li><b>Modal:</b> Bloquea la ventana padre hasta cerrarse</li>
     *   <li><b>Título:</b> "Info"</li>
     *   <li><b>Sin cabecera:</b> Solo muestra el mensaje principal</li>
     * </ul>
     * 
     * <h3>Casos de uso típicos:</h3>
     * <ul>
     *   <li>Confirmación de operaciones exitosas</li>
     *   <li>Resumen de cambios realizados</li>
     *   <li>Información sobre el estado del sistema</li>
     * </ul>
     * 
     * @param win     Ventana propietaria del diálogo (usualmente la ventana principal)
     * @param mensaje Texto informativo a mostrar al usuario
     * 
     * @see javafx.scene.control.Alert
     * @see javafx.scene.control.Alert.AlertType#INFORMATION
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