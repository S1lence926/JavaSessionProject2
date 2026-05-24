package library.repository;

import library.db.DatabaseConnection;
import library.model.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для сущности Author.
 * Реализует CrudRepository — все SQL-операции инкапсулированы здесь.
 */
public class AuthorRepository implements CrudRepository<Author, Integer> {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Author save(Author author) {
        String sql = "INSERT INTO authors(name, country) VALUES(?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getCountry());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) author.setId(keys.getInt(1));
            }
            return author;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении автора: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Author> findById(Integer id) {
        String sql = "SELECT * FROM authors WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска автора по id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Author> findAll() {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT * FROM authors ORDER BY name";
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка авторов: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Author author) {
        String sql = "UPDATE authors SET name = ?, country = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getCountry());
            ps.setInt(3, author.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления автора: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM authors WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления автора id=" + id, e);
        }
    }

    @Override
    public long count() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT COUNT(*) FROM authors")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подсчёта авторов", e);
        }
    }

    public List<Author> findByName(String name) {
        List<Author> list = new ArrayList<>();
        String sql = "SELECT * FROM authors WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска автора по имени", e);
        }
        return list;
    }

    private Author map(ResultSet rs) throws SQLException {
        return new Author(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("country")
        );
    }
}
