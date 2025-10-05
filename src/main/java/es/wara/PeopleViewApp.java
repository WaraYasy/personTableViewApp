package es.wara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

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

        // Configurar idioma y resource bundle
        //Locale locale = Locale.forLanguageTag("en");
        Locale locale = Locale.forLanguageTag("es");

        loger.debug("Configurando idioma: {}", locale.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("es.wara.texts", locale);
        loger.debug("Resource bundle cargado para locale: {}", locale);


        // Cargar archivo FXML con la definición de la interfaz
        loger.debug("Cargando archivo FXML: fxml/tableView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(PeopleViewApp.class.getResource("fxml/tableView.fxml"),bundle);
        scene = new Scene(fxmlLoader.load());
        loger.info("Archivo FXML cargado exitosamente");

        // Configurar icono de la aplicación
        Image icon = new Image(
                PeopleViewApp.class.getResource("/es/wara/images/icon.png").toExternalForm()
        );
        stage.getIcons().add(icon);

        // Configurar título de la ventana
        stage.setTitle("Agenda Personal");

        // Cargar y aplicar estilos CSS
        loger.debug("Cargando hoja de estilos CSS..");
        String cssPath = getClass().getResource("/es/wara/css/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        loger.debug("Estilos CSS aplicados exitosamente");

        // Configurar escena y dimensiones en el escenario
        stage.setScene(scene);
        stage.setMinWidth(650);
        stage.setMinHeight(600);
        stage.setMaxWidth(1000);
        stage.setMaxHeight(900);

        // Mostrar la ventana principal
        loger.info("Mostrando ventana principal de la aplicación");
        stage.show();

        loger.info("Aplicación JavaFX iniciada correctamente");

    }

    /**
     * Función principal de la aplicación.
     * Lanza la aplicación de JavaFX.
     *
     * @param args Argumentos de la línea de comandos (No sse requieren).
     */
    public static void main(String[] args) {
        loger.info("=== INICIO DE PEOPLE VIEW APP ===");
        launch(args);
    }
}