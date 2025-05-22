package crud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("vistaPrincipal"), 640, 480);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("util/icono.jpg")));
        scene.getStylesheets().add(getClass().getResource("util/crud.css").toExternalForm());
        stage.setMinWidth(700);
        stage.setMinHeight(400);
        stage.show();
        stage.setOnCloseRequest(evento -> {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Salida");
            alerta.setHeaderText("¿Seguro que quieres salir de la aplicación?");
            Optional<ButtonType> botonCancelar = alerta.showAndWait();
            if (botonCancelar.isPresent() && botonCancelar.get() == ButtonType.CANCEL) {
                evento.consume();
            }
        });
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}