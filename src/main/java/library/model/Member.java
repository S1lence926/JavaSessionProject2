package library.model;

/**
 * Сущность «Читатель» библиотеки.
 */
public class Member extends BaseEntity {

    private String name;
    private String email;
    private String phone;

    public Member() {}

    public Member(int id, String name, String email, String phone) {
        super(id);
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    public Member(String name, String email, String phone) {
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName()  { return name; }
    public void   setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void   setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void   setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return String.format("[%d] %s | Email: %-25s | Тел: %s",
            id, name,
            email != null ? email : "—",
            phone != null ? phone : "—");
    }
}
