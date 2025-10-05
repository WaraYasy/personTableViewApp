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
 * Data Access Object (DAO) para la entidad Person - Implementación del patrón DAO.
 * <p>
 * Esta clase encapsula todo el acceso a la capa de persistencia para la entidad {@code Person},
 * proporcionando una interfaz limpia entre la lógica de negocio y la base de datos MariaDB.
 * Implementa operaciones CRUD (Create, Read, Delete) de forma síncrona con manejo robusto
 * de errores y logging detallado.
 * </p>
 * 
 * <h2>Operaciones disponibles:</h2>
 * <ul>
 *   <li><b>CREATE:</b> {@link #addPersonSync(Person)} - Inserta nuevas personas</li>
 *   <li><b>READ:</b> {@link #fillTableSync()} - Consulta todas las personas</li>
 *   <li><b>DELETE:</b> {@link #deletePersonSync(Person)} - Elimina personas específicas</li>
 *   <li><b>RESTORE:</b> {@link #restoreBasicData()} - Restaura datos iniciales</li>
 * </ul>
 * 
 * <h2>Características técnicas:</h2>
 * <ul>
 *   <li>Gestión automática de conexiones y recursos</li>
 *   <li>Prepared Statements para prevenir inyección SQL</li>
 *   <li>Transacciones para operaciones críticas</li>
 *   <li>Logging completo de operaciones y errores</li>
 *   <li>Conversión segura entre tipos de datos Java y SQL</li>
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
    private static final Logger logger = LoggerFactory.getLogger(DaoPerson.class);

    /*----------------------------------------------*/
    /* .             Métodos Síncronos             .*/
    /*----------------------------------------------*/

    /**
     * Recupera todas las personas almacenadas en la base de datos.
     * <p>
     * Ejecuta una consulta SELECT completa sobre la tabla 'personas' y mapea cada registro
     * a un objeto {@link Person}. La lista resultante es {@link ObservableList} para
     * compatibilidad directa con controles JavaFX como TableView, ListView, etc.
     * </p>
     *
     * 
     * @return {@link ObservableList} conteniendo todas las personas de la base de datos.
     *         Lista vacía si no hay registros o si ocurre un error.
     * 
     * @see Person#Person()
     * @see ConectionDB#ConectionDB()
     * @see #parseBirthDate(String)
     */
    public static ObservableList<Person> fillTableSync() {
        logger.debug("Iniciando carga de personas desde la base de datos");
        // Crear lista observable vacía - compatible con JavaFX
        ObservableList<Person> lstPerson = FXCollections.observableArrayList();
        ConectionDB connection = null;

        try {
            // Establecer conexión usando nuestra clase wrapper
            connection = new ConectionDB();
            
            // Consulta SQL: seleccionar todos los campos de la tabla personas
            String consulta = "SELECT personId, firstName, lastName, birthDate FROM personas";
            
            // Preparar statement para evitar inyección SQL
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            
            // Ejecutar consulta y obtener resultados
            ResultSet rs = pstmt.executeQuery();
            logger.debug("Consulta ejecutada, procesando resultados...");

            // Iterar sobre cada fila del resultado
            while (rs.next()) {
                // Crear nueva instancia de Person para cada registro
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
     * Elimina una persona específica de la base de datos usando su ID único.
     * <p>
     * Realiza una operación DELETE segura utilizando PreparedStatement con el ID
     * de la persona como criterio único de eliminación. La operación es atómica
     * y retorna información precisa sobre el éxito de la eliminación.
     * </p>
     *
     * @param personToDelete La persona a eliminar. Debe tener un ID válido (> 0).
     * @return {@code true} si se eliminó exactamente una persona,
     *         {@code false} si no se encontró la persona o ocurrió un error.
     * 
     * @throws IllegalArgumentException si personToDelete es null o tiene ID inválido
     * @see Person#getPersonId()
     * @see ConectionDB#ConectionDB()
     */
    public static boolean deletePersonSync(Person personToDelete) {
        logger.debug("Iniciando eliminación de persona: {}", personToDelete.toString());
        ConectionDB connection = null;
        int filasEliminadas = 0;  // Contador de registros afectados

        try {
            // Establecer conexión a la base de datos
            connection = new ConectionDB();
            
            // SQL DELETE con parámetro para prevenir inyección SQL
            String sql = "DELETE FROM personas WHERE personId = ?";
            
            // Preparar statement con el parámetro
            PreparedStatement pstmt = connection.getConnection().prepareStatement(sql);
            pstmt.setInt(1, personToDelete.getPersonId());  // Asignar ID como parámetro seguro
            
            logger.debug("Ejecutando DELETE para persona con ID: {}", personToDelete.getPersonId());
            
            // Ejecutar eliminación y capturar número de filas afectadas
            filasEliminadas = pstmt.executeUpdate();
            
            // Liberar resources del statement
            pstmt.close();
            
            // Evaluar resultado de la operación
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
     * Inserta una nueva persona en la tabla de base de datos con generación automática de ID.
     * <p>
     * Realiza una inserción segura usando PreparedStatement para prevenir inyección SQL.
     * El ID se genera automáticamente (AUTO_INCREMENT) por lo que no debe especificarse.
     * Maneja la conversión de tipos Java a SQL de forma transparente.
     * </p>
     *
     * <h3>Campos insertados:</h3>
     * <ul>
     *   <li><b>personId:</b> Auto-generado (AUTO_INCREMENT) - no incluir</li>
     *   <li><b>firstName:</b> Nombre de la persona (requerido)</li>
     *   <li><b>lastName:</b> Apellido de la persona (requerido)</li>
     *   <li><b>birthDate:</b> Fecha de nacimiento en formato ISO (opcional)</li>
     * </ul>
     * 
     * <h3>Validaciones recomendadas:</h3>
     * Antes de llamar este método, considere validar usando {@link Person#isValidPerson()}
     * para asegurar que los datos cumplan las reglas de negocio.
     *
     * @param personaAdd La persona a insertar. Debe contener firstName y lastName válidos.
     *                   birthDate es opcional pero debe ser una fecha válida si se proporciona.
     * @return {@code true} si la inserción fue exitosa (exactamente 1 fila insertada),
     *         {@code false} si falló la inserción o ocurrió un error de base de datos.
     * 
     * @throws IllegalArgumentException si personaAdd es null
     * @see Person#isValidPerson()
     * @see ConectionDB#ConectionDB()
     * @see #formatBirthDate(LocalDate)
     */
    public static boolean addPersonSync(Person personaAdd){
        logger.debug("Iniciando inserción de nueva persona: {}", personaAdd.toString());
        ConectionDB connection = null;
        int filasInsertadas = 0;  // Contador de registros insertados

        try {
            // Establecer conexión a la base de datos
            connection = new ConectionDB();
            
            // SQL INSERT con placeholders (?) para parámetros seguros
            String consulta = "INSERT INTO personas (firstName, lastName, birthDate) VALUES (?, ?, ?)";

            // Preparar statement con los parámetros
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            
            // Asignar valores a los parámetros de forma segura
            pstmt.setString(1, personaAdd.getFirstName());                    // Parámetro 1: firstName
            pstmt.setString(2, personaAdd.getLastName());                     // Parámetro 2: lastName  
            pstmt.setString(3, formatBirthDate(personaAdd.getBirthDate()));   // Parámetro 3: birthDate convertida
            
            logger.debug("Ejecutando INSERT para: {} {}", personaAdd.getFirstName(), personaAdd.getLastName());

            // Ejecutar inserción y obtener número de filas afectadas
            filasInsertadas = pstmt.executeUpdate();
            
            // Liberar recursos del statement
            pstmt.close();
            
            // Evaluar resultado de la inserción
            if (filasInsertadas > 0) {
                logger.info("✓ Inserción exitosa - Nueva persona creada: {}", personaAdd.toString());
            } else {
                logger.warn("⚠ Inserción falló - No se crearon registros para: {}", personaAdd.toString());
            }
        } catch (SQLException e) {
            logger.error("💥 Error SQL al insertar persona {}: {}", 
                       personaAdd.toString(), e.getMessage(), e);
        } finally {
            // Garantizar cierre de conexión en todos los casos
            if (connection != null) {
                connection.closeConnection();
                logger.debug("🔌 Conexión cerrada");
            }
        }
        
        // Retornar true solo si se insertó exactamente 1 registro
        return filasInsertadas > 0;
    }

    /**
     * Restaura completamente la tabla personas a su estado inicial con datos de demostración.
     * <p>
     * Esta operación crítica realiza una limpieza completa y reinserción de datos de referencia.
     * Utiliza transacciones para garantizar consistencia: o se ejecuta completamente o no se
     * aplica ningún cambio (atomicidad). Los datos restaurados son los miembros de The Beatles.
     * </p>
     * 
     * <h3>Proceso de restauración (Transaccional):</h3>
     * <ol>
     *   <li><b>Iniciar transacción:</b> setAutoCommit(false)</li>
     *   <li><b>Limpiar datos:</b> DELETE FROM personas (elimina TODOS los registros)</li>
     *   <li><b>Reiniciar secuencia:</b> ALTER TABLE AUTO_INCREMENT = 1</li>
     *   <li><b>Insertar datos base:</b> INSERT múltiple con datos de The Beatles</li>
     *   <li><b>Confirmar cambios:</b> commit() - hace permanentes los cambios</li>
     *   <li><b>En caso de error:</b> rollback() - deshace todos los cambios</li>
     * </ol>
     * 
     * <h3>Datos restaurados (The Beatles):</h3>
     * <ul>
     *   <li>John Lennon (1940-10-09)</li>
     *   <li>Paul McCartney (1942-06-18)</li>
     *   <li>George Harrison (1943-02-25)</li>
     *   <li>Ringo Starr (1940-07-07)</li>
     * </ul>
     * 
     * <h3>⚠ ADVERTENCIA:</h3>
     * Esta operación es <b>DESTRUCTIVA</b>. Elimina TODOS los datos existentes sin posibilidad
     * de recuperación. Usar solo para resetear la aplicación a estado demo.
     * 
     * @return {@code true} si se restauraron exactamente 4 registros exitosamente,
     *         {@code false} si falló la operación (se ejecutó rollback automáticamente)
     * 
     * @see ConectionDB#ConectionDB()
     */
    public static boolean restoreBasicData() {
        logger.info("Iniciando restauración de datos básicos de The Beatles");
        ConectionDB connection = null;
        try {
            // Establecer conexión para operación transaccional
            connection = new ConectionDB();
            
            // Operación en una sola transacción para robustez
            connection.getConnection().setAutoCommit(false);
            logger.debug("Iniciando transacción para restauración de datos");
            
            // Limpiar tabla y reiniciar auto_increment
            PreparedStatement deleteStmt = connection.getConnection().prepareStatement("DELETE FROM personas");
            int deletedRows = deleteStmt.executeUpdate();
            deleteStmt.close();
            logger.debug("Eliminadas {} filas existentes", deletedRows);
            
            //Reiniciar contador AUTO_INCREMENT para IDs consecutivos desde 1
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
     * Convierte String de base de datos a LocalDate de Java de forma segura.
     * <p>
     * Maneja casos null, vacíos y formatos inválidos sin lanzar excepciones.
     * Utilizado al recuperar fechas desde ResultSet donde pueden ser NULL.
     * </p>
     * 
     * @param dateStr fecha en formato ISO (YYYY-MM-DD) o null
     * @return LocalDate correspondiente o null si la entrada es inválida
     */
    private static LocalDate parseBirthDate(String dateStr) {
        // Verificar que el string no sea null ni esté vacío
        return (dateStr != null && !dateStr.trim().isEmpty())
                ? LocalDate.parse(dateStr)  // Parsear formato ISO estándar
                : null;                     // Retornar null para valores inválidos
    }

    /**
     * Convierte LocalDate de Java a String para almacenamiento en base de datos.
     * <p>
     * Maneja valores null de forma segura, convirtiendo a formato ISO estándar.
     * Utilizado al insertar/actualizar fechas en PreparedStatement.
     * </p>
     * 
     * @param date fecha LocalDate o null
     * @return String en formato ISO (YYYY-MM-DD) o null si la fecha es null
     */
    private static String formatBirthDate(LocalDate date) {
        // Convertir a string ISO o mantener null
        return (date != null) ? date.toString()  // Formato ISO: YYYY-MM-DD
                             : null;             // Mantener null para BD
    }
}


