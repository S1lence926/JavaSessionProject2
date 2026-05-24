package library.service;

import library.exception.LibraryException;
import library.model.Category;
import library.repository.CategoryRepository;

import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Category create(String name) {
        if (name == null || name.isBlank())
            throw new LibraryException("Название категории не может быть пустым.");
        return categoryRepo.save(new Category(name.trim()));
    }

    public Category getById(int id) {
        return categoryRepo.findById(id)
            .orElseThrow(() -> new LibraryException("Категория id=" + id + " не найдена."));
    }

    public List<Category> getAll() {
        return categoryRepo.findAll();
    }

    public void update(int id, String name) {
        Category cat = getById(id);
        if (name != null && !name.isBlank()) cat.setName(name.trim());
        categoryRepo.update(cat);
    }

    public void delete(int id) {
        getById(id);
        categoryRepo.deleteById(id);
    }
}
