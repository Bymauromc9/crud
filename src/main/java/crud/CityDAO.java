package crud;


import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CityDAO {

    public List<City> getCitiesByCountry(String countryName) throws SQLException {
        List<City> cities = new ArrayList<>();
        String query = "SELECT city.ID, city.Name, city.District, city.Population " +
                       "FROM city " +
                       "JOIN country ON city.CountryCode = country.Code " +
                       "WHERE country.Name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, countryName);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                cities.add(new City(
                        resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("District"),
                        resultSet.getInt("Population")
                ));
            }
        }
        return cities;
    }
    public List<Pais> getAllCities() throws SQLException {
        List<Pais> cities = new ArrayList<>();
        String query = "SELECT city.ID, city.Name, city.District, city.Population " +
                       "FROM city JOIN country ON city.CountryCode = country.Code";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                cities.add(new Pais(
                        rs.getString("ID"),
                        rs.getString("Name"),
                        rs.getString("District"),
                        rs.getInt("Population")
                ));
            }
        }
        return cities;
    }

    public void updateCity(City city) throws SQLException {
        String query = "UPDATE city SET Name = ?, District = ?, Population = ? WHERE ID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, city.getName());
            statement.setString(2, city.getDistrict());
            statement.setInt(3, city.getPopulation());
            statement.setInt(4, city.getId());
            statement.executeUpdate();
        }
    }

    public void deleteCity(int cityId) throws SQLException {
        String query = "DELETE FROM city WHERE ID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, cityId);
            statement.executeUpdate();
        }
    }

    public void addCity(City city, String countryCode) throws SQLException {
        String query = "INSERT INTO city (Name, CountryCode, District, Population) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, city.getName());
            statement.setString(2, countryCode);
            statement.setString(3, city.getDistrict());
            statement.setInt(4, city.getPopulation());
            statement.executeUpdate();
        }
    }
}