package br.com.dio.board.dao;

import br.com.dio.board.model.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardDAO {
    private final Connection connection;

    public CardDAO(Connection connection) {
        this.connection = connection;
    }

    public Card insert(String title, String description, long columnId) throws SQLException {
        String sql = "INSERT INTO cards(title, description, column_id, blocked) VALUES (?, ?, ?, false)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setLong(3, columnId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Card(rs.getLong(1), title, description, columnId, false, null);
                }
            }
        }
        throw new SQLException("Não foi possível obter o ID do card criado.");
    }

    public Optional<Card> findById(long id) throws SQLException {
        String sql = "SELECT id, title, description, column_id, blocked, created_at FROM cards WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<Card> findByBoardId(long boardId) throws SQLException {
        String sql = "SELECT c.id, c.title, c.description, c.column_id, c.blocked, c.created_at " +
                "FROM cards c JOIN board_columns bc ON bc.id = c.column_id " +
                "WHERE bc.board_id = ? ORDER BY bc.column_order, c.id";
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.add(map(rs));
                }
            }
        }
        return cards;
    }

    public List<Card> findByColumnId(long columnId) throws SQLException {
        String sql = "SELECT id, title, description, column_id, blocked, created_at FROM cards WHERE column_id = ? ORDER BY id";
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, columnId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cards.add(map(rs));
            }
        }
        return cards;
    }

    public void move(long cardId, long columnId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE cards SET column_id = ? WHERE id = ?")) {
            ps.setLong(1, columnId);
            ps.setLong(2, cardId);
            ps.executeUpdate();
        }
    }

    public void setBlocked(long cardId, boolean blocked) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE cards SET blocked = ? WHERE id = ?")) {
            ps.setBoolean(1, blocked);
            ps.setLong(2, cardId);
            ps.executeUpdate();
        }
    }

    private Card map(ResultSet rs) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("created_at");
        return new Card(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getLong("column_id"),
                rs.getBoolean("blocked"),
                createdAt == null ? null : createdAt.toLocalDateTime());
    }
}
