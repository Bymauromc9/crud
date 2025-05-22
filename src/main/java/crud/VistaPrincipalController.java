package crud;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class VistaPrincipalController {

    @FXML private BorderPane root;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TableView<City> tableView;

    @FXML
    private TableColumn<Pais, Integer> colId;

    @FXML
    private TableColumn<Pais, String> colNombre;

    @FXML
    private TableColumn<Pais, String> colDistrito;

    @FXML
    private TableColumn<Pais, Integer> colPoblacion;
    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldNombre;

    @FXML
    private TextField textFieldDistrito;

    @FXML
    private TextField textFieldPoblacion;

    @FXML
    private TextField textFieldBuscar;

    @FXML
    private Button buttonConfigurar;

    private CityDAO ciudadDAO = new CityDAO();
    private CountryDAO countryDAO=new CountryDAO();

    private ObservableList<City> paises = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarComboBox();
        agregarListenerTabla();


        tableView.setItems(FXCollections.observableArrayList());

        comboBox.setOnAction(event->{
            String paisSeleccionado = comboBox.getValue();
            if(paisSeleccionado!=null && !paisSeleccionado.isEmpty())
                filtrarDatosPorPais(paisSeleccionado);
            else
                tableView.setItems(FXCollections.observableArrayList());
        });
    }

    @FXML
    private void handleActualizar() {
    // Obtener los valores de los campos de texto
    String id = textFieldId.getText();
    String nombre = textFieldNombre.getText();
    String distrito = textFieldDistrito.getText();
    String poblacionStr = textFieldPoblacion.getText();

    if (id.isEmpty() || nombre.isEmpty() || distrito.isEmpty() || poblacionStr.isEmpty()) {
        mostrarAlerta("Error", "Todos los campos deben estar completos.");
        return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmar actualización");
    alert.setContentText("¿Seguro que quieres actualizarlo?");
    Optional<ButtonType> result = alert.showAndWait();
    if(result.isPresent()&& result.get()==ButtonType.OK){

    int poblacion;
    try {
        poblacion = Integer.parseInt(poblacionStr);
    } catch (NumberFormatException e) {
        mostrarAlerta("Error", "La población debe ser un número.");
        return;
    }

    // Actualizar en la base de datos
    String sql = "UPDATE city SET Name = '" + nombre + "', District = '" + distrito + "', Population = " + poblacion + " WHERE ID = " + id;

    try (Connection connection = DatabaseConnection.getConnection();
         Statement statement = connection.createStatement()) {
        int filasActualizadas = statement.executeUpdate(sql);
        if (filasActualizadas > 0) {
            mostrarAlerta("Actualizada", "Ciudad actualizada correctamente.");
            filtrarDatosPorPais(comboBox.getValue());
        } else {
            mostrarAlerta("Actualizada", "No se encontro la ciudada con ese ID");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}

    @FXML
private void handleBorrar() {
    String id = textFieldId.getText();
    if (id.isEmpty()) {
        mostrarAlerta("Error", "Debes seleccionar una ciudad para borrar.");
        return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmar borrado");
    alert.setContentText("¿Seguro que quieres borar?");
    Optional<ButtonType> result = alert.showAndWait();
    if(result.isPresent()&& result.get()==ButtonType.OK){

    String sql = "DELETE FROM city WHERE ID = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, Integer.parseInt(id));
        int filasBorradas = statement.executeUpdate();
        if (filasBorradas > 0) {
            mostrarAlerta("Borrado", "Ciudad borrada correctamente");
            filtrarDatosPorPais(comboBox.getValue());
        } else {
            mostrarAlerta("Error", "No se encontró la ciudad con ese ID.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}

@FXML
private void handleInsertar() {
    String nombre = textFieldNombre.getText();
    String distrito = textFieldDistrito.getText();
    String poblacionStr = textFieldPoblacion.getText();
    String paisSeleccionado = comboBox.getValue();

    if (nombre.isEmpty() || distrito.isEmpty() || poblacionStr.isEmpty() || paisSeleccionado == null) {
        mostrarAlerta("Error", "Todos los campos deben estar completos y un país seleccionado.");
        return;
    }
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmar insertar");
    alert.setContentText("¿Seguro que quieres instentarlo?");
    Optional<ButtonType> result = alert.showAndWait();
    if(result.isPresent()&& result.get()==ButtonType.OK){

    int poblacion;
    try {
        poblacion = Integer.parseInt(poblacionStr);
    } catch (NumberFormatException e) {
        mostrarAlerta("Error", "La población debe ser un número.");
        return;
    }

    // Obtener el código del país
    String countryCode = null;
    String sqlCountry = "SELECT Code FROM country WHERE Name = ?";
    try (Connection connection = DatabaseConnection.getConnection();
         java.sql.PreparedStatement psCountry = connection.prepareStatement(sqlCountry)) {
        psCountry.setString(1, paisSeleccionado);
        ResultSet rs = psCountry.executeQuery();
        if (rs.next()) {
            countryCode = rs.getString("Code");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return;
    }

    if (countryCode == null) {
        mostrarAlerta("Error", "No se encontró el código del país.");
        return;
    }

    // Comprobar si ya existe la ciudad
    String sqlCheck = "SELECT * FROM city WHERE Name = ? AND District = ? AND CountryCode = ?";
    try (Connection connection = DatabaseConnection.getConnection();
        java.sql.PreparedStatement psCheck = connection.prepareStatement(sqlCheck)) {
        psCheck.setString(1, nombre);
        psCheck.setString(2, distrito);
        psCheck.setString(3, countryCode);
        ResultSet rsCheck = psCheck.executeQuery();
        if (rsCheck.next()) {
            mostrarAlerta("Ciudad existente", "Ya existe una ciudad con ese nombre, distrito y país.");
            return;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return;
    }

    // Insertar la ciudad
    String sql = "INSERT INTO city (Name, CountryCode, District, Population) VALUES (?, ?, ?, ?)";
    try (Connection connection = DatabaseConnection.getConnection();
        java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, nombre);
        statement.setString(2, countryCode);
        statement.setString(3, distrito);
        statement.setInt(4, poblacion);
        int filasInsertadas = statement.executeUpdate();
        if (filasInsertadas > 0) {
            mostrarAlerta("Éxito", "Ciudad insertada correctamente.");
            filtrarDatosPorPais(comboBox.getValue());
        } else {
            mostrarAlerta("Error", "No se pudo insertar la ciudad.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}

// Método auxiliar para mostrar alertas
private void mostrarAlerta(String titulo, String mensaje) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(titulo);
    alert.setHeaderText(null);
    alert.setContentText(mensaje);
    alert.showAndWait();
}

    

    private void agregarListenerTabla() {
    tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
            textFieldId.setText(String.valueOf(newValue.getId()));
            textFieldNombre.setText(newValue.getName());
            textFieldDistrito.setText(newValue.getDistrict());
            textFieldPoblacion.setText(String.valueOf(newValue.getPopulation()));
        }
    });
}

    private void configurarTabla() {
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
    colDistrito.setCellValueFactory(new PropertyValueFactory<>("district"));
    colPoblacion.setCellValueFactory(new PropertyValueFactory<>("population"));
}

        @FXML
    private void cargarDatos() {
        paises.clear();
        try {
            List<Pais> ciudades = ciudadDAO.getAllCities();
            ciudades.addAll(ciudades);
            tableView.setItems(paises);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void filtrarDatosPorPais(String paisSeleccionado) {
    if (paisSeleccionado == null || paisSeleccionado.isEmpty()) {
            cargarDatos();
            return;
        }
        paises.clear();
        try {
            List<City> ciudades = ciudadDAO.getCitiesByCountry(paisSeleccionado);
            paises.addAll(ciudades);
            tableView.setItems(paises);
        } catch (SQLException e) {
            e.printStackTrace();
        }
}



    private void cargarComboBox() {
        try (Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            // Selecciona los nombres de los países y ordénalos alfabéticamente
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT Name AS nombre FROM country ORDER BY Name ASC")) {

            while (resultSet.next()) {
                comboBox.getItems().add(resultSet.getString("nombre"));
            }

            // Configura un listener para manejar la selección del ComboBox
            comboBox.setOnAction(event -> filtrarDatosPorPais(comboBox.getValue()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void setTemaClaro() {
        Scene scene = root.getScene();
        scene.getRoot().getStyleClass().remove("dark-theme");
        System.out.println("Tema claro");
    }

    @FXML
    private void setTemaOscuro() {
        Scene scene = root.getScene();
        if (!scene.getRoot().getStyleClass().contains("dark-theme")) {
            scene.getRoot().getStyleClass().add("dark-theme");
        }
        System.out.println("Tema oscuro");
    }
}
