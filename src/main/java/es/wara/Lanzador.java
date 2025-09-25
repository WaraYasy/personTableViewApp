package es.wara;

/**
 * Clase lanzadora principal de la aplicación JavaFX.
 * Esta clase actúa como punto de entrada alternativo que proporciona logging
 * antes de inicializar la aplicación JavaFX principal.
 *
 * <p>Se utiliza como wrapper para el lanzamiento de {@link PeopleViewApp},
 * permitiendo un mejor control del proceso de inicio y registro de eventos.</p>
 *
 * @author Wara Pacheco
 * @version 1.0
 * @since 2025-09-23
 * @see PeopleViewApp
 */
public class Lanzador {

    /**
     * Punto de entrada principal de la aplicación.
     * Registra el evento de lanzamiento y delega la ejecución a la clase
     * principal de JavaFX {@link PeopleViewApp}.
     *
     * <p>Este método actúa como un proxy que permite añadir logging y
     * potencialmente otras operaciones de inicialización antes de
     * lanzar la interfaz gráfica.</p>
     *
     * @param args Argumentos de línea de comandos que se pasan a la aplicación JavaFX
     */
    public static void main(String[] args) {
        PeopleViewApp.main(args);
    }
}