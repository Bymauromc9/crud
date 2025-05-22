package crud;

import com.google.gson.annotations.SerializedName;

public class City {

    private int id;
    private String name;
    private String district;
    private int population;

    /**
     * Constructor
     * @param id
     * @param name
     * @param district
     * @param population
     */

    public City(int id, String name, String district, int population) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.population = population;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDistrict() {
        return district;
    }

    public int getPopulation() {
        return population;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}