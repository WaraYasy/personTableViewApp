package es.wara.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de gestión de conexiones a la base de datos MariaDB.
 * <ul>
 *   <li>Establecimiento automático de conexión en la construcción del objeto</li>
 *   <li>Carga de configuración desde archivo properties</li>
 *   <li>Gestión segura del cierre de conexiones</li>
 *   <li>Logging completo de eventos y errores de conexión</li>
 * </ul>
 * 
 * <h2>Configuración requerida:</h2>
 * <p>El archivo {@code configuration.properties} debe contener las siguientes propiedades:</p>
 * <ul>
 *   <li>{@code host} - Dirección del servidor de base de datos</li>
 *   <li>{@code port} - Puerto de conexión</li>
 *   <li>{@code user} - Usuario</li>
 *   <li>{@code pass} - Contraseña</li>
 *   <li>{@code database} - Nombre de la base de datos a conectar</li>
 * </ul>
 * 
 * @author Wara Pacheco
 * @version 1.0
 * @since 2025-10-01
 */
public class ConectionDB {

    /** Conexión activa a la base de datos MariaDB. */
    private Connection conexionMDB = null;
    
    /**
     * Logger para registrar eventos, errores y mensajes de depuración durante el ciclo de vida de la aplicación.
     */
    private static final Logger loger = LoggerFactory.getLogger(ConectionDB.class);

    /**
     * Constructor que establece automáticamente la conexión a la base de datos.
     * <p>
     * Carga la configuración desde el archivo {@code configuration.properties},
     * construye la URL de conexión JDBC y establece la conexión con MariaDB.
     * </p>
     * 
     * @throws SQLException si ocurre un error durante el establecimiento de la conexión
     */
    public ConectionDB() throws SQLException{
        try {
            String host = Propiedades.getValor("host");
            String user = Propiedades.getValor("user");
            String pass = Propiedades.getValor("pass");
            String port = Propiedades.getValor("port");
            String database = Propiedades.getValor("database");
            // Construir URL de conexión JDBC
            String url = "jdbc:mariadb://" + host + ":" + port + "/" + database;
            conexionMDB = DriverManager.getConnection(url, user, pass);
            // Log de conexión exitosa
            loger.info("Conexión establecida con {}", database);

        } catch (SQLException e) {
            loger.error("Conexión a BD fallida: " + e.getMessage());
        }
    }

    /**
     * Obtiene la conexión activa a la base de datos.
     * <p>
     * Retorna la instancia de {@link Connection} establecida en el constructor.
     * Esta conexión puede ser utilizada para ejecutar consultas y transacciones.
     * </p>
     * 
     * @return la conexión activa a la base de datos, o {@code null} si la conexión falló
     * @see java.sql.Connection
     */
    public Connection getConnection() {
        return conexionMDB;
    }

    /**
     * Cierra la conexión a la base de datos de forma segura.
     * <p>
     * Verifica que la conexión esté activa antes de cerrarla y registra el evento.
     * Es importante llamar a este método cuando se termine de usar la conexión
     * para liberar recursos del sistema.
     * </p>
     *
     * @see java.sql.Connection#close()
     */
    public void closeConnection() {
        if (conexionMDB != null) {
            try {
                conexionMDB.close();
                loger.info("Conexión cerrada");
            } catch (SQLException e) {
                loger.error("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

}
