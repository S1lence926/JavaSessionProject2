package library.model;

/**
 * Сущность «Книга».
 * Связана с Author (Many-to-One) и Category (Many-to-One).
 */
public class Book extends BaseEntity {

    private String title;
    private String isbn;
    private int    year;
    private int    copies;

    // Денормализованные поля для удобства отображения
    private int    authorId;
    private String authorName;
    private int    categoryId;
    private String categoryName;

    public Book() {}

    public Book(int id, String title, String isbn, int year, int copies,
                int authorId, String authorName, int categoryId, String categoryName) {
        super(id);
        this.title        = title;
        this.isbn         = isbn;
        this.year         = year;
        this.copies       = copies;
        this.authorId     = authorId;
        this.authorName   = authorName;
        this.categoryId   = categoryId;
        this.categoryName = categoryName;
    }

    public Book(String title, String isbn, int year, int copies, int authorId, int categoryId) {
        this.title      = title;
        this.isbn       = isbn;
        this.year       = year;
        this.copies     = copies;
        this.authorId   = authorId;
        this.categoryId = categoryId;
    }

    // --- Геттеры / сеттеры ---

    public String getTitle()    { return title; }
    public void   setTitle(String title) { this.title = title; }

    public String getIsbn()     { return isbn; }
    public void   setIsbn(String isbn) { this.isbn = isbn; }

    public int    getYear()     { return year; }
    public void   setYear(int year) { this.year = year; }

    public int    getCopies()   { return copies; }
    public void   setCopies(int copies) { this.copies = copies; }

    public int    getAuthorId()     { return authorId; }
    public void   setAuthorId(int authorId) { this.authorId = authorId; }

    public String getAuthorName()   { return authorName; }
    public void   setAuthorName(String n) { this.authorName = n; }

    public int    getCategoryId()   { return categoryId; }
    public void   setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void   setCategoryName(String n) { this.categoryName = n; }

    @Override
    public String toString() {
        return String.format(
            "[%d] \"%s\" | ISBN: %-14s | %d г. | %d экз. | Автор: %s | Категория: %s",
            id,
            title,
            isbn != null ? isbn : "—",
            year,
            copies,
            authorName   != null ? authorName   : "—",
            categoryName != null ? categoryName : "—"
        );
    }
}
