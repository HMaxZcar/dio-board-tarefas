package br.com.dio.board.model;

import java.time.LocalDateTime;

public class Card {
    private Long id;
    private String title;
    private String description;
    private Long columnId;
    private boolean blocked;
    private LocalDateTime createdAt;

    public Card() {
    }

    public Card(Long id, String title, String description, Long columnId, boolean blocked, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.columnId = columnId;
        this.blocked = blocked;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getColumnId() { return columnId; }
    public void setColumnId(Long columnId) { this.columnId = columnId; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
