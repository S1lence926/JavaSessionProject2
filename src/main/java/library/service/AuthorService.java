package library.service;

import library.exception.LibraryException;
import library.model.Author;
import library.repository.AuthorRepository;

import java.util.List;

/**
 * Сервисный слой для сущности Author.
 * Содержит бизнес-правила и валидацию.
 */
public class AuthorService {

    private final AuthorRepository authorRepo;

    public AuthorService(AuthorRepository authorRepo) {
        this.authorRepo = authorRepo;
    }

    public Author create(String name, String country) {
        if (name == null || name.isBlank())
            throw new LibraryException("Имя автора не может быть пустым.");
        Author author = new Author(name.trim(), country != null ? country.trim() : null);
        return authorRepo.save(author);
    }

    public Author getById(int id) {
        return authorRepo.findById(id)
            .orElseThrow(() -> new LibraryException("Автор с id=" + id + " не найден."));
    }

    public List<Author> getAll() {
        return authorRepo.findAll();
    }

    public List<Author> search(String name) {
        return authorRepo.findByName(name);
    }

    public void update(int id, String name, String country) {
        Author author = getById(id);
        if (name != null && !name.isBlank()) author.setName(name.trim());
        if (country != null)                  author.setCountry(country.trim());
        authorRepo.update(author);
    }

    public void delete(int id) {
        getById(id); // проверка существования
        authorRepo.deleteById(id);
    }

    public long count() {
        return authorRepo.count();
    }
}
