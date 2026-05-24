package library.service;

import library.exception.LibraryException;
import library.model.Book;
import library.repository.AuthorRepository;
import library.repository.BookRepository;
import library.repository.CategoryRepository;

import java.util.List;

public class BookService {

    private final BookRepository     bookRepo;
    private final AuthorRepository   authorRepo;
    private final CategoryRepository categoryRepo;

    public BookService(BookRepository bookRepo,
                       AuthorRepository authorRepo,
                       CategoryRepository categoryRepo) {
        this.bookRepo     = bookRepo;
        this.authorRepo   = authorRepo;
        this.categoryRepo = categoryRepo;
    }

    public Book create(String title, String isbn, int year, int copies,
                       int authorId, int categoryId) {
        if (title == null || title.isBlank())
            throw new LibraryException("Название книги не может быть пустым.");
        if (copies < 0)
            throw new LibraryException("Количество экземпляров не может быть отрицательным.");
        // Проверяем существование связанных сущностей
        if (authorId > 0)
            authorRepo.findById(authorId)
                .orElseThrow(() -> new LibraryException("Автор id=" + authorId + " не найден."));
        if (categoryId > 0)
            categoryRepo.findById(categoryId)
                .orElseThrow(() -> new LibraryException("Категория id=" + categoryId + " не найдена."));

        Book book = new Book(title.trim(), isbn, year, copies, authorId, categoryId);
        return bookRepo.save(book);
    }

    public Book getById(int id) {
        return bookRepo.findById(id)
            .orElseThrow(() -> new LibraryException("Книга id=" + id + " не найдена."));
    }

    public List<Book> getAll() {
        return bookRepo.findAll();
    }

    public List<Book> searchByTitle(String title) {
        return bookRepo.findByTitle(title);
    }

    public List<Book> getByAuthor(int authorId) {
        return bookRepo.findByAuthorId(authorId);
    }

    public void update(int id, String title, String isbn, int year,
                       int copies, int authorId, int categoryId) {
        Book book = getById(id);
        if (title != null && !title.isBlank()) book.setTitle(title.trim());
        if (isbn  != null && !isbn.isBlank())  book.setIsbn(isbn.trim());
        if (year   > 0)    book.setYear(year);
        if (copies >= 0)   book.setCopies(copies);
        if (authorId   > 0) book.setAuthorId(authorId);
        if (categoryId > 0) book.setCategoryId(categoryId);
        bookRepo.update(book);
    }

    public void delete(int id) {
        getById(id);
        bookRepo.deleteById(id);
    }

    public long count() {
        return bookRepo.count();
    }
}
