package edu.cit.oswa.tradersguardian.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_plans")
public class TradePlan {

    public enum Status {
        PENDING, APPROVED, DISAPPROVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private String tradeType; // BUY / SELL

    @Column(nullable = false)
    private double entryPrice;

    @Column(nullable = false)
    private double stopLoss;

    @Column(nullable = false)
    private double takeProfit;

    @Column(nullable = false)
    private double positionSize;

    @Column(nullable = false)
    private double riskAmount;

    @Column(nullable = false)
    private double riskPercent;

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    private String disapprovalReason;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public double getEntryPrice() { return entryPrice; }
    public void setEntryPrice(double entryPrice) { this.entryPrice = entryPrice; }

    public double getStopLoss() { return stopLoss; }
    public void setStopLoss(double stopLoss) { this.stopLoss = stopLoss; }

    public double getTakeProfit() { return takeProfit; }
    public void setTakeProfit(double takeProfit) { this.takeProfit = takeProfit; }

    public double getPositionSize() { return positionSize; }
    public void setPositionSize(double positionSize) { this.positionSize = positionSize; }

    public double getRiskAmount() { return riskAmount; }
    public void setRiskAmount(double riskAmount) { this.riskAmount = riskAmount; }

    public double getRiskPercent() { return riskPercent; }
    public void setRiskPercent(double riskPercent) { this.riskPercent = riskPercent; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getDisapprovalReason() { return disapprovalReason; }
    public void setDisapprovalReason(String disapprovalReason) { this.disapprovalReason = disapprovalReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
