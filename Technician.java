
package model;

public class Technician {
    private String id;
    private String name;

    public Technician(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + " | " + name;
    }
}
