package library.repository;

import library.db.DatabaseConnection;
import library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository implements CrudRepository<Book, Integer> {

    private static final String SELECT_FULL = """
        SELECT b.id, b.title, b.isbn, b.year, b.copies,
               b.author_id,   a.name  AS author_name,
               b.category_id, c.name  AS category_name
        FROM   books b
        LEFT JOIN authors    a ON a.id = b.author_id
        LEFT JOIN categories c ON c.id = b.category_id
        """;

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Book save(Book book) {
        String sql = """
            INSERT INTO books(title, isbn, year, copies, author_id, category_id)
            VALUES(?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setInt   (3, book.getYear());
            ps.setInt   (4, book.getCopies());
            setNullableInt(ps, 5, book.getAuthorId());
            setNullableInt(ps, 6, book.getCategoryId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) book.setId(keys.getInt(1));
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения книги: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Book> findById(Integer id) {
        String sql = SELECT_FULL + " WHERE b.id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска книги id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery(SELECT_FULL + " ORDER BY b.title")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения книг: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Book book) {
        String sql = """
            UPDATE books
            SET title = ?, isbn = ?, year = ?, copies = ?, author_id = ?, category_id = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setInt   (3, book.getYear());
            ps.setInt   (4, book.getCopies());
            setNullableInt(ps, 5, book.getAuthorId());
            setNullableInt(ps, 6, book.getCategoryId());
            ps.setInt   (7, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления книги: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления книги id=" + id, e);
        }
    }

    @Override
    public long count() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT COUNT(*) FROM books")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подсчёта книг", e);
        }
    }

    /** Поиск по части названия */
    public List<Book> findByTitle(String title) {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE LOWER(b.title) LIKE LOWER(?) ORDER BY b.title";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска книги по названию", e);
        }
        return list;
    }

    /** Книги конкретного автора */
    public List<Book> findByAuthorId(int authorId) {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE b.author_id = ? ORDER BY b.title";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска книг автора id=" + authorId, e);
        }
        return list;
    }

    /** Уменьшить количество экземпляров на 1 при выдаче */
    public void decrementCopies(int bookId) {
        String sql = "UPDATE books SET copies = copies - 1 WHERE id = ? AND copies > 0";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RuntimeException("Нет доступных экземпляров книги id=" + bookId);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка уменьшения копий: " + e.getMessage(), e);
        }
    }

    /** Увеличить количество экземпляров на 1 при возврате */
    public void incrementCopies(int bookId) {
        String sql = "UPDATE books SET copies = copies + 1 WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка увеличения копий: " + e.getMessage(), e);
        }
    }

    private Book map(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("isbn"),
            rs.getInt("year"),
            rs.getInt("copies"),
            rs.getInt("author_id"),
            rs.getString("author_name"),
            rs.getInt("category_id"),
            rs.getString("category_name")
        );
    }

    private void setNullableInt(PreparedStatement ps, int idx, int value) throws SQLException {
        if (value > 0) ps.setInt(idx, value);
        else           ps.setNull(idx, Types.INTEGER);
    }
}
