package es.wara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX.
 * <p>
 * Se encarga de cargar la interfaz desde un archivo FXML, aplicar estilos CSS y mostrar la ventana principal.
 * </p>
 *
 * @author Wara
 * @version 1.0
 * @since 2025-09-25
 * @see javafx.application.Application
 * @see es.wara.control.TableViewController
 * @see es.wara.model.Person
 */
public class PeopleViewApp extends Application {

    /**
     * Escena principal de la aplicación JavaFX.
     */
    private static Scene scene;

    /**
     * Logger para registrar eventos, errores y mensajes de depuración durante el ciclo de vida de la aplicación.
     */
    private static final Logger loger = LoggerFactory.getLogger(PeopleViewApp.class);

     /**
     * Este método se ejecuta al arrancar la aplicación.
     * Carga la interfaz (FXML), aplica el CSS y muestra la ventana.
     *
     * @param stage Ventana principal de la aplicación.
     * @throws IOException Si hay un problema al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        loger.info("Iniciando aplicación JavaFX - PeopleViewApp");

        // Cargar archivo FXML con la definición de la interfaz
        loger.debug("Cargando archivo FXML: fxml/tableView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(PeopleViewApp.class.getResource("fxml/tableView.fxml"));
        scene = new Scene(fxmlLoader.load());
        loger.info("Archivo FXML cargado exitosamente");

        // Configurar título de la ventana
        stage.setTitle("Agenda Personal");

        // Cargar y aplicar estilos CSS
        loger.debug("Cargando hoja de estilos CSS..");
        String cssPath = getClass().getResource("/es/wara/css/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        loger.debug("Estilos CSS aplicados exitosamente");

        // Configurar escena y dimensiones en el escenario
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(600);
        stage.setMaxWidth(900);
        stage.setMaxHeight(900);

        // Mostrar la ventana principal
        loger.info("Mostrando ventana principal de la aplicación");
        stage.show();

        loger.info("Aplicación JavaFX iniciada correctamente");

    }

    /**
     * Método principal de la aplicación.
     * Lanza la aplicaación de JavaFX.
     *
     * @param args Argumentos de la línea de comandos (No sse requieren).
     */
    public static void main(String[] args) {
        loger.info("=== INICIO DE PEOPLE VIEW APP ===");
        launch(args);
    }
}