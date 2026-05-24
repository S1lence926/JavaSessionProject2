package library;

import library.db.DatabaseConnection;
import library.repository.*;
import library.service.*;
import library.ui.ConsoleUI;

/**
 * Точка входа в приложение.
 * Вручную инициализирует все зависимости (dependency injection без фреймворка).
 *
 * Слои приложения:
 *   Main → UI → Service → Repository → DB
 */
public class Main {

    public static void main(String[] args) {

        // 1. Инициализация БД (singleton, создаёт таблицы при первом запуске)
        DatabaseConnection.getInstance();

        // 2. Репозитории
        AuthorRepository   authorRepo   = new AuthorRepository();
        CategoryRepository categoryRepo = new CategoryRepository();
        BookRepository     bookRepo     = new BookRepository();
        MemberRepository   memberRepo   = new MemberRepository();
        LoanRepository     loanRepo     = new LoanRepository();

        // 3. Сервисы
        AuthorService   authorService   = new AuthorService(authorRepo);
        CategoryService categoryService = new CategoryService(categoryRepo);
        BookService     bookService     = new BookService(bookRepo, authorRepo, categoryRepo);
        MemberService   memberService   = new MemberService(memberRepo);
        LoanService     loanService     = new LoanService(loanRepo, bookRepo, memberRepo);

        // 4. Запуск консольного интерфейса
        ConsoleUI ui = new ConsoleUI(bookService, authorService, categoryService,
                                     memberService, loanService);

        try {
            ui.run();
        } finally {
            DatabaseConnection.getInstance().close();
        }
    }
}
