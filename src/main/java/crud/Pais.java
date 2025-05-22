package crud;

public class Pais {
    private String id;
    private String nombre;
    private String distrito;
    private int poblacion;

    public Pais(String id, String nombre, String distrito, int poblacion) {
        this.id = id;
        this.nombre = nombre;
        this.distrito = distrito;
        this.poblacion = poblacion;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDistrito() {
        return distrito;
    }

    public int getPoblacion() {
        return poblacion;
    }
}
