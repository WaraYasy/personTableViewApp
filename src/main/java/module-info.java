/**
 * Módulo de aplicación JavaFX para gestión de personas en tabla.
 * 
 * @author Wara Pacheco
 * @version 1.1
 */
module es.wara {
    // Dependencias JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    
    // Logging
    requires org.slf4j;
    requires javafx.base;
    requires java.sql;


    // Permitir acceso por reflexión para JavaFX
    opens es.wara to javafx.fxml;
    opens es.wara.control to javafx.fxml;
    opens es.wara.model to javafx.base;
    
    // Paquetes públicos
    exports es.wara;
    exports es.wara.control;
    exports es.wara.model;
}
