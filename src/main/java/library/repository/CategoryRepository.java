package library.repository;

import library.db.DatabaseConnection;
import library.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepository implements CrudRepository<Category, Integer> {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Category save(Category cat) {
        String sql = "INSERT INTO categories(name) VALUES(?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cat.setId(keys.getInt(1));
            }
            return cat;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения категории: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Category> findById(Integer id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска категории id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT * FROM categories ORDER BY name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения категорий: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Category cat) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, cat.getName());
            ps.setInt(2, cat.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления категории: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления категории id=" + id, e);
        }
    }

    @Override
    public long count() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT COUNT(*) FROM categories")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подсчёта категорий", e);
        }
    }

    private Category map(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("id"), rs.getString("name"));
    }
}
