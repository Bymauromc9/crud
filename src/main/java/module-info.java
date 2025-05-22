module crud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens crud to javafx.fxml;
    exports crud;
}
