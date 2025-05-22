package crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CountryDAO {

    /**
     * Obtiene todos los nombres de los paises de la base de datos, ordenados alfabeticamente
     * @return
     * @throws SQLException
     */
    public List<String> getAllCountries() throws SQLException {
        List<String> countries = new ArrayList<>();
        String query = "SELECT Name FROM country ORDER BY Name ASC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                countries.add(resultSet.getString("Name"));
            }
        }
        return countries;
    }

    /**
     * Obtiene el codigo de un pais dado su nombre
     * @param paisSeleccionado
     * @return
     * @throws SQLException
     */

    public String getCountryCode(String paisSeleccionado) throws SQLException {
        String query = "SELECT Code FROM country WHERE Name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, paisSeleccionado);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Code");
                }
            }
        }
    return null; 
}
}
