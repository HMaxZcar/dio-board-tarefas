package br.com.dio.board.model;

public class BoardColumn {
    private Long id;
    private Long boardId;
    private String name;
    private int order;
    private BoardColumnType type;

    public BoardColumn() {
    }

    public BoardColumn(Long id, Long boardId, String name, int order, BoardColumnType type) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.order = order;
        this.type = type;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    public BoardColumnType getType() { return type; }
    public void setType(BoardColumnType type) { this.type = type; }

    @Override
    public String toString() {
        return order + " - " + name + " [" + type + "]";
    }
}
