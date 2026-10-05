package br.com.dio.board.dao;

import br.com.dio.board.model.CardBlock;

import java.sql.*;
import java.util.Optional;

public class CardBlockDAO {
    private final Connection connection;

    public CardBlockDAO(Connection connection) {
        this.connection = connection;
    }

    public void block(long cardId, String reason) throws SQLException {
        String sql = "INSERT INTO card_blocks(card_id, block_reason) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.setString(2, reason);
            ps.executeUpdate();
        }
    }

    public void unblock(long cardId, String reason) throws SQLException {
        String sql = "UPDATE card_blocks SET unblocked_at = CURRENT_TIMESTAMP, unblock_reason = ? " +
                "WHERE card_id = ? AND unblocked_at IS NULL ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setLong(2, cardId);
            ps.executeUpdate();
        }
    }

    public Optional<CardBlock> findActiveBlock(long cardId) throws SQLException {
        String sql = "SELECT id, card_id, blocked_at, block_reason, unblocked_at, unblock_reason " +
                "FROM card_blocks WHERE card_id = ? AND unblocked_at IS NULL ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Timestamp unblockedAt = rs.getTimestamp("unblocked_at");
                return Optional.of(new CardBlock(
                        rs.getLong("id"),
                        rs.getLong("card_id"),
                        rs.getTimestamp("blocked_at").toLocalDateTime(),
                        rs.getString("block_reason"),
                        unblockedAt == null ? null : unblockedAt.toLocalDateTime(),
                        rs.getString("unblock_reason")));
            }
        }
    }
}
