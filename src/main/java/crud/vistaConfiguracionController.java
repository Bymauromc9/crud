package crud;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class vistaConfiguracionController {
    @FXML
    private TextField textFieldURL;
    @FXML
    private TextField textFieldDatabase;
    @FXML
    private TextField textFieldUser;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button buttonConfirmar;
    @FXML
    Button buttonCancelar;

    @FXML
    public void initialize() {

    }

    public void setURL() {

    }

    public void setDatabase() {

    }

    public void setUser() {

    }

    public void setPassword() {

    }

    public void pulsarBotonConfirmar() {

    }

    public void pulsarBotonCancelar() {

    }

    public void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
