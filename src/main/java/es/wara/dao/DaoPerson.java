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

public class DaoPerson {

    /**
     * Logger para registrar eventos, errores y mensajes de depuración durante el ciclo de vida de la aplicación.
     */
    private static final Logger loger = LoggerFactory.getLogger(DaoPerson.class);


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


