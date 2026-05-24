package library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton-класс для управления подключением к SQLite БД.
 * Инициализирует таблицы при первом запуске.
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            initSchema();
            System.out.println("[DB] Подключение к базе данных установлено.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC драйвер не найден.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к БД: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка переподключения к БД: " + e.getMessage(), e);
        }
        return connection;
    }

    /**
     * Создаёт схему БД: таблицы authors, categories, books, members, loans.
     */
    private void initSchema() throws SQLException {
        String createAuthors = """
            CREATE TABLE IF NOT EXISTS authors (
                id      INTEGER PRIMARY KEY AUTOINCREMENT,
                name    TEXT NOT NULL,
                country TEXT
            );
            """;

        String createCategories = """
            CREATE TABLE IF NOT EXISTS categories (
                id   INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
            """;

        String createBooks = """
            CREATE TABLE IF NOT EXISTS books (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                title       TEXT NOT NULL,
                isbn        TEXT UNIQUE,
                year        INTEGER,
                copies      INTEGER NOT NULL DEFAULT 1,
                author_id   INTEGER,
                category_id INTEGER,
                FOREIGN KEY (author_id)   REFERENCES authors(id)    ON DELETE SET NULL,
                FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
            );
            """;

        String createMembers = """
            CREATE TABLE IF NOT EXISTS members (
                id    INTEGER PRIMARY KEY AUTOINCREMENT,
                name  TEXT NOT NULL,
                email TEXT UNIQUE,
                phone TEXT
            );
            """;

        String createLoans = """
            CREATE TABLE IF NOT EXISTS loans (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id     INTEGER NOT NULL,
                member_id   INTEGER NOT NULL,
                loan_date   TEXT NOT NULL,
                return_date TEXT,
                FOREIGN KEY (book_id)   REFERENCES books(id)   ON DELETE CASCADE,
                FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
            );
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createAuthors);
            stmt.execute(createCategories);
            stmt.execute(createBooks);
            stmt.execute(createMembers);
            stmt.execute(createLoans);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Соединение закрыто.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }
}
