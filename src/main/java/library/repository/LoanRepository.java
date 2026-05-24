package library.repository;

import library.db.DatabaseConnection;
import library.model.Loan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanRepository implements CrudRepository<Loan, Integer> {

    private static final String SELECT_FULL = """
        SELECT l.id, l.book_id, b.title AS book_title,
               l.member_id, m.name AS member_name,
               l.loan_date, l.return_date
        FROM   loans l
        LEFT JOIN books   b ON b.id = l.book_id
        LEFT JOIN members m ON m.id = l.member_id
        """;

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Loan save(Loan loan) {
        String sql = "INSERT INTO loans(book_id, member_id, loan_date) VALUES(?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, loan.getBookId());
            ps.setInt   (2, loan.getMemberId());
            ps.setString(3, loan.getLoanDate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) loan.setId(keys.getInt(1));
            }
            return loan;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания записи о выдаче: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Loan> findById(Integer id) {
        String sql = SELECT_FULL + " WHERE l.id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска выдачи id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Loan> findAll() {
        List<Loan> list = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery(SELECT_FULL + " ORDER BY l.loan_date DESC")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения выдач: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Loan loan) {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, loan.getReturnDate());
            ps.setInt   (2, loan.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления выдачи: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM loans WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления выдачи id=" + id, e);
        }
    }

    @Override
    public long count() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT COUNT(*) FROM loans")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подсчёта выдач", e);
        }
    }

    /** Активные выдачи (книга не возвращена) */
    public List<Loan> findActive() {
        List<Loan> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE l.return_date IS NULL ORDER BY l.loan_date DESC";
        try (Statement stmt = conn().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения активных выдач", e);
        }
        return list;
    }

    /** Выдачи конкретного читателя */
    public List<Loan> findByMemberId(int memberId) {
        List<Loan> list = new ArrayList<>();
        String sql = SELECT_FULL + " WHERE l.member_id = ? ORDER BY l.loan_date DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска выдач читателя id=" + memberId, e);
        }
        return list;
    }

    private Loan map(ResultSet rs) throws SQLException {
        return new Loan(
            rs.getInt("id"),
            rs.getInt("book_id"),
            rs.getString("book_title"),
            rs.getInt("member_id"),
            rs.getString("member_name"),
            rs.getString("loan_date"),
            rs.getString("return_date")
        );
    }
}
