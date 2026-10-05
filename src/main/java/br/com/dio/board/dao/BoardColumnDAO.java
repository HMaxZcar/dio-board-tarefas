package br.com.dio.board.dao;

import br.com.dio.board.model.BoardColumn;
import br.com.dio.board.model.BoardColumnType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardColumnDAO {
    private final Connection connection;

    public BoardColumnDAO(Connection connection) {
        this.connection = connection;
    }

    public BoardColumn insert(long boardId, String name, int order, BoardColumnType type) throws SQLException {
        String sql = "INSERT INTO board_columns(board_id, name, column_order, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, boardId);
            ps.setString(2, name);
            ps.setInt(3, order);
            ps.setString(4, type.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new BoardColumn(rs.getLong(1), boardId, name, order, type);
                }
            }
        }
        throw new SQLException("Não foi possível obter o ID da coluna criada.");
    }

    public List<BoardColumn> findByBoardId(long boardId) throws SQLException {
        String sql = "SELECT id, board_id, name, column_order, type FROM board_columns WHERE board_id = ? ORDER BY column_order";
        List<BoardColumn> columns = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    columns.add(map(rs));
                }
            }
        }
        return columns;
    }

    public Optional<BoardColumn> findById(long id) throws SQLException {
        String sql = "SELECT id, board_id, name, column_order, type FROM board_columns WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public Optional<BoardColumn> findByBoardAndType(long boardId, BoardColumnType type) throws SQLException {
        String sql = "SELECT id, board_id, name, column_order, type FROM board_columns WHERE board_id = ? AND type = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setString(2, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public Optional<BoardColumn> findNextColumn(long boardId, int currentOrder) throws SQLException {
        String sql = "SELECT id, board_id, name, column_order, type FROM board_columns " +
                "WHERE board_id = ? AND column_order > ? AND type <> 'CANCEL' ORDER BY column_order LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setInt(2, currentOrder);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    private BoardColumn map(ResultSet rs) throws SQLException {
        return new BoardColumn(
                rs.getLong("id"),
                rs.getLong("board_id"),
                rs.getString("name"),
                rs.getInt("column_order"),
                BoardColumnType.valueOf(rs.getString("type")));
    }
}
