package es.wara.database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public abstract class Propiedades{
    private static Properties props = new Properties();
    /** Logger para registrar eventos y depuración del controlador. */
    private static final Logger loger = LoggerFactory.getLogger(Propiedades.class);


    static{
        try {
            // Usar getResourceAsStream que es más confiable para archivos en el classpath
            InputStream input = Propiedades.class.getClassLoader().getResourceAsStream("es/wara/configuration.properties");
            
            if (input != null) {
                try {
                    props.load(input);
                    loger.info("Archivo configuration.properties cargado correctamente desde el classpath");
                    loger.debug("Propiedades cargadas: {}", props.keySet());
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

    public static String getValor(String clave) {
        String valor = props.getProperty(clave);
        if(valor != null && !valor.trim().isEmpty()) {
            loger.debug("Clave '{}' encontrada con valor: '{}'", clave, valor);
            return valor.trim();
        }
        loger.error("La clave '{}' no está disponible en configuration.properties", clave);
        loger.error("Claves disponibles: {}", props.keySet());
        throw new RuntimeException("La clave '" + clave + "' solicitada en configuration.properties no está disponible");
    }
}