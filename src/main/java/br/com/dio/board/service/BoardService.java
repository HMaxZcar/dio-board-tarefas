package br.com.dio.board.service;

import br.com.dio.board.dao.BoardColumnDAO;
import br.com.dio.board.dao.BoardDAO;
import br.com.dio.board.exception.BusinessException;
import br.com.dio.board.model.Board;
import br.com.dio.board.model.BoardColumn;
import br.com.dio.board.model.BoardColumnType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BoardService {
    private final Connection connection;
    private final BoardDAO boardDAO;
    private final BoardColumnDAO columnDAO;

    public BoardService(Connection connection) {
        this.connection = connection;
        this.boardDAO = new BoardDAO(connection);
        this.columnDAO = new BoardColumnDAO(connection);
    }

    public Board create(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException("O nome do board é obrigatório.");
        }

        try {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                Board board = boardDAO.insert(name.trim());
                columnDAO.insert(board.getId(), "A Fazer", 1, BoardColumnType.INITIAL);
                columnDAO.insert(board.getId(), "Em Andamento", 2, BoardColumnType.PENDING);
                columnDAO.insert(board.getId(), "Concluído", 3, BoardColumnType.FINAL);
                columnDAO.insert(board.getId(), "Cancelado", 4, BoardColumnType.CANCEL);
                connection.commit();
                return board;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            throw new BusinessException("Erro ao criar board: " + e.getMessage());
        }
    }

    public List<Board> listAll() {
        try {
            return boardDAO.findAll();
        } catch (SQLException e) {
            throw new BusinessException("Erro ao listar boards: " + e.getMessage());
        }
    }

    public Board get(long id) {
        try {
            return boardDAO.findById(id)
                    .orElseThrow(() -> new BusinessException("Board não encontrado."));
        } catch (SQLException e) {
            throw new BusinessException("Erro ao consultar board: " + e.getMessage());
        }
    }

    public List<BoardColumn> getColumns(long boardId) {
        get(boardId);
        try {
            return columnDAO.findByBoardId(boardId);
        } catch (SQLException e) {
            throw new BusinessException("Erro ao listar colunas: " + e.getMessage());
        }
    }

    public void delete(long id) {
        get(id);
        try {
            boardDAO.delete(id);
        } catch (SQLException e) {
            throw new BusinessException("Erro ao excluir board: " + e.getMessage());
        }
    }
}
