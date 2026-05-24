package library.model;

/**
 * Абстрактный базовый класс для всех сущностей системы.
 * Демонстрирует наследование и инкапсуляцию.
 */
public abstract class BaseEntity {

    protected int id;

    protected BaseEntity() {}

    protected BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /** Каждая сущность должна уметь описать себя — полиморфизм через переопределение */
    @Override
    public abstract String toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity other)) return false;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
