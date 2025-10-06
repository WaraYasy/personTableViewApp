package es.wara.database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Clase utilitaria para cargar y acceder a propiedades de configuración.
 * <p>
 * Carga automáticamente el archivo {@code configuration.properties} desde el classpath
 * y proporciona acceso a los valores de configuración de la base de datos.
 * </p>
 *
 * @author Wara
 * @version 1.0
 * @since 2025-10-02
 */
public abstract class Propiedades{
    
    /** Objeto Properties que contiene las propiedades cargadas del archivo de configuración. */
    private static Properties props = new Properties();
    
    /** Logger para registrar eventos y depuración de la carga de propiedades. */
    private static final Logger loger = LoggerFactory.getLogger(Propiedades.class);

    /*
     * Bloque estático que se ejecuta al cargar la clase.
     * <p>
     * Carga automáticamente el archivo {@code configuration.properties} desde el classpath
     * ubicado en {@code es/wara/configuration.properties}.
     * </p>
     */
    static{
        try {
            // Usar getResourceAsStream que es más confiable para archivos en el classpath
            InputStream input = Propiedades.class.getClassLoader().getResourceAsStream("es/wara/configuration.properties");
            
            if (input != null) {
                try {
                    props.load(input);
                    //loger.info("Archivo configuration.properties cargado correctamente desde el classpath");
                    //loger.debug("Propiedades cargadas: {}", props.keySet());
                } finally {
                    input.close();
                }
            } else {
                loger.error("No se pudo encontrar es/wara/configuration.properties en el classpath");
                // Intentar buscar con diferentes rutas
                loger.debug("Intentando buscar con ruta alternativa...");
                input = Propiedades.class.getResourceAsStream("/es/wara/configuration.properties");
                if (input != null) {
                    try {
                        props.load(input);
                        loger.info("Archivo configuration.properties cargado con ruta alternativa");
                    } finally {
                        input.close();
                    }
                }
            }
        } catch (Exception e) {
            loger.error("Error al cargar configuration.properties: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtiene el valor de una propiedad específica.
     *
     * @param clave La clave de la propiedad a buscar
     * @return El valor de la propiedad como String, sin espacios en blanco al inicio y final
     * @throws RuntimeException Si la clave no existe o está vacía
     */
    public static String getValor(String clave) {
        String valor = props.getProperty(clave);
        if(valor != null && !valor.trim().isEmpty()) {
            //loger.debug("Clave '{}' encontrada con valor: '{}'", clave, valor);
            return valor.trim();
        }
        loger.error("La clave '{}' no está disponible en configuration.properties", clave);
        //loger.error("Claves disponibles: {}", props.keySet());
        throw new RuntimeException("La clave '" + clave + "' solicitada en configuration.properties no está disponible");
    }
}