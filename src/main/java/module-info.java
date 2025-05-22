module crud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires com.google.gson;

    opens crud to javafx.fxml, com.google.gson;
    exports crud;
}
