module es.wara {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;

    opens es.wara to javafx.fxml;
    opens es.wara.control to javafx.fxml;
    opens es.wara.model to javafx.base;
    exports es.wara;
    exports es.wara.control;
    exports es.wara.model;
}
