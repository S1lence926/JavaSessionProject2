package library.model;

/**
 * Сущность «Автор».
 * Наследует BaseEntity — id, equals, hashCode.
 */
public class Author extends BaseEntity {

    private String name;
    private String country;

    public Author() {}

    public Author(int id, String name, String country) {
        super(id);
        this.name    = name;
        this.country = country;
    }

    public Author(String name, String country) {
        this.name    = name;
        this.country = country;
    }

    // --- Геттеры / сеттеры (инкапсуляция) ---

    public String getName()    { return name; }
    public void   setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void   setCountry(String country) { this.country = country; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s)", id, name, country != null ? country : "—");
    }
}
