package es.wara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PeopleViewApp extends Application {

    /** Escena principal de la aplicación */
    private static Scene scene;

    /** Logger para registrar eventos y depuración de la aplicación */
    private static final Logger loger = LoggerFactory.getLogger(PeopleViewApp.class);

    /**
     * Inicializa la aplicación JavaFX.
     * <p>
     * Carga el archivo FXML, aplica la hoja de estilos CSS, configura la ventana
     * principal y muestra la escena.
     * </p>
     *
     * @param stage Escenario principal proporcionado por JavaFX
     * @throws IOException Si ocurre un error al cargar el FXML o CSS
     */
    @Override
    public void start(Stage stage) throws IOException {
        loger.debug("Buscando archivo FXML...");
        FXMLLoader fxmlLoader = new FXMLLoader(PeopleViewApp.class.getResource("fxml/tableView.fxml"));
        scene = new Scene(fxmlLoader.load());
        loger.debug("Archivo FXML cargado correctamente.");

        stage.setTitle("Lista de Contáctos");

        loger.debug("Buscando hoja de estilos CSS...");
        String cssPath = getClass().getResource("/es/wara/css/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        loger.debug("Hoja de estilos CSS cargada correctamente.");

        // Configuración de la ventana
        stage.setScene(scene);
        // Dimensiones mínimas de la ventana
        // Dimensiones máximas de la ventana


        loger.debug("Mostrando la ventana principal.");
        stage.show();
    }

    /**
     * Punto de entrada principal de la aplicación.
     * <p>
     * Invoca el método launch() de JavaFX para inicializar
     * el runtime y ejecutar el método {@link #start(Stage)}.
     * </p>
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        loger.info("INICIO DE TABLEVIEW APP.");
        launch();
    }

}
