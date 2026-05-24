package library.repository;

import library.db.DatabaseConnection;
import library.model.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberRepository implements CrudRepository<Member, Integer> {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO members(name, email, phone) VALUES(?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) member.setId(keys.getInt(1));
            }
            return member;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сохранения читателя: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Member> findById(Integer id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска читателя id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Member> findAll() {
        List<Member> list = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT * FROM members ORDER BY name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения читателей: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Member member) {
        String sql = "UPDATE members SET name = ?, email = ?, phone = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setInt   (4, member.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления читателя: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления читателя id=" + id, e);
        }
    }

    @Override
    public long count() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT COUNT(*) FROM members")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подсчёта читателей", e);
        }
    }

    public List<Member> findByName(String name) {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска читателя по имени", e);
        }
        return list;
    }

    private Member map(ResultSet rs) throws SQLException {
        return new Member(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone")
        );
    }
}
