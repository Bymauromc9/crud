package crud;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javafx.scene.control.Alert;

public class DatabaseConnection {

    private static Connection connection;


    /**
     * Obtiene la conexion a la base de datos, si no existe
     * o esta cerrada, la crea
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try (FileInputStream fis = new FileInputStream("src/main/resources/crud/db.properties")) {
                Properties properties = new Properties();
                properties.load(fis);

                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");

                connection = DriverManager.getConnection(url, user, password);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error de conexión");
                alert.setHeaderText("No se pudo conectar a la base de datos");
                alert.setContentText("Detalles del error: " + e.getMessage());
                alert.showAndWait();
            }
        }
        return connection;
    }
}