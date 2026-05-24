package library.ui;

import library.exception.LibraryException;
import library.model.*;
import library.service.*;

import java.util.List;
import java.util.Scanner;

/**
 * Консольный интерфейс библиотечной системы.
 * Все меню построены через единый метод readInt — единое место обработки ввода.
 */
public class ConsoleUI {

    private static final String DIVIDER = "─".repeat(72);
    private static final String HEADER  = "═".repeat(72);

    private final Scanner        scanner;
    private final BookService    bookService;
    private final AuthorService  authorService;
    private final CategoryService categoryService;
    private final MemberService  memberService;
    private final LoanService    loanService;

    public ConsoleUI(BookService bookService,
                     AuthorService authorService,
                     CategoryService categoryService,
                     MemberService memberService,
                     LoanService loanService) {
        this.scanner         = new Scanner(System.in);
        this.bookService     = bookService;
        this.authorService   = authorService;
        this.categoryService = categoryService;
        this.memberService   = memberService;
        this.loanService     = loanService;
    }

    // ─────────────────────────────────────────── MAIN LOOP ──────

    public void run() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Выберите пункт: ");
            switch (choice) {
                case 1 -> booksMenu();
                case 2 -> authorsMenu();
                case 3 -> categoriesMenu();
                case 4 -> membersMenu();
                case 5 -> loansMenu();
                case 6 -> printStats();
                case 0 -> running = false;
                default -> warn("Неверный пункт меню.");
            }
        }
        System.out.println("\nДо свидания!");
    }

    // ─────────────────────────────────────────── MENUS ──────────

    private void printMainMenu() {
        System.out.println("\n" + HEADER);
        System.out.println("  📚  СИСТЕМА УПРАВЛЕНИЯ БИБЛИОТЕКОЙ");
        System.out.println(HEADER);
        System.out.println("  1. Книги");
        System.out.println("  2. Авторы");
        System.out.println("  3. Категории");
        System.out.println("  4. Читатели");
        System.out.println("  5. Выдача / возврат");
        System.out.println("  6. Статистика");
        System.out.println("  0. Выход");
        System.out.println(HEADER);
    }

    // ── BOOKS ────────────────────────────────────────────────────

    private void booksMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIVIDER);
            System.out.println("  КНИГИ");
            System.out.println(DIVIDER);
            System.out.println("  1. Список всех книг");
            System.out.println("  2. Найти книгу по названию");
            System.out.println("  3. Книги по автору");
            System.out.println("  4. Добавить книгу");
            System.out.println("  5. Редактировать книгу");
            System.out.println("  6. Удалить книгу");
            System.out.println("  0. Назад");
            System.out.println(DIVIDER);
            int c = readInt("Выберите: ");
            switch (c) {
                case 1 -> listBooks(bookService.getAll());
                case 2 -> {
                    String q = readString("Поиск по названию: ");
                    listBooks(bookService.searchByTitle(q));
                }
                case 3 -> {
                    listAuthors(authorService.getAll());
                    int aid = readInt("ID автора: ");
                    listBooks(bookService.getByAuthor(aid));
                }
                case 4 -> addBook();
                case 5 -> editBook();
                case 6 -> deleteBook();
                case 0 -> back = true;
                default -> warn("Неверный пункт.");
            }
        }
    }

    private void addBook() {
        System.out.println("\n--- Добавление книги ---");
        String title = readString("Название: ");
        String isbn  = readStringOrEmpty("ISBN (Enter = пропустить): ");
        int    year  = readInt("Год издания (0 = не указывать): ");
        int    copies = readInt("Количество экземпляров: ");

        listAuthors(authorService.getAll());
        int authorId = readInt("ID автора (0 = без автора): ");

        listCategories(categoryService.getAll());
        int catId = readInt("ID категории (0 = без категории): ");

        try {
            Book book = bookService.create(title, isbn.isEmpty() ? null : isbn,
                                           year, copies, authorId, catId);
            ok("Книга добавлена: " + book);
        } catch (LibraryException e) {
            warn(e.getMessage());
        }
    }

    private void editBook() {
        listBooks(bookService.getAll());
        int id = readInt("ID книги для редактирования: ");
        try {
            Book book = bookService.getById(id);
            System.out.println("Редактируем: " + book);
            String title  = readStringOrEmpty("Новое название (Enter = без изменений): ");
            String isbn   = readStringOrEmpty("Новый ISBN (Enter = без изменений): ");
            int    year   = readIntOrZero("Новый год (0 = без изменений): ");
            int    copies = readIntOrZero("Новое кол-во экз. (0 = без изменений): ");

            listAuthors(authorService.getAll());
            int authorId = readInt("ID автора (0 = без изменений): ");
            listCategories(categoryService.getAll());
            int catId = readInt("ID категории (0 = без изменений): ");

            bookService.update(id,
                title.isEmpty()  ? null : title,
                isbn.isEmpty()   ? null : isbn,
                year, copies, authorId, catId);
            ok("Книга обновлена.");
        } catch (LibraryException e) {
            warn(e.getMessage());
        }
    }

    private void deleteBook() {
        listBooks(bookService.getAll());
        int id = readInt("ID книги для удаления: ");
        if (confirm("Удалить книгу id=" + id + "?")) {
            try {
                bookService.delete(id);
                ok("Книга удалена.");
            } catch (LibraryException e) {
                warn(e.getMessage());
            }
        }
    }

    // ── AUTHORS ──────────────────────────────────────────────────

    private void authorsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIVIDER);
            System.out.println("  АВТОРЫ");
            System.out.println(DIVIDER);
            System.out.println("  1. Список авторов");
            System.out.println("  2. Найти автора");
            System.out.println("  3. Добавить автора");
            System.out.println("  4. Редактировать автора");
            System.out.println("  5. Удалить автора");
            System.out.println("  0. Назад");
            System.out.println(DIVIDER);
            int c = readInt("Выберите: ");
            switch (c) {
                case 1 -> listAuthors(authorService.getAll());
                case 2 -> {
                    String q = readString("Поиск: ");
                    listAuthors(authorService.search(q));
                }
                case 3 -> {
                    String name    = readString("Имя автора: ");
                    String country = readStringOrEmpty("Страна (Enter = пропустить): ");
                    try {
                        Author a = authorService.create(name, country.isEmpty() ? null : country);
                        ok("Автор добавлен: " + a);
                    } catch (LibraryException e) { warn(e.getMessage()); }
                }
                case 4 -> editAuthor();
                case 5 -> deleteAuthor();
                case 0 -> back = true;
                default -> warn("Неверный пункт.");
            }
        }
    }

    private void editAuthor() {
        listAuthors(authorService.getAll());
        int id = readInt("ID автора: ");
        try {
            Author a = authorService.getById(id);
            System.out.println("Редактируем: " + a);
            String name    = readStringOrEmpty("Новое имя (Enter = без изменений): ");
            String country = readStringOrEmpty("Новая страна (Enter = без изменений): ");
            authorService.update(id,
                name.isEmpty()    ? null : name,
                country.isEmpty() ? null : country);
            ok("Автор обновлён.");
        } catch (LibraryException e) { warn(e.getMessage()); }
    }

    private void deleteAuthor() {
        listAuthors(authorService.getAll());
        int id = readInt("ID автора для удаления: ");
        if (confirm("Удалить автора id=" + id + "?")) {
            try {
                authorService.delete(id);
                ok("Автор удалён.");
            } catch (LibraryException e) { warn(e.getMessage()); }
        }
    }

    // ── CATEGORIES ───────────────────────────────────────────────

    private void categoriesMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIVIDER);
            System.out.println("  КАТЕГОРИИ");
            System.out.println(DIVIDER);
            System.out.println("  1. Список категорий");
            System.out.println("  2. Добавить категорию");
            System.out.println("  3. Переименовать категорию");
            System.out.println("  4. Удалить категорию");
            System.out.println("  0. Назад");
            System.out.println(DIVIDER);
            int c = readInt("Выберите: ");
            switch (c) {
                case 1 -> listCategories(categoryService.getAll());
                case 2 -> {
                    String name = readString("Название: ");
                    try {
                        Category cat = categoryService.create(name);
                        ok("Категория добавлена: " + cat);
                    } catch (LibraryException e) { warn(e.getMessage()); }
                }
                case 3 -> {
                    listCategories(categoryService.getAll());
                    int id   = readInt("ID категории: ");
                    String n = readString("Новое название: ");
                    try {
                        categoryService.update(id, n);
                        ok("Категория обновлена.");
                    } catch (LibraryException e) { warn(e.getMessage()); }
                }
                case 4 -> {
                    listCategories(categoryService.getAll());
                    int id = readInt("ID категории для удаления: ");
                    if (confirm("Удалить категорию id=" + id + "?")) {
                        try {
                            categoryService.delete(id);
                            ok("Категория удалена.");
                        } catch (LibraryException e) { warn(e.getMessage()); }
                    }
                }
                case 0 -> back = true;
                default -> warn("Неверный пункт.");
            }
        }
    }

    // ── MEMBERS ──────────────────────────────────────────────────

    private void membersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIVIDER);
            System.out.println("  ЧИТАТЕЛИ");
            System.out.println(DIVIDER);
            System.out.println("  1. Список читателей");
            System.out.println("  2. Найти читателя");
            System.out.println("  3. Добавить читателя");
            System.out.println("  4. Редактировать читателя");
            System.out.println("  5. Удалить читателя");
            System.out.println("  0. Назад");
            System.out.println(DIVIDER);
            int c = readInt("Выберите: ");
            switch (c) {
                case 1 -> listMembers(memberService.getAll());
                case 2 -> {
                    String q = readString("Поиск по имени: ");
                    listMembers(memberService.search(q));
                }
                case 3 -> addMember();
                case 4 -> editMember();
                case 5 -> deleteMember();
                case 0 -> back = true;
                default -> warn("Неверный пункт.");
            }
        }
    }

    private void addMember() {
        System.out.println("\n--- Добавление читателя ---");
        String name  = readString("Имя: ");
        String email = readStringOrEmpty("Email (Enter = пропустить): ");
        String phone = readStringOrEmpty("Телефон (Enter = пропустить): ");
        try {
            Member m = memberService.create(name,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone);
            ok("Читатель добавлен: " + m);
        } catch (LibraryException e) { warn(e.getMessage()); }
    }

    private void editMember() {
        listMembers(memberService.getAll());
        int id = readInt("ID читателя: ");
        try {
            Member m = memberService.getById(id);
            System.out.println("Редактируем: " + m);
            String name  = readStringOrEmpty("Новое имя (Enter = без изменений): ");
            String email = readStringOrEmpty("Новый email (Enter = без изменений): ");
            String phone = readStringOrEmpty("Новый телефон (Enter = без изменений): ");
            memberService.update(id,
                name.isEmpty()  ? null : name,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone);
            ok("Читатель обновлён.");
        } catch (LibraryException e) { warn(e.getMessage()); }
    }

    private void deleteMember() {
        listMembers(memberService.getAll());
        int id = readInt("ID читателя для удаления: ");
        if (confirm("Удалить читателя id=" + id + "?")) {
            try {
                memberService.delete(id);
                ok("Читатель удалён.");
            } catch (LibraryException e) { warn(e.getMessage()); }
        }
    }

    // ── LOANS ────────────────────────────────────────────────────

    private void loansMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n" + DIVIDER);
            System.out.println("  ВЫДАЧА / ВОЗВРАТ");
            System.out.println(DIVIDER);
            System.out.println("  1. Все записи о выдачах");
            System.out.println("  2. Активные выдачи (не возвращены)");
            System.out.println("  3. История читателя");
            System.out.println("  4. Выдать книгу");
            System.out.println("  5. Принять возврат");
            System.out.println("  6. Удалить запись о выдаче");
            System.out.println("  0. Назад");
            System.out.println(DIVIDER);
            int c = readInt("Выберите: ");
            switch (c) {
                case 1 -> listLoans(loanService.getAll());
                case 2 -> listLoans(loanService.getActive());
                case 3 -> {
                    listMembers(memberService.getAll());
                    int mid = readInt("ID читателя: ");
                    listLoans(loanService.getByMember(mid));
                }
                case 4 -> issueBook();
                case 5 -> returnBook();
                case 6 -> {
                    listLoans(loanService.getAll());
                    int lid = readInt("ID записи для удаления: ");
                    if (confirm("Удалить запись id=" + lid + "?")) {
                        try {
                            loanService.delete(lid);
                            ok("Запись удалена.");
                        } catch (LibraryException e) { warn(e.getMessage()); }
                    }
                }
                case 0 -> back = true;
                default -> warn("Неверный пункт.");
            }
        }
    }

    private void issueBook() {
        System.out.println("\n--- Выдача книги ---");
        listBooks(bookService.getAll());
        int bid = readInt("ID книги: ");
        listMembers(memberService.getAll());
        int mid = readInt("ID читателя: ");
        try {
            Loan loan = loanService.issueBook(bid, mid);
            ok("Книга выдана. Запись #" + loan.getId() + ", дата: " + loan.getLoanDate());
        } catch (LibraryException e) { warn(e.getMessage()); }
    }

    private void returnBook() {
        System.out.println("\n--- Возврат книги ---");
        listLoans(loanService.getActive());
        int lid = readInt("ID записи о выдаче: ");
        try {
            loanService.returnBook(lid);
            ok("Книга возвращена.");
        } catch (LibraryException e) { warn(e.getMessage()); }
    }

    // ── STATS ────────────────────────────────────────────────────

    private void printStats() {
        System.out.println("\n" + HEADER);
        System.out.println("  СТАТИСТИКА");
        System.out.println(HEADER);
        System.out.printf("  Книг в каталоге:   %d%n", bookService.count());
        System.out.printf("  Авторов:           %d%n", authorService.count());
        System.out.printf("  Категорий:         %d%n", categoryService.getAll().size());
        System.out.printf("  Читателей:         %d%n", memberService.count());
        System.out.printf("  Всего выдач:       %d%n", loanService.count());
        System.out.printf("  Активных выдач:    %d%n", loanService.getActive().size());
        System.out.println(HEADER);
    }

    // ─────────────────────────────────────────── LIST HELPERS ───

    private void listBooks(List<Book> books) {
        System.out.println("\n--- Книги (" + books.size() + ") ---");
        if (books.isEmpty()) { System.out.println("  (список пуст)"); return; }
        books.forEach(b -> System.out.println("  " + b));
    }

    private void listAuthors(List<Author> authors) {
        System.out.println("\n--- Авторы (" + authors.size() + ") ---");
        if (authors.isEmpty()) { System.out.println("  (список пуст)"); return; }
        authors.forEach(a -> System.out.println("  " + a));
    }

    private void listCategories(List<Category> cats) {
        System.out.println("\n--- Категории (" + cats.size() + ") ---");
        if (cats.isEmpty()) { System.out.println("  (список пуст)"); return; }
        cats.forEach(c -> System.out.println("  " + c));
    }

    private void listMembers(List<Member> members) {
        System.out.println("\n--- Читатели (" + members.size() + ") ---");
        if (members.isEmpty()) { System.out.println("  (список пуст)"); return; }
        members.forEach(m -> System.out.println("  " + m));
    }

    private void listLoans(List<Loan> loans) {
        System.out.println("\n--- Выдачи (" + loans.size() + ") ---");
        if (loans.isEmpty()) { System.out.println("  (список пуст)"); return; }
        loans.forEach(l -> System.out.println("  " + l));
    }

    // ─────────────────────────────────────────── INPUT HELPERS ──

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                warn("Введите целое число.");
            }
        }
    }

    private int readIntOrZero(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        try { return Integer.parseInt(line); } catch (NumberFormatException e) { return 0; }
    }

    private String readString(String prompt) {
        String val = "";
        while (val.isBlank()) {
            System.out.print(prompt);
            val = scanner.nextLine().trim();
            if (val.isBlank()) warn("Поле не может быть пустым.");
        }
        return val;
    }

    private String readStringOrEmpty(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private boolean confirm(String question) {
        System.out.print(question + " [y/N]: ");
        String ans = scanner.nextLine().trim().toLowerCase();
        return ans.equals("y") || ans.equals("да");
    }

    // ─────────────────────────────────────────── OUTPUT HELPERS ─

    private void ok(String msg) {
        System.out.println("  ✔  " + msg);
    }

    private void warn(String msg) {
        System.out.println("  ✘  " + msg);
    }

    private void printBanner() {
        System.out.println(HEADER);
        System.out.println("  ██╗     ██╗██████╗ ██████╗  █████╗ ██████╗ ██╗   ██╗");
        System.out.println("  ██║     ██║██╔══██╗██╔══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝");
        System.out.println("  ██║     ██║██████╔╝██████╔╝███████║██████╔╝ ╚████╔╝ ");
        System.out.println("  ██║     ██║██╔══██╗██╔══██╗██╔══██║██╔══██╗  ╚██╔╝  ");
        System.out.println("  ███████╗██║██████╔╝██║  ██║██║  ██║██║  ██║   ██║   ");
        System.out.println("  ╚══════╝╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝  ");
        System.out.println(HEADER);
        System.out.println("  Система управления библиотекой v1.0  |  Java + SQLite");
        System.out.println(HEADER);
    }
}
