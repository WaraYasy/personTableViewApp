package es.wara.dao;

import es.wara.database.ConectionDB;
import es.wara.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Person.
 * <p>
 * Maneja las operaciones Create, Read y Delete para la tabla 'personas'
 * en la base de datos MariaDB.
 * </p>
 * 
 * <h2>Operaciones soportadas:</h2>
 * <ul>
 *   <li><b>Create:</b> Inserción de nuevas personas ({@link #addPerson(Person)})</li>
 *   <li><b>Read:</b> Consulta y carga de todas las personas ({@link #fillTable()})</li>
 *   <li><b>Delete:</b> Eliminación de personas existentes ({@link #deletePerson(Person)})</li>
 *   <li><b>Restore:</b> Restauración de datos básicos ({@link #restoreBasicData()})</li>
 * </ul>
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
    private static final Logger loger = LoggerFactory.getLogger(DaoPerson.class);

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
    public static ObservableList<Person> fillTable(){
        ConectionDB connection;
        ObservableList<Person> lstPerson= FXCollections.observableArrayList();
        try {
            connection = new ConectionDB();
            String consulta = "SELECT personId,firstName,lastName,birthDate FROM personas";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("personId");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String birthDate = rs.getString("birthDate");
                // Manejar fecha null de forma segura
                LocalDate bdate = (birthDate != null && !birthDate.trim().isEmpty()) ? 
                                 LocalDate.parse(birthDate) : null;
                Person persona = new Person();
                persona.setPersonId(Integer.parseInt(id));
                persona.setFirstName(firstName);
                persona.setLastName(lastName);
                persona.setBirthDate(bdate);
                lstPerson.add(persona);
            }
            rs.close();
            pstmt.close();
            connection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
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
    public static boolean deletePerson(Person personToDelete) {
        ConectionDB connection;
        int filasEliminadas =0;
        try {
            connection = new ConectionDB();
            String sql ="DELETE FROM personas WHERE personId=? ";
            PreparedStatement pstmt =connection.getConnection().prepareStatement(sql);
            pstmt.setInt(1,personToDelete.getPersonId());
            filasEliminadas = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filasEliminadas >0;
    }

    /**
     * Inserta una nueva persona en la base de datos.
     * <p>
     * Añade un nuevo registro a la tabla 'personas' con los datos básicos proporcionados.
     * El ID de la persona será asignado automáticamente por la base de datos (AUTO_INCREMENT).
     * Se recomienda validar los datos antes de llamar a este método usando {@link Person#isValidPerson()}.
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
     * @see Person#isValidPerson()
     * @see ConectionDB#ConectionDB()
     */
    public static boolean addPerson(Person personaAdd){
        ConectionDB connection;
        int exito=0;
        try {
            connection = new ConectionDB();
            String consulta="INSERT INTO personas (firstName,lastName,birthDate) VALUES (?,?,?)";
            PreparedStatement pstmt;
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, personaAdd.getFirstName());
            pstmt.setString(2, personaAdd.getLastName());
            // Manejar fecha null de forma segura
            String birthDateStr = (personaAdd.getBirthDate() != null) ? 
                                  personaAdd.getBirthDate().toString() : null;
            pstmt.setString(3, birthDateStr);
            exito=pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
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
        ConectionDB connection = null;
        try {
            connection = new ConectionDB();
            
            // Operación en una sola transacción para robustez
            connection.getConnection().setAutoCommit(false);
            
            // Limpiar tabla y reiniciar auto_increment
            PreparedStatement deleteStmt = connection.getConnection().prepareStatement("DELETE FROM personas");
            deleteStmt.executeUpdate();
            deleteStmt.close();
            
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
            
            // Confirmar transacción
            connection.getConnection().commit();
            connection.closeConnection();
            
            return insertedRows == 4;
            
        } catch (SQLException e) {
            // Rollback en caso de error
            try {
                if (connection != null && connection.getConnection() != null) {
                    connection.getConnection().rollback();
                }
            } catch (SQLException rollbackEx) {
                // Log del rollback si falla
            }
            e.printStackTrace();
            if (connection != null) {
                connection.closeConnection();
            }
            return false;
        }
    }
}


