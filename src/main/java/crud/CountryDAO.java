package crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CountryDAO {

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
}
