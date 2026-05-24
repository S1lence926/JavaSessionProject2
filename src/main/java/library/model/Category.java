package library.model;

/**
 * Сущность «Категория» книги (Роман, Техническая, Наука и т.д.)
 */
public class Category extends BaseEntity {

    private String name;

    public Category() {}

    public Category(int id, String name) {
        super(id);
        this.name = name;
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void   setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return String.format("[%d] %s", id, name);
    }
}
