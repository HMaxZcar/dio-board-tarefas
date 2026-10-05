package br.com.dio.board.service;

import br.com.dio.board.dao.BoardColumnDAO;
import br.com.dio.board.dao.CardBlockDAO;
import br.com.dio.board.dao.CardDAO;
import br.com.dio.board.exception.BusinessException;
import br.com.dio.board.model.BoardColumn;
import br.com.dio.board.model.BoardColumnType;
import br.com.dio.board.model.Card;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CardService {
    private final Connection connection;
    private final CardDAO cardDAO;
    private final BoardColumnDAO columnDAO;
    private final CardBlockDAO blockDAO;

    public CardService(Connection connection) {
        this.connection = connection;
        this.cardDAO = new CardDAO(connection);
        this.columnDAO = new BoardColumnDAO(connection);
        this.blockDAO = new CardBlockDAO(connection);
    }

    public Card create(long boardId, String title, String description) {
        if (title == null || title.isBlank()) {
            throw new BusinessException("O título do card é obrigatório.");
        }
        try {
            BoardColumn initial = columnDAO.findByBoardAndType(boardId, BoardColumnType.INITIAL)
                    .orElseThrow(() -> new BusinessException("Coluna inicial não encontrada."));
            return cardDAO.insert(title.trim(), description == null ? "" : description.trim(), initial.getId());
        } catch (SQLException e) {
            throw new BusinessException("Erro ao criar card: " + e.getMessage());
        }
    }

    public List<Card> listByBoard(long boardId) {
        try {
            return cardDAO.findByBoardId(boardId);
        } catch (SQLException e) {
            throw new BusinessException("Erro ao listar cards: " + e.getMessage());
        }
    }

    public Card get(long cardId) {
        try {
            return cardDAO.findById(cardId)
                    .orElseThrow(() -> new BusinessException("Card não encontrado."));
        } catch (SQLException e) {
            throw new BusinessException("Erro ao consultar card: " + e.getMessage());
        }
    }

    public void moveToNextColumn(long cardId) {
        try {
            Card card = get(cardId);
            if (card.isBlocked()) {
                throw new BusinessException("O card está bloqueado e não pode ser movido.");
            }

            BoardColumn current = columnDAO.findById(card.getColumnId())
                    .orElseThrow(() -> new BusinessException("Coluna atual não encontrada."));

            if (current.getType() == BoardColumnType.FINAL) {
                throw new BusinessException("O card já está na coluna final.");
            }
            if (current.getType() == BoardColumnType.CANCEL) {
                throw new BusinessException("Um card cancelado não pode ser movido.");
            }

            BoardColumn next = columnDAO.findNextColumn(current.getBoardId(), current.getOrder())
                    .orElseThrow(() -> new BusinessException("Não existe próxima coluna disponível."));
            cardDAO.move(cardId, next.getId());
        } catch (SQLException e) {
            throw new BusinessException("Erro ao mover card: " + e.getMessage());
        }
    }

    public void cancel(long cardId) {
        try {
            Card card = get(cardId);
            if (card.isBlocked()) {
                throw new BusinessException("O card está bloqueado e não pode ser cancelado.");
            }
            BoardColumn current = columnDAO.findById(card.getColumnId())
                    .orElseThrow(() -> new BusinessException("Coluna atual não encontrada."));
            if (current.getType() == BoardColumnType.FINAL) {
                throw new BusinessException("Um card concluído não pode ser cancelado.");
            }
            if (current.getType() == BoardColumnType.CANCEL) {
                throw new BusinessException("O card já está cancelado.");
            }
            BoardColumn cancel = columnDAO.findByBoardAndType(current.getBoardId(), BoardColumnType.CANCEL)
                    .orElseThrow(() -> new BusinessException("Coluna de cancelamento não encontrada."));
            cardDAO.move(cardId, cancel.getId());
        } catch (SQLException e) {
            throw new BusinessException("Erro ao cancelar card: " + e.getMessage());
        }
    }

    public void block(long cardId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("Informe o motivo do bloqueio.");
        }
        try {
            Card card = get(cardId);
            if (card.isBlocked()) {
                throw new BusinessException("O card já está bloqueado.");
            }
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                blockDAO.block(cardId, reason.trim());
                cardDAO.setBlocked(cardId, true);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new BusinessException("Erro ao bloquear card: " + e.getMessage());
        }
    }

    public void unblock(long cardId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("Informe o motivo do desbloqueio.");
        }
        try {
            Card card = get(cardId);
            if (!card.isBlocked()) {
                throw new BusinessException("O card não está bloqueado.");
            }
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                blockDAO.unblock(cardId, reason.trim());
                cardDAO.setBlocked(cardId, false);
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new BusinessException("Erro ao desbloquear card: " + e.getMessage());
        }
    }
}
