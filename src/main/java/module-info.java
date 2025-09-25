module es.wara {
    requires javafx.controls;
    requires javafx.fxml;

    opens es.wara to javafx.fxml;
    exports es.wara;
}
