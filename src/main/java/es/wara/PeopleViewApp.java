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
 * Aplicación principal JavaFX para gestión de personas.
 * <p>
 * Carga la interfaz FXML con soporte de internacionalización,
 * aplica estilos CSS y configura la ventana principal.
 * </p>
 *
 * @author Wara
 * @version 1.0
 * @since 2025-10-02
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
     * Inicializa y muestra la aplicación JavaFX.
     * <p>
     * Configura el idioma, carga la interfaz FXML con resource bundles,
     * aplica estilos CSS y muestra la ventana principal.
     * </p>
     *
     * @param stage Ventana principal de la aplicación
     * @throws IOException Si ocurre un error al cargar recursos FXML o CSS
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

        // Cargar interfaz FXML
        loger.debug("Cargando interfaz FXML...");
        FXMLLoader fxmlLoader = new FXMLLoader(PeopleViewApp.class.getResource("fxml/tableView.fxml"), bundle);
        scene = new Scene(fxmlLoader.load());
        loger.info("Interfaz FXML cargada exitosamente");

        // Configurar icono de la aplicación
        Image icon = new Image(
                PeopleViewApp.class.getResource("/es/wara/images/icon.png").toExternalForm()
        );
        stage.getIcons().add(icon);
 

        // Configurar ventana
        stage.setTitle(bundle.getString("title"));
        stage.setScene(scene);
        stage.setMinWidth(650);
        stage.setMinHeight(600);
        stage.setMaxWidth(1000);
        stage.setMaxHeight(900);
        loger.debug("Ventana configurada - Dimensiones: 650x600 - 900x900");

        // Aplicar estilos CSS
        String cssPath = getClass().getResource("/es/wara/css/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        loger.debug("Estilos CSS aplicados");

        // Mostrar aplicación
        stage.show();
        loger.info("=== PeopleViewApp iniciada correctamente ===");
    }

    /**
     * Punto de entrada principal de la aplicación.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        loger.info("=== INICIANDO PEOPLE VIEW APP ===");
        launch(args);
    }
}