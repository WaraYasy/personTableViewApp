package es.wara.dao;

import es.wara.database.ConectionDB;
import es.wara.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Person.
 * <p>
 * Maneja las operaciones Create, Read y Delete para la tabla 'personas'
 * en la base de datos MariaDB.
 * </p>
 * 
 * @author Wara Pacheco
 * @version 1.0
 * @since 2025-10-01
 * @see Person
 * @see ConectionDB
 */
public class DaoPerson {

    /**
     * Logger para registrar eventos, errores y mensajes de depuración durante el ciclo de vida de la aplicación.
     */
    private static final Logger logger = LoggerFactory.getLogger(DaoPerson.class);

    /*----------------------------------------------*/
    /* .             Métodos Síncronos             .*/
    /*----------------------------------------------*/

    /**
     * Carga todas las personas desde la base de datos y las retorna en una lista observable.
     * <p>
     * Este método ejecuta una consulta SELECT para obtener todos los registros de la tabla 'personas'
     * y los convierte en objetos {@link Person}. La lista resultante es compatible con JavaFX
     * para binding automático con controles de interfaz como {@link javafx.scene.control.TableView}.
     * </p>
     *
     * 
     * @return {@link ObservableList} conteniendo todas las personas de la base de datos.
     *         Lista vacía si no hay registros o si ocurre un error.
     * 
     * @see Person#Person()
     * @see ConectionDB#ConectionDB()
     */
    public static ObservableList<Person> fillTableSync() {
        logger.debug("Cargando personas desde la base de datos");
        ObservableList<Person> lstPerson = FXCollections.observableArrayList();
        ConectionDB connection = null;

        try {
            connection = new ConectionDB();
            String consulta = "SELECT personId, firstName, lastName, birthDate FROM personas";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Person persona = new Person();
                persona.setPersonId(rs.getInt("personId"));
                persona.setFirstName(rs.getString("firstName"));
                persona.setLastName(rs.getString("lastName"));
                persona.setBirthDate(parseBirthDate(rs.getString("birthDate")));
                lstPerson.add(persona);
            }

            rs.close();
            pstmt.close();
            logger.debug("Cargadas {} personas", lstPerson.size());

        } catch (SQLException e) {
            logger.error("Error al cargar personas: {}", e.getMessage(), e);
        } finally {
            //Cerrar la conexión en caso de éxito o error
            if (connection != null) {
                connection.closeConnection();
            }
        }

