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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class VistaPrincipalController {

    @FXML
    private BorderPane root;

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
    private Button buttonExportar;

    private CityDAO ciudadDAO = new CityDAO();
    private CountryDAO countryDAO = new CountryDAO();

    private ObservableList<City> paises = FXCollections.observableArrayList();

    /**
     * Inicializa la vista principal.(Configuracion de la tabla, cargar el combo, añade el listener para la tabla,
     * )
     */

    @FXML
    public void initialize() {
        configurarTabla();
        cargarComboBox();
        agregarListenerTabla();

        tableView.setItems(FXCollections.observableArrayList());

        comboBox.setOnAction(event -> {
            String paisSeleccionado = comboBox.getValue();
            if (paisSeleccionado != null && !paisSeleccionado.isEmpty())
                filtrarDatosPorPais(paisSeleccionado);
            else
                tableView.setItems(FXCollections.observableArrayList());
        });
    }

    /**
     * Hace la accion de actualizar una ciudad seleccionada. 
     * Valida que los campos esten correctos y actualiza la ciudad en la base de datos
     */
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
        if (result.isPresent() && result.get() == ButtonType.OK) {

            int poblacion;
            try {
                poblacion = Integer.parseInt(poblacionStr);
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "La población debe ser un número.");
                return;
            }

           try {
                City ciudad = new City(Integer.parseInt(id), nombre, distrito, poblacion);
                ciudadDAO.actualizarCiudad(ciudad);
                mostrarAlerta("Actualizada", "Ciudad actualizada correctamente.");
                filtrarDatosPorPais(comboBox.getValue());
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo actualizar la ciudad.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Hace la accion de borrar una ciudad seleccionada y la elimina de la base de datos 
     *      
     * */

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
        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                ciudadDAO.borrarCiudad(Integer.parseInt(id));
                mostrarAlerta("Borrado", "Ciudad borrada correctamente");
                filtrarDatosPorPais(comboBox.getValue());
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo borrar la ciudad.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Hace la accion de insertar una nueva ciudad. Valida los campos, comprueba si estan duplicados
     * y añade la ciudad a la base de datos
     */

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
        alert.setContentText("¿Seguro que quieres insertarlo?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            int poblacion;
            try {
                poblacion = Integer.parseInt(poblacionStr);
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "La población debe ser un número.");
                return;
            }

            String countryCode = null;
            try {
                countryCode = countryDAO.getCountryCode(paisSeleccionado);
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo obtener el código del país.");
                e.printStackTrace();
                return;
            }

            if (countryCode == null) {
                mostrarAlerta("Error", "No se encontró el código del país.");
                return;
            }

            try (Connection connection = DatabaseConnection.getConnection();
                java.sql.PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM city WHERE Name = ? AND District = ? AND CountryCode = ?")) {
                ps.setString(1, nombre);
                ps.setString(2, distrito);
                ps.setString(3, countryCode);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    mostrarAlerta("Ciudad existente", "Ya existe una ciudad con ese nombre, distrito y país.");
                    return;
                }
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo comprobar la existencia de la ciudad.");
                e.printStackTrace();
                return;
            }


            try {
                City ciudad = new City(0, nombre, distrito, poblacion);
                ciudadDAO.añadirCiudad(ciudad, countryCode);
                mostrarAlerta("Éxito", "Ciudad insertada correctamente.");
                filtrarDatosPorPais(comboBox.getValue());
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo insertar la ciudad.");
                e.printStackTrace();
        }
    }
}

    /**
     * Este metodo es un metodo auxiliar para mostrar alertas de tipo information
     * @param titulo
     * @param mensaje
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Añade un listener a la tabla para mostrar los atributos de las ciudades en los textfield
     */
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

    /**
     * Metodo para configurar las columnas de la tabla para mostrar las ciudades
     */

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDistrito.setCellValueFactory(new PropertyValueFactory<>("district"));
        colPoblacion.setCellValueFactory(new PropertyValueFactory<>("population"));
    }

    /**
     * Carga los datos de las ciudades en la tabla
     */

    @FXML
    private void cargarDatos() {
        paises.clear();
        try {
            List<Pais> ciudades = ciudadDAO.getAllCities();
            ciudades.addAll(ciudades);
            tableView.setItems(paises);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filtra y muestra las ciudades segun el pais seleccionado en el combo box
     * @param paisSeleccionado
     */

    private void filtrarDatosPorPais(String paisSeleccionado) {
        if (paisSeleccionado == null || paisSeleccionado.isEmpty()) {
            cargarDatos();
            return;
        }
        paises.clear();
        try {
            List<City> ciudades = ciudadDAO.getCiudadesByCountry(paisSeleccionado);
            paises.addAll(ciudades);
            tableView.setItems(paises);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cargar los paises en el combobox
     */

    private void cargarComboBox() {
        try {
            comboBox.getItems().setAll(countryDAO.getAllCountries());
            comboBox.setOnAction(e->filtrarDatosPorPais(comboBox.getValue()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cambia el tema de la aplicacion a claro
     */
    @FXML
    private void setTemaClaro() {
        Scene scene = root.getScene();
        scene.getRoot().getStyleClass().remove("dark-theme");
        System.out.println("Tema claro");
    }

    /**
     * Cambia el tema de la aplicacion a oscuro
     */
    @FXML
    private void setTemaOscuro() {
        Scene scene = root.getScene();
        if (!scene.getRoot().getStyleClass().contains("dark-theme")) {
            scene.getRoot().getStyleClass().add("dark-theme");
        }
        System.out.println("Tema oscuro");
    }

    /**
     * Exporta los datos de la tabla a un archivo JSON seleccionando la ruta por el usuario
     */

    @FXML
    private void exportarJson(){
        ObservableList<City> datos = tableView.getItems();
        if(datos == null|| datos.isEmpty()){
            mostrarAlerta("Exportar JSON", "No hay datos para exportar");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos JSON", "*.json"));
        fileChooser.setInitialFileName("ciudades_exportadas.json");

        File archivo = fileChooser.showSaveDialog(root.getScene().getWindow());
        if(archivo !=null){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(archivo)){
                gson.toJson(datos,writer);
                mostrarAlerta("Exportacion exitosa", "Datos exportados a: "+archivo.getAbsolutePath());
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo exportar el archivo: "+e.getMessage());
            } 
        }
    }

}
