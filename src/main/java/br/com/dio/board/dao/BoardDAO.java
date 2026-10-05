package br.com.dio.board.dao;

import br.com.dio.board.model.Board;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardDAO {
    private final Connection connection;

    public BoardDAO(Connection connection) {
        this.connection = connection;
    }

    public Board insert(String name) throws SQLException {
        String sql = "INSERT INTO boards(name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Board(rs.getLong(1), name, null);
                }
            }
        }
        throw new SQLException("Não foi possível obter o ID do board criado.");
    }

    public List<Board> findAll() throws SQLException {
        String sql = "SELECT id, name, created_at FROM boards ORDER BY id";
        List<Board> boards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp createdAt = rs.getTimestamp("created_at");
                boards.add(new Board(
                        rs.getLong("id"),
                        rs.getString("name"),
                        createdAt == null ? null : createdAt.toLocalDateTime()));
            }
        }
        return boards;
    }

    public Optional<Board> findById(long id) throws SQLException {
        String sql = "SELECT id, name, created_at FROM boards WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    return Optional.of(new Board(
                            rs.getLong("id"),
                            rs.getString("name"),
                            createdAt == null ? null : createdAt.toLocalDateTime()));
                }
            }
        }
        return Optional.empty();
    }

    public boolean delete(long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM boards WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