        return lstPerson;
    }

    /**
     * Elimina una persona específica de la base de datos.
     * <p>
     * Ejecuta una operación DELETE utilizando el ID único de la persona como criterio.
     * </p>
     *
     * @param personToDelete La persona a eliminar. Debe tener un ID válido.
     * @return {@code true} si la persona fue eliminada exitosamente (1 fila afectada),
     *         {@code false} si no se encontró la persona o si ocurrió un error.
     * 
     * @see Person#getPersonId()
     * @see ConectionDB#ConectionDB()
     */
    public static boolean deletePersonSync(Person personToDelete) {
        logger.debug("Eliminando persona: {}", personToDelete.toString());
        ConectionDB connection = null;
        int filasEliminadas = 0;

        try {
            connection = new ConectionDB();
            String sql ="DELETE FROM personas WHERE personId=? ";
            PreparedStatement pstmt =connection.getConnection().prepareStatement(sql);
            pstmt.setInt(1,personToDelete.getPersonId());

            filasEliminadas = pstmt.executeUpdate();
            pstmt.close();
            
            if (filasEliminadas > 0) {
                logger.info("Persona eliminada exitosamente. {}", personToDelete.toString());
            } else {
                logger.warn("No se encontró ninguna persona con ID: {} para eliminar", personToDelete.getPersonId());
            }
        } catch (SQLException e) {
            logger.error("Error al eliminar persona con ID {}: {}", personToDelete.getPersonId(), e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.closeConnection();
            }
        }

        return filasEliminadas > 0;
    }

    /**
     * Inserta una nueva persona en la base de datos.
     * <p>
     * Añade un nuevo registro a la tabla 'personas' con los datos básicos proporcionados.
     * El ID de la persona será asignado automáticamente por la base de datos (AUTO_INCREMENT).
     * Se recomienda validar los datos antes de llamar a este método usando {@link Person#isValidPerson}.
     * </p>
     *
     * <h3>Campos insertados:</h3>
     * <ul>
     *   <li><b>firstName:</b> Nombre de la persona</li>
     *   <li><b>lastName:</b> Apellido de la persona</li>
     *   <li><b>birthDate:</b> Fecha de nacimiento (puede ser null)</li>
     * </ul>
     *
     * @param personaAdd La persona a insertar. Debe contener al menos firstName y lastName.
     * @return {@code true} si la persona fue insertada exitosamente (1 fila afectada),
     *         {@code false} si no se pudo insertar o si ocurrió un error.
     * 
     * @see Person#isValidPerson
     * @see ConectionDB#ConectionDB()
     */
    public static boolean addPersonSync(Person personaAdd){
        logger.debug("Iniciando inserción de nueva persona: {}", personaAdd.toString());
        ConectionDB connection = null;
        int exito=0;

        try {
            connection = new ConectionDB();
            String consulta="INSERT INTO personas (firstName,lastName,birthDate) VALUES (?,?,?)";

            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, personaAdd.getFirstName());
            pstmt.setString(2, personaAdd.getLastName());
            pstmt.setString(3, formatBirthDate(personaAdd.getBirthDate()));

            exito=pstmt.executeUpdate();
            pstmt.close();
            
            if (exito > 0) {
                logger.info("Persona añadida exitosamente: {}", personaAdd.toString());
            }
        } catch (SQLException e) {
            logger.error("Error al añadir persona {}: {}",
                       personaAdd.toString(), e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.closeConnection();
            }
        }
        return exito>0;
    }

    /**
     * Restaura los datos básicos originales de la tabla personas.
     * Elimina todos los datos actuales e inserta los 4 registros básicos de The Beatles.
     * 
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public static boolean restoreBasicData() {
        logger.info("Iniciando restauración de datos básicos de The Beatles");
        ConectionDB connection = null;
        try {
            connection = new ConectionDB();
            
            // Operación en una sola transacción para robustez
            connection.getConnection().setAutoCommit(false);
            logger.debug("Iniciando transacción para restauración de datos");
            
            // Limpiar tabla y reiniciar auto_increment
            PreparedStatement deleteStmt = connection.getConnection().prepareStatement("DELETE FROM personas");
            int deletedRows = deleteStmt.executeUpdate();
            deleteStmt.close();
            logger.debug("Eliminadas {} filas existentes", deletedRows);
            
            PreparedStatement resetStmt = connection.getConnection().prepareStatement("ALTER TABLE personas AUTO_INCREMENT = 1");
            resetStmt.executeUpdate();
            resetStmt.close();
            
            // Insertar datos básicos de forma eficiente
            String insertQuery = "INSERT INTO personas (firstName, lastName, birthDate) VALUES " +
                                "('John', 'Lennon', '1940-10-09'), " +
                                "('Paul', 'McCartney', '1942-06-18'), " +
                                "('George', 'Harrison', '1943-02-25'), " +
                                "('Ringo', 'Starr', '1940-07-07')";
            
            PreparedStatement insertStmt = connection.getConnection().prepareStatement(insertQuery);
            int insertedRows = insertStmt.executeUpdate();
            insertStmt.close();
            logger.debug("Insertados {} registros de The Beatles", insertedRows);
            
            // Confirmar transacción
            connection.getConnection().commit();
            
            logger.info("Restauración completada exitosamente. {} registros de The Beatles insertados", insertedRows);
            return insertedRows == 4;

        } catch (SQLException e) {
            logger.error("Error en restauración: {}", e.getMessage(), e);

            // Rollback
            if (connection != null) {
                try {
                    connection.getConnection().rollback();
                    logger.debug("Rollback ejecutado");
                } catch (SQLException rollbackEx) {
                    logger.error("Error en rollback: {}", rollbackEx.getMessage(), rollbackEx);
                }
            }
            return false;

        } finally {
            if (connection != null) {
                connection.closeConnection();
            }
        }
    }
    /*----------------------------------------------*/
    /*             Métodos auxiliares               */
    /*----------------------------------------------*/

    /**
     * Convierte String a LocalDate de forma segura
     */
    private static LocalDate parseBirthDate(String dateStr) {
        return (dateStr != null && !dateStr.trim().isEmpty())
                ? LocalDate.parse(dateStr)
                : null;
    }

    /**
     * Convierte LocalDate a String de forma segura
     */
    private static String formatBirthDate(LocalDate date) {
        return (date != null) ? date.toString() : null;
    }
}


