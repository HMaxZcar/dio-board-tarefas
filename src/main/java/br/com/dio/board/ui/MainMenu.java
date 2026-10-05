package br.com.dio.board.ui;

import br.com.dio.board.exception.BusinessException;
import br.com.dio.board.model.Board;
import br.com.dio.board.model.BoardColumn;
import br.com.dio.board.model.Card;
import br.com.dio.board.service.BoardService;
import br.com.dio.board.service.CardService;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final BoardService boardService;
    private final CardService cardService;

    public MainMenu(Connection connection) {
        this.boardService = new BoardService(connection);
        this.cardService = new CardService(connection);
    }

    public void execute() {
        boolean running = true;
        while (running) {
            try {
                printMainMenu();
                switch (readInt("Escolha uma opção: ")) {
                    case 1 -> createBoard();
                    case 2 -> selectBoard();
                    case 3 -> deleteBoard();
                    case 4 -> running = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (BusinessException e) {
                System.out.println("ERRO: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("ERRO INESPERADO: " + e.getMessage());
            }
        }
        System.out.println("Aplicação encerrada.");
    }

    private void printMainMenu() {
        System.out.println("\n===========================");
        System.out.println("      BOARD DE TAREFAS");
        System.out.println("===========================");
        System.out.println("1 - Criar novo board");
        System.out.println("2 - Selecionar board");
        System.out.println("3 - Excluir board");
        System.out.println("4 - Sair");
    }

    private void createBoard() {
        String name = readLine("Nome do board: ");
        Board board = boardService.create(name);
        System.out.println("Board criado com sucesso. ID: " + board.getId());
    }

    private void selectBoard() {
        List<Board> boards = boardService.listAll();
        if (boards.isEmpty()) {
            System.out.println("Nenhum board cadastrado.");
            return;
        }
        System.out.println("\nBoards disponíveis:");
        boards.forEach(board -> System.out.println(board.getId() + " - " + board.getName()));
        long id = readLong("ID do board: ");
        Board board = boardService.get(id);
        boardMenu(board);
    }

    private void deleteBoard() {
        List<Board> boards = boardService.listAll();
        if (boards.isEmpty()) {
            System.out.println("Nenhum board cadastrado.");
            return;
        }
        boards.forEach(board -> System.out.println(board.getId() + " - " + board.getName()));
        long id = readLong("ID do board que deseja excluir: ");
        String confirm = readLine("Confirma a exclusão? (S/N): ");
        if (confirm.equalsIgnoreCase("S")) {
            boardService.delete(id);
            System.out.println("Board excluído.");
        }
    }

    private void boardMenu(Board board) {
        boolean inside = true;
        while (inside) {
            try {
                System.out.println("\n===========================");
                System.out.println("BOARD: " + board.getName());
                System.out.println("===========================");
                System.out.println("1 - Criar card");
                System.out.println("2 - Mover card");
                System.out.println("3 - Bloquear card");
                System.out.println("4 - Desbloquear card");
                System.out.println("5 - Cancelar card");
                System.out.println("6 - Visualizar board");
                System.out.println("7 - Voltar");

                switch (readInt("Escolha uma opção: ")) {
                    case 1 -> createCard(board.getId());
                    case 2 -> moveCard(board.getId());
                    case 3 -> blockCard(board.getId());
                    case 4 -> unblockCard(board.getId());
                    case 5 -> cancelCard(board.getId());
                    case 6 -> showBoard(board.getId());
                    case 7 -> inside = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (BusinessException e) {
                System.out.println("ERRO: " + e.getMessage());
            }
        }
    }

    private void createCard(long boardId) {
        String title = readLine("Título: ");
        String description = readLine("Descrição: ");
        Card card = cardService.create(boardId, title, description);
        System.out.println("Card criado com ID: " + card.getId());
    }

    private void moveCard(long boardId) {
        showBoard(boardId);
        long cardId = readLong("ID do card a mover: ");
        ensureCardBelongsToBoard(cardId, boardId);
        cardService.moveToNextColumn(cardId);
        System.out.println("Card movido para a próxima coluna.");
    }

    private void blockCard(long boardId) {
        showBoard(boardId);
        long cardId = readLong("ID do card a bloquear: ");
        ensureCardBelongsToBoard(cardId, boardId);
        String reason = readLine("Motivo do bloqueio: ");
        cardService.block(cardId, reason);
        System.out.println("Card bloqueado.");
    }

    private void unblockCard(long boardId) {
        showBoard(boardId);
        long cardId = readLong("ID do card a desbloquear: ");
        ensureCardBelongsToBoard(cardId, boardId);
        String reason = readLine("Motivo do desbloqueio: ");
        cardService.unblock(cardId, reason);
        System.out.println("Card desbloqueado.");
    }

    private void cancelCard(long boardId) {
        showBoard(boardId);
        long cardId = readLong("ID do card a cancelar: ");
        ensureCardBelongsToBoard(cardId, boardId);
        cardService.cancel(cardId);
        System.out.println("Card cancelado.");
    }

    private void showBoard(long boardId) {
        List<BoardColumn> columns = boardService.getColumns(boardId);
        List<Card> cards = cardService.listByBoard(boardId);

        System.out.println("\n----------- BOARD -----------");
        for (BoardColumn column : columns) {
            System.out.println("\n[" + column.getName() + "]");
            List<Card> cardsInColumn = cards.stream()
                    .filter(card -> card.getColumnId().equals(column.getId()))
                    .toList();

            if (cardsInColumn.isEmpty()) {
                System.out.println("  (sem cards)");
            } else {
                for (Card card : cardsInColumn) {
                    System.out.printf("  #%d - %s%s%n",
                            card.getId(),
                            card.getTitle(),
                            card.isBlocked() ? " [BLOQUEADO]" : "");
                    if (card.getDescription() != null && !card.getDescription().isBlank()) {
                        System.out.println("       " + card.getDescription());
                    }
                }
            }
        }
        System.out.println("\n-----------------------------");
    }

    private void ensureCardBelongsToBoard(long cardId, long boardId) {
        Card card = cardService.get(cardId);
        BoardColumn column = boardService.getColumns(boardId).stream()
                .filter(c -> c.getId().equals(card.getColumnId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("O card informado não pertence a este board."));
        if (column.getBoardId() != boardId) {
            throw new BusinessException("O card informado não pertence a este board.");
        }
    }

    private int readInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(readLine(message));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private long readLong(String message) {
        while (true) {
            try {
                return Long.parseLong(readLine(message));
            } catch (NumberFormatException e) {
                System.out.println("Digite um ID válido.");
            }
        }
    }

    private String readLine(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
}
