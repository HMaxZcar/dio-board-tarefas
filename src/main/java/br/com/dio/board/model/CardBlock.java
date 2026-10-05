package br.com.dio.board.model;

import java.time.LocalDateTime;

public class CardBlock {
    private Long id;
    private Long cardId;
    private LocalDateTime blockedAt;
    private String blockReason;
    private LocalDateTime unblockedAt;
    private String unblockReason;

    public CardBlock() {
    }

    public CardBlock(Long id, Long cardId, LocalDateTime blockedAt, String blockReason,
                     LocalDateTime unblockedAt, String unblockReason) {
        this.id = id;
        this.cardId = cardId;
        this.blockedAt = blockedAt;
        this.blockReason = blockReason;
        this.unblockedAt = unblockedAt;
        this.unblockReason = unblockReason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public LocalDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }
    public String getBlockReason() { return blockReason; }
    public void setBlockReason(String blockReason) { this.blockReason = blockReason; }
    public LocalDateTime getUnblockedAt() { return unblockedAt; }
    public void setUnblockedAt(LocalDateTime unblockedAt) { this.unblockedAt = unblockedAt; }
    public String getUnblockReason() { return unblockReason; }
    public void setUnblockReason(String unblockReason) { this.unblockReason = unblockReason; }
}
