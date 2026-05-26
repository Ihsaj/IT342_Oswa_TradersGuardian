package edu.cit.oswa.tradersguardian.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "account_settings")
public class AccountSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private double accountBalance = 10000.0;

    @Column(nullable = false)
    private double riskPerTrade = 2.0;   // percentage

    @Column(nullable = false)
    private double dailyLossLimit = 5.0; // percentage

    @Column(nullable = false)
    private double currentDailyLoss = 0.0; // percentage consumed today

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }

    public double getRiskPerTrade() { return riskPerTrade; }
    public void setRiskPerTrade(double riskPerTrade) { this.riskPerTrade = riskPerTrade; }

    public double getDailyLossLimit() { return dailyLossLimit; }
    public void setDailyLossLimit(double dailyLossLimit) { this.dailyLossLimit = dailyLossLimit; }

    public double getCurrentDailyLoss() { return currentDailyLoss; }
    public void setCurrentDailyLoss(double currentDailyLoss) { this.currentDailyLoss = currentDailyLoss; }
}
